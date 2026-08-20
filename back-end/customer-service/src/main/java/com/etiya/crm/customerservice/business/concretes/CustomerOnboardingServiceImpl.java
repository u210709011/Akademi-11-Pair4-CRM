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
/** Müşteri, hesap ve iletişim bilgilerinin birlikte oluşturulmasını yönetir. */
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
	/** Önce doğum tarihi, kimlik doğrulama ve tekillik kontrollerini yapar. */
	public IdentityVerificationResponse verifyIdentity(IndividualInfo individual) {
		identityRules.validateBirthDate(individual.birthDate());
		identityVerificationService.verify(individual); 
		identityRules.ensureUniqueNationalId(partyClient.existsByNationalId(individual.nationalId()));
		return IdentityVerificationResponse.ok();
	}

	@Override
	@Transactional
	/** Dağıtık oluşturma akışını yürütür ve başarısız adımları telafi eder. */
	public CustomerResponse onboard(OnboardCustomerRequest request) {
		
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
		// Hesap numarası, veritabanı kimliği oluştuktan sonra üretilir.
		Customer customer = new Customer();
		customer.setPartyRoleId(partyRoleId);

		customer.setCustTpId(lookupResolver.resolveIndividualCustomerTypeId());
		customer = customerRepository.save(customer); 

		CustomerAccount account = new CustomerAccount();
		account.setCustomer(customer);
		account.setAccountTpId(lookupResolver.resolveCustomerAccountTypeId());
		account.setAcctStId(lookupResolver.resolveActiveAccountStatusId());

		account.setAccountNo(UUID.randomUUID().toString());
		account = customerAccountRepository.save(account);
		account.setAccountNo(accountNumberGenerator.generate(account.getCustAcctId()));
		account = customerAccountRepository.save(account);

		customer.getAccounts().add(account);
		return customer;
	}


	private void compensateContactInfo(Long custId) {
		// Contact servisindeki kısmi kaydı onboarding'i geri alırken temizler.
		try {
			contactAddressClient.deleteByCustomerId(custId, lookupResolver.resolveCustomerDataTypeId());
		} catch (Exception ex) {
			log.error(LogMessages.ONBOARDING_CONTACT_COMPENSATION_FAILED, custId, ex);
		}
	}


	private void createSearchView(Customer customer, IndividualInfo individual, ContactInfo contact,
			String accountNo) {
		// Arama ekranı için gerekli alanları yerel read-model'e kopyalar.
		CustomerSearchView view = new CustomerSearchView();
		view.setCustId(customer.getCustId());
		view.setPartyRoleId(customer.getPartyRoleId());
		view.setFirstName(individual.firstName());
		view.setMiddleName(individual.middleName());
		view.setLastName(individual.lastName());
		view.setTcNo(individual.nationalId());
		view.setGsm(contact.mobilePhone());
		view.setAcctNo(accountNo);
		Long customerRoleTypeId = lookupResolver.resolveCustomerRoleTypeId();
		view.setRole(lookupCacheService.resolveTypeValue(customerRoleTypeId));
		view.setPartyRoleTypeId(customerRoleTypeId);
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
