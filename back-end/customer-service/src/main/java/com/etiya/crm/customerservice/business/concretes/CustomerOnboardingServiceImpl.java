package com.etiya.crm.customerservice.business.concretes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.etiya.crm.customerservice.business.abstracts.CustomerOnboardingService;
import com.etiya.crm.customerservice.business.abstracts.CustomerLookupResolver;
import com.etiya.crm.customerservice.business.abstracts.IdentityVerificationService;
import com.etiya.crm.customerservice.business.abstracts.LookupCacheService;
import com.etiya.crm.customerservice.business.dtos.requests.ContactInfo;
import com.etiya.crm.customerservice.business.dtos.requests.IndividualInfo;
import com.etiya.crm.customerservice.business.dtos.requests.OnboardCustomerRequest;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerResponse;
import com.etiya.crm.customerservice.business.dtos.responses.IdentityVerificationResponse;
import com.etiya.crm.customerservice.business.exceptions.OnboardingFailedException;
import com.etiya.crm.customerservice.business.rules.AddressBusinessRules;
import com.etiya.crm.customerservice.business.rules.IdentityValidationRules;
import com.etiya.crm.customerservice.clients.controllers.ContactAddressClient;
import com.etiya.crm.customerservice.clients.controllers.PartyClient;
import com.etiya.crm.customerservice.constants.LogMessages;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerAccountRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerSearchViewRepository;
import com.etiya.crm.customerservice.entities.concretes.Customer;
import com.etiya.crm.customerservice.entities.concretes.CustomerAccount;
import com.etiya.crm.customerservice.entities.concretes.CustomerSearchView;
import com.etiya.crm.customerservice.mapper.CustomerMapper;
import com.etiya.crm.shared.contracts.contactmedium.ContactMediumCommand;
import com.etiya.crm.shared.contracts.contactmedium.CreateContactCommand;
import com.etiya.crm.shared.contracts.gnltp.GnlTpCodes;
import com.etiya.crm.shared.contracts.individual.CreateIndividualCommand;
import com.etiya.crm.shared.contracts.individual.PartyRoleResponse;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.customer.CustomerEventTypes;
import com.etiya.crm.shared.events.customer.CustomerOnboardedEvent;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerOnboardingServiceImpl implements CustomerOnboardingService {

	private final CustomerRepository customerRepository;
	private final CustomerAccountRepository customerAccountRepository;
	private final CustomerSearchViewRepository customerSearchViewRepository;
	private final PartyClient partyClient;
	private final ContactAddressClient contactAddressClient;
	private final IdentityVerificationService identityVerificationService;
	private final IdentityValidationRules identityRules;
	private final AddressBusinessRules addressRules;
	private final CustomerLookupResolver lookupResolver;
	private final CustomerMapper customerMapper;
	private final OutboxEventPublisher outboxEventPublisher;
	private final AccountNumberGenerator accountNumberGenerator;
	private final LookupCacheService lookupCacheService;

	@Override
	public IdentityVerificationResponse verifyIdentity(IndividualInfo individual) {
		identityRules.validateBirthDate(individual.birthDate());
		identityVerificationService.verify(individual); // ACC-009/010 (fake KPS)
		identityRules.ensureUniqueNationalId(partyClient.existsByNationalId(individual.nationalId())); // ACC-011/012
		return IdentityVerificationResponse.ok();
	}

	@Override
	@Transactional
	public CustomerResponse onboard(OnboardCustomerRequest request) {
		// ACC-023: Create'e basildiginda ayni dogrulamalar tekrar calisir (defense in depth).
		verifyIdentity(request.individual());

		PartyRoleResponse partyRole = partyClient.createIndividualWithRole(toIndividualCommand(request.individual()));

		try {
			Customer customer = createCustomerWithDefaultAccount(partyRole.partyRoleId());
			CustomerAccount account = customer.getAccounts().get(0);
			createSearchView(customer, request.individual(), request.contact(), account.getAccountNo());

			try {
				contactAddressClient.createContact(toContactCommand(customer.getCustId(), request));
			} catch (Exception ex) {
				log.error(LogMessages.ONBOARDING_CONTACT_FAILED, customer.getCustId(), ex);
				// Local customer/account/search-view yazimlari icin ayrica bir telafi GEREKMEZ:
				// throw ex bu metodun @Transactional sinirini asip tum transaction'i rollback
				// ettirir. Sadece contact-info-service (ayri DB, ayri transaction) icin telafi gerekli.
				compensateContactInfo(customer.getCustId());
				throw ex;
			}

			outboxEventPublisher.publish(KafkaTopics.CUSTOMER_AGGREGATE_TYPE, customer.getCustId().toString(),
					CustomerEventTypes.CUSTOMER_ONBOARDED,
					new CustomerOnboardedEvent(UUID.randomUUID(), CustomerEventTypes.CUSTOMER_ONBOARDED,
							customer.getCustId(), customer.getPartyRoleId()));

			return customerMapper.toResponse(customer, List.of(account), lookupResolver.resolveActiveAccountStatusId());
		} catch (Exception ex) {
			log.error(LogMessages.ONBOARDING_FAILED_COMPENSATING_PARTY, partyRole.partyId(), ex);
			try {
				partyClient.deleteParty(partyRole.partyId());
			} catch (Exception compensationEx) {
				log.error(LogMessages.ONBOARDING_PARTY_COMPENSATION_FAILED, partyRole.partyId(), compensationEx);
			}
			throw new OnboardingFailedException(ex);
		}
	}

	private Customer createCustomerWithDefaultAccount(Long partyRoleId) {
		Customer customer = new Customer();
		customer.setPartyRoleId(partyRoleId);
		// onboard() sadece bireysel musteri akisidir (party-service'e createIndividualWithRole
		// cagrilir) - kurumsal onboarding henuz yok, bu yuzden CORPORATE_CUSTOMER burada hic
		// kullanilmaz.
		customer.setCustTpId(lookupResolver.resolveIndividualCustomerTypeId());
		customer = customerRepository.save(customer); // IDENTITY: save sonrasi custId dolu gelir.

		// ACC-025: musteri olusturulurken otomatik olarak varsayilan tipte tek bir hesap acilir.
		CustomerAccount account = new CustomerAccount();
		account.setCustomer(customer);
		account.setAccountTpId(lookupResolver.resolveCustomerAccountTypeId());
		account.setAcctStId(lookupResolver.resolveActiveAccountStatusId());
		// acct_no NOT NULL+UNIQUE oldugu icin gecici bir deger ile ilk kayit yapilir, IDENTITY'den
		// donen custAcctId ile asil numara ikinci kayitta yazilir - B-06: onceden burada custId
		// kullaniliyordu, cust_acct ile ayni sequence olmadigi icin billing account'larla (custAcctId
		// kullanan) cakisip acct_no UNIQUE constraint'ini kirabiliyordu (bkz. AccountNumberGenerator).
		account.setAccountNo(UUID.randomUUID().toString());
		account = customerAccountRepository.save(account);
		account.setAccountNo(accountNumberGenerator.generate(account.getCustAcctId()));
		account = customerAccountRepository.save(account);

		customer.getAccounts().add(account);
		return customer;
	}

	/**
	 * createContact basarisiz olsa bile contact-info-service tarafinda kismen commit edilmis
	 * olabilir (orn. adresler yazildi ama yanit deserialize edilirken/timeout'ta hata olustu) -
	 * bu satirlar aksi halde hic temizlenmezdi (ContactAddressClient.deleteByCustomerId tam da
	 * bunun icin var ama onboarding hicbir zaman cagirmiyordu). Best-effort: bu cagri basarisiz
	 * olsa da asil onboarding hatasini maskelememesi icin sadece loglanir, yeniden firlatilmaz.
	 */
	private void compensateContactInfo(Long custId) {
		try {
			contactAddressClient.deleteByCustomerId(custId, lookupResolver.resolveCustomerDataTypeId());
		} catch (Exception ex) {
			log.error(LogMessages.ONBOARDING_CONTACT_COMPENSATION_FAILED, custId, ex);
		}
	}

	/**
	 * firstName/middleName/lastName/tcNo/gsm burada senkron yazilir: hepsi bu
	 * istekte customer-service'e caller tarafindan verilen degerlerdir, baska
	 * bir servisin karari degildir. role de artik burada senkron yazilir -
	 * onceden SADECE PartyEventListener'in async tuketecegi IndividualPartyCreated
	 * event'i ile dolduruluyordu; event kaybolursa/lookup-service cagrisi basarisiz
	 * olup 4 denemeden sonra DLQ'ya duserse role kalici olarak null kaliyordu, hicbir
	 * hata da gorunmuyordu. onboard() sadece bireysel akis oldugundan ve party-service
	 * her bireysel musteriye SABIT olarak ayni rolu (CUSTOMER_ROLE) atadigindan
	 * (bkz. party-service IndividualManager), bu deger onboarding aninda zaten
	 * biliniyor - async event'i beklemeye gerek yok. PartyEventListener, gelecekte
	 * rol degisirse diye (INDIVIDUAL_UPDATED) hala calismaya devam ediyor.
	 */
	private void createSearchView(Customer customer, IndividualInfo individual, ContactInfo contact,
			String accountNo) {
		CustomerSearchView view = new CustomerSearchView();
		view.setCustId(customer.getCustId());
		view.setPartyRoleId(customer.getPartyRoleId());
		view.setFirstName(individual.firstName());
		view.setMiddleName(individual.middleName());
		view.setLastName(individual.lastName());
		view.setTcNo(individual.nationalId());
		view.setGsm(contact.mobilePhone());
		view.setAcctNo(accountNo);
		view.setRole(lookupCacheService.resolveTypeValue(lookupResolver.resolveCustomerRoleTypeId()));
		view.setStatus("ACTIVE");
		view.setDeleted(false);
		customerSearchViewRepository.save(view);
	}

	private CreateIndividualCommand toIndividualCommand(IndividualInfo individual) {
		return new CreateIndividualCommand(individual.firstName(), individual.middleName(), individual.lastName(),
				individual.birthDate(), individual.genderId(), individual.motherName(), individual.fatherName(),
				individual.nationalId());
	}

	private CreateContactCommand toContactCommand(Long custId, OnboardCustomerRequest request) {
		return new CreateContactCommand(custId, lookupResolver.resolveCustomerDataTypeId(),
				addressRules.toAddressCommandsWithPrimaryRule(request.addresses()), toContactMediumCommands(request.contact()));
	}

	private List<ContactMediumCommand> toContactMediumCommands(ContactInfo contact) {
		List<ContactMediumCommand> mediums = new ArrayList<>();
		mediums.add(new ContactMediumCommand(lookupResolver.resolveContactMediumTypeId(GnlTpCodes.EMAIL),
				contact.email()));
		mediums.add(new ContactMediumCommand(lookupResolver.resolveContactMediumTypeId(GnlTpCodes.MOBILE),
				contact.mobilePhone()));
		if (StringUtils.hasText(contact.homePhone())) {
			mediums.add(new ContactMediumCommand(lookupResolver.resolveContactMediumTypeId(GnlTpCodes.LANDLINE),
					contact.homePhone()));
		}
		if (StringUtils.hasText(contact.fax())) {
			mediums.add(new ContactMediumCommand(lookupResolver.resolveContactMediumTypeId(GnlTpCodes.FAX),
					contact.fax()));
		}
		return mediums;
	}
}
