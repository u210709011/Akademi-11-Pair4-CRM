package com.etiya.crm.customerservice.business.concretes;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.etiya.crm.customerservice.business.abstracts.CustomerService;
import com.etiya.crm.customerservice.business.abstracts.IdentityVerificationService;
import com.etiya.crm.customerservice.business.abstracts.LookupCacheService;
import com.etiya.crm.customerservice.business.dtos.requests.AddressEditRequest;
import com.etiya.crm.customerservice.business.dtos.requests.ContactInfo;
import com.etiya.crm.customerservice.business.dtos.requests.CreateBillingAccountRequest;
import com.etiya.crm.customerservice.business.dtos.requests.CustomerSearchRequest;
import com.etiya.crm.customerservice.business.dtos.requests.IndividualInfo;
import com.etiya.crm.customerservice.business.dtos.requests.OnboardCustomerRequest;
import com.etiya.crm.customerservice.business.dtos.requests.UpdateIndividualInfo;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerAccountResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerSearchResponse;
import com.etiya.crm.customerservice.business.dtos.responses.IdentityVerificationResponse;
import com.etiya.crm.customerservice.business.exceptions.CustomerNotFoundException;
import com.etiya.crm.customerservice.business.exceptions.OnboardingFailedException;
import com.etiya.crm.customerservice.business.rules.CustomerBusinessRules;
import com.etiya.crm.customerservice.clients.commands.ContactMediumCommand;
import com.etiya.crm.customerservice.clients.commands.CreateAddressCommand;
import com.etiya.crm.customerservice.clients.commands.CreateContactCommand;
import com.etiya.crm.customerservice.clients.commands.CreateContactMediumCommand;
import com.etiya.crm.customerservice.clients.commands.CreateIndividualCommand;
import com.etiya.crm.customerservice.clients.commands.UpdateAddressCommand;
import com.etiya.crm.customerservice.clients.commands.UpdateContactMediumCommand;
import com.etiya.crm.customerservice.clients.commands.UpdateIndividualCommand;
import com.etiya.crm.customerservice.clients.controllers.ContactAddressClient;
import com.etiya.crm.customerservice.clients.controllers.PartyClient;
import com.etiya.crm.customerservice.clients.responses.AddressResponse;
import com.etiya.crm.customerservice.clients.responses.ContactMediumResponse;
import com.etiya.crm.customerservice.clients.responses.IndividualResponse;
import com.etiya.crm.customerservice.clients.responses.PartyRoleResponse;
import com.etiya.crm.customerservice.constants.AccountDefaults;
import com.etiya.crm.customerservice.constants.CacheNames;
import com.etiya.crm.customerservice.constants.DefaultLookupValues;
import com.etiya.crm.customerservice.constants.LogMessages;
import com.etiya.crm.customerservice.constants.LookupCodes;
import com.etiya.crm.customerservice.constants.LookupTpGroups;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerAccountRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerSearchSpecifications;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerSearchViewRepository;
import com.etiya.crm.customerservice.entities.concretes.Customer;
import com.etiya.crm.customerservice.entities.concretes.CustomerAccount;
import com.etiya.crm.customerservice.entities.concretes.CustomerSearchView;
import com.etiya.crm.customerservice.mapper.CustomerMapper;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.customer.CustomerDeletedEvent;
import com.etiya.crm.shared.events.customer.CustomerEventTypes;
import com.etiya.crm.shared.events.customer.CustomerOnboardedEvent;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepository customerRepository;
	private final CustomerAccountRepository customerAccountRepository;
	private final CustomerSearchViewRepository customerSearchViewRepository;
	private final PartyClient partyClient;
	private final ContactAddressClient contactAddressClient;
	private final LookupCacheService lookupCacheService;
	private final IdentityVerificationService identityVerificationService;
	private final CustomerBusinessRules rules;
	private final CustomerMapper customerMapper;
	private final OutboxEventPublisher outboxEventPublisher;

	@Override
	public IdentityVerificationResponse verifyIdentity(IndividualInfo individual) {
		rules.validateBirthDate(individual.birthDate());
		identityVerificationService.verify(individual); // ACC-009/010 (fake KPS)
		rules.ensureUniqueNationalId(partyClient.existsByNationalId(individual.nationalId())); // ACC-011/012
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
			createSearchView(customer, request.individual(), account.getAccountNo());

			try {
				contactAddressClient.createContact(toContactCommand(customer.getCustId(), request));
			} catch (Exception ex) {
				log.error(LogMessages.ONBOARDING_CONTACT_FAILED, customer.getCustId(), ex);
				compensateCustomer(customer.getCustId());
				throw ex;
			}

			outboxEventPublisher.publish(KafkaTopics.CUSTOMER_AGGREGATE_TYPE, customer.getCustId().toString(),
					CustomerEventTypes.CUSTOMER_ONBOARDED,
					new CustomerOnboardedEvent(UUID.randomUUID(), CustomerEventTypes.CUSTOMER_ONBOARDED,
							customer.getCustId(), customer.getPartyRoleId()));

			return customerMapper.toResponse(customer, List.of(account));
		} catch (Exception ex) {
			log.error(LogMessages.ONBOARDING_FAILED_COMPENSATING_PARTY, partyRole.partyId(), ex);
			partyClient.deleteParty(partyRole.partyId());
			throw new OnboardingFailedException(ex);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CustomerSearchResponse> search(CustomerSearchRequest request) {
		return customerSearchViewRepository
				.findAll(CustomerSearchSpecifications.search(request.firstName(), request.lastName(),
						request.tcNo(), request.acctNo()))
				.stream()
				.map(customerMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheManager = CacheNames.REDIS_CACHE_MANAGER, cacheNames = CacheNames.CUSTOMERS, key = "#custId")
	public CustomerResponse getById(Long custId) {
		Customer customer = getActiveCustomerOrThrow(custId);
		List<CustomerAccount> accounts = customerAccountRepository.findByCustomer_CustIdAndActiveTrue(custId);
		return customerMapper.toResponse(customer, accounts);
	}

	@Override
	@Transactional
	@CacheEvict(cacheManager = CacheNames.REDIS_CACHE_MANAGER, cacheNames = CacheNames.CUSTOMERS, key = "#custId")
	public void softDelete(Long custId) {
		Customer customer = getActiveCustomerOrThrow(custId);
		customer.setActive(false);
		customerRepository.save(customer);
		customerAccountRepository.softDeleteByCustId(custId);
		customerSearchViewRepository.findById(custId).ifPresent(view -> {
			view.setDeleted(true);
			customerSearchViewRepository.save(view);
		});

		outboxEventPublisher.publish(KafkaTopics.CUSTOMER_AGGREGATE_TYPE, custId.toString(),
				CustomerEventTypes.CUSTOMER_DELETED,
				new CustomerDeletedEvent(UUID.randomUUID(), CustomerEventTypes.CUSTOMER_DELETED, custId,
						customer.getPartyRoleId()));
	}

	@Override
	@Transactional(readOnly = true)
	public IndividualResponse getIndividual(Long custId) {
		Customer customer = getActiveCustomerOrThrow(custId);
		return partyClient.getIndividualByPartyRoleId(customer.getPartyRoleId());
	}

	@Override
	@Transactional(readOnly = true)
	public IndividualResponse updateIndividual(Long custId, UpdateIndividualInfo request) {
		Customer customer = getActiveCustomerOrThrow(custId);
		UpdateIndividualCommand command = new UpdateIndividualCommand(request.firstName(), request.middleName(),
				request.lastName(), request.genderId(), request.motherName(), request.fatherName());
		// CustomerSearchView senkronu burada YAPILMAZ: party-service'in yayinlayacagi
		// IndividualUpdated event'i PartyEventListener tarafindan async islenir.
		return partyClient.updateIndividual(customer.getPartyRoleId(), command);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AddressResponse> getAddresses(Long custId) {
		getActiveCustomerOrThrow(custId);
		return contactAddressClient.getAddressesByCustomer(custId, resolveCustomerDataTypeId());
	}

	@Override
	@Transactional(readOnly = true)
	public AddressResponse addAddress(Long custId, AddressEditRequest request) {
		getActiveCustomerOrThrow(custId);
		Long dataTypeId = resolveCustomerDataTypeId();
		List<AddressResponse> existing = contactAddressClient.getAddressesByCustomer(custId, dataTypeId);
		rules.validateAddressLimit(existing.size());

		CreateAddressCommand command = new CreateAddressCommand(custId, dataTypeId, request.cityId(),
				request.streetName(), request.buildingName(), request.addressDesc(), request.primary());
		return contactAddressClient.addAddress(command);
	}

	@Override
	@Transactional(readOnly = true)
	public AddressResponse updateAddress(Long custId, Long addressId, AddressEditRequest request) {
		getActiveCustomerOrThrow(custId);
		List<AddressResponse> existing = contactAddressClient.getAddressesByCustomer(custId, resolveCustomerDataTypeId());
		rules.ensureAddressBelongsToCustomer(custId, addressId, existing);

		UpdateAddressCommand command = new UpdateAddressCommand(request.cityId(), request.streetName(),
				request.buildingName(), request.addressDesc(), request.primary());
		return contactAddressClient.updateAddress(addressId, command);
	}

	@Override
	@Transactional(readOnly = true)
	public ContactInfo getContact(Long custId) {
		getActiveCustomerOrThrow(custId);
		List<ContactMediumResponse> mediums = contactAddressClient.getContactMediumsByCustomer(custId,
				resolveCustomerDataTypeId());
		return new ContactInfo(
				findMediumValue(mediums, resolveContactMediumTypeId(LookupCodes.CONTACT_MEDIUM_EMAIL)),
				findMediumValue(mediums, resolveContactMediumTypeId(LookupCodes.CONTACT_MEDIUM_MOBILE_PHONE)),
				findMediumValue(mediums, resolveContactMediumTypeId(LookupCodes.CONTACT_MEDIUM_HOME_PHONE)),
				findMediumValue(mediums, resolveContactMediumTypeId(LookupCodes.CONTACT_MEDIUM_FAX)));
	}

	@Override
	@Transactional(readOnly = true)
	public ContactInfo updateContact(Long custId, ContactInfo request) {
		getActiveCustomerOrThrow(custId);
		Long dataTypeId = resolveCustomerDataTypeId();
		List<ContactMediumResponse> existing = contactAddressClient.getContactMediumsByCustomer(custId, dataTypeId);

		ContactMediumResponse email = upsertMedium(custId, dataTypeId, existing,
				resolveContactMediumTypeId(LookupCodes.CONTACT_MEDIUM_EMAIL), request.email(), true);
		ContactMediumResponse mobile = upsertMedium(custId, dataTypeId, existing,
				resolveContactMediumTypeId(LookupCodes.CONTACT_MEDIUM_MOBILE_PHONE), request.mobilePhone(), true);
		ContactMediumResponse home = upsertMedium(custId, dataTypeId, existing,
				resolveContactMediumTypeId(LookupCodes.CONTACT_MEDIUM_HOME_PHONE), request.homePhone(), false);
		ContactMediumResponse fax = upsertMedium(custId, dataTypeId, existing,
				resolveContactMediumTypeId(LookupCodes.CONTACT_MEDIUM_FAX), request.fax(), false);

		return new ContactInfo(email.cntcData(), mobile.cntcData(), home != null ? home.cntcData() : null,
				fax != null ? fax.cntcData() : null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CustomerAccountResponse> getAccounts(Long custId) {
		getActiveCustomerOrThrow(custId);
		return customerAccountRepository.findByCustomer_CustIdAndActiveTrue(custId).stream()
				.map(customerMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional
	public CustomerAccountResponse createBillingAccount(Long custId, CreateBillingAccountRequest request) {
		Customer customer = getActiveCustomerOrThrow(custId);
		rules.ensureAddressProvided(request.addressId(), request.newAddress());

		Long addressId = resolveBillingAddressId(custId, request);

		CustomerAccount account = new CustomerAccount();
		account.setCustomer(customer);
		account.setAccountName(request.accountName());
		account.setAccountDesc(request.accountDesc());
		account.setAccountTpId(DefaultLookupValues.BILLING_ACCOUNT_TYPE_ID);
		account.setAddressId(addressId);
		// acct_no NOT NULL+UNIQUE oldugu icin gecici bir deger ile ilk kayit yapilir,
		// IDENTITY'den donen custAcctId ile asil numara ikinci kayitta yazilir.
		account.setAccountNo(UUID.randomUUID().toString());
		account = customerAccountRepository.save(account);
		account.setAccountNo(AccountDefaults.ACCOUNT_NO_PREFIX + account.getCustAcctId());
		account = customerAccountRepository.save(account);

		return customerMapper.toResponse(account);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsAccountByAddressId(Long addressId) {
		return customerAccountRepository.existsByAddressIdAndActiveTrue(addressId);
	}

	private Long resolveBillingAddressId(Long custId, CreateBillingAccountRequest request) {
		Long dataTypeId = resolveCustomerDataTypeId();
		if (request.newAddress() != null) {
			CreateAddressCommand command = new CreateAddressCommand(custId, dataTypeId, request.newAddress().cityId(),
					request.newAddress().streetName(), request.newAddress().buildingName(),
					request.newAddress().addressDesc(), false);
			return contactAddressClient.addAddress(command).id();
		}
		List<AddressResponse> existing = contactAddressClient.getAddressesByCustomer(custId, dataTypeId);
		rules.ensureAddressBelongsToCustomer(custId, request.addressId(), existing);
		return request.addressId();
	}

	/**
	 * required=false alanlar (homePhone/fax) icin: mevcut kayit varsa ve yeni
	 * deger bossa dokunulmaz (silme desteklenmiyor); mevcut kayit yoksa ve deger
	 * de bossa hicbir sey yapilmaz (null doner).
	 */
	private ContactMediumResponse upsertMedium(Long custId, Long dataTypeId, List<ContactMediumResponse> existing,
			Long typeId, String value, boolean required) {
		ContactMediumResponse current = existing.stream()
				.filter(medium -> medium.cntcMediumTypeId().equals(typeId))
				.findFirst()
				.orElse(null);

		if (current != null) {
			if (!required && !StringUtils.hasText(value)) {
				return current;
			}
			UpdateContactMediumCommand command = new UpdateContactMediumCommand(value, typeId);
			return contactAddressClient.updateContactMedium(current.id(), command);
		}

		if (!StringUtils.hasText(value)) {
			return null;
		}

		CreateContactMediumCommand command = new CreateContactMediumCommand(custId, dataTypeId, value, typeId);
		return contactAddressClient.addContactMedium(command);
	}

	private String findMediumValue(List<ContactMediumResponse> mediums, Long typeId) {
		return mediums.stream()
				.filter(medium -> medium.cntcMediumTypeId().equals(typeId))
				.map(ContactMediumResponse::cntcData)
				.findFirst()
				.orElse(null);
	}

	private Long resolveCustomerDataTypeId() {
		return lookupCacheService.resolveTpId(LookupTpGroups.DATA_TYPE, LookupCodes.DATA_TYPE_CUSTOMER);
	}

	private Long resolveContactMediumTypeId(String code) {
		return lookupCacheService.resolveTpId(LookupTpGroups.CONTACT_MEDIUM_TYPE, code);
	}

	private Customer getActiveCustomerOrThrow(Long custId) {
		return customerRepository.findByCustIdAndActiveTrue(custId)
				.orElseThrow(() -> new CustomerNotFoundException(custId));
	}

	private Customer createCustomerWithDefaultAccount(Long partyRoleId) {
		Customer customer = new Customer();
		customer.setPartyRoleId(partyRoleId);
		customer = customerRepository.save(customer); // IDENTITY: save sonrasi custId dolu gelir.

		// ACC-025: musteri olusturulurken otomatik olarak varsayilan tipte tek bir hesap acilir.
		CustomerAccount account = new CustomerAccount();
		account.setCustomer(customer);
		account.setAccountNo(AccountDefaults.ACCOUNT_NO_PREFIX + customer.getCustId());
		account.setAccountTpId(DefaultLookupValues.DEFAULT_ACCOUNT_TYPE_ID);
		account = customerAccountRepository.save(account);

		customer.getAccounts().add(account);
		return customer;
	}

	private void compensateCustomer(Long custId) {
		customerAccountRepository.softDeleteByCustId(custId);
		customerSearchViewRepository.deleteById(custId);
		customerRepository.findByCustIdAndActiveTrue(custId).ifPresent(customer -> {
			customer.setActive(false);
			customerRepository.save(customer);
		});
	}

	private void createSearchView(Customer customer, IndividualInfo individual, String accountNo) {
		CustomerSearchView view = new CustomerSearchView();
		view.setCustId(customer.getCustId());
		view.setPartyRoleId(customer.getPartyRoleId());
		view.setFirstName(individual.firstName());
		view.setLastName(individual.lastName());
		view.setTcNo(individual.nationalId());
		view.setAcctNo(accountNo);
		view.setStatus(LookupCodes.STATUS_ACTIVE);
		view.setDeleted(false);
		customerSearchViewRepository.save(view);
	}

	private CreateIndividualCommand toIndividualCommand(IndividualInfo individual) {
		return new CreateIndividualCommand(individual.firstName(), individual.middleName(), individual.lastName(),
				individual.birthDate(), individual.genderId(), individual.motherName(), individual.fatherName(),
				individual.nationalId());
	}

	private CreateContactCommand toContactCommand(Long custId, OnboardCustomerRequest request) {
		return new CreateContactCommand(custId, rules.toAddressCommandsWithPrimaryRule(request.addresses()),
				toContactMediumCommands(request.contact()));
	}

	private List<ContactMediumCommand> toContactMediumCommands(ContactInfo contact) {
		List<ContactMediumCommand> mediums = new ArrayList<>();
		mediums.add(new ContactMediumCommand(
				lookupCacheService.resolveTpId(LookupTpGroups.CONTACT_MEDIUM_TYPE, LookupCodes.CONTACT_MEDIUM_EMAIL),
				contact.email()));
		mediums.add(new ContactMediumCommand(
				lookupCacheService.resolveTpId(LookupTpGroups.CONTACT_MEDIUM_TYPE,
						LookupCodes.CONTACT_MEDIUM_MOBILE_PHONE),
				contact.mobilePhone()));
		if (StringUtils.hasText(contact.homePhone())) {
			mediums.add(new ContactMediumCommand(
					lookupCacheService.resolveTpId(LookupTpGroups.CONTACT_MEDIUM_TYPE,
							LookupCodes.CONTACT_MEDIUM_HOME_PHONE),
					contact.homePhone()));
		}
		if (StringUtils.hasText(contact.fax())) {
			mediums.add(new ContactMediumCommand(
					lookupCacheService.resolveTpId(LookupTpGroups.CONTACT_MEDIUM_TYPE, LookupCodes.CONTACT_MEDIUM_FAX),
					contact.fax()));
		}
		return mediums;
	}
}
