package com.etiya.crm.customerservice.business.concretes;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.etiya.crm.customerservice.business.abstracts.CustomerService;
import com.etiya.crm.customerservice.business.abstracts.IdentityVerificationService;
import com.etiya.crm.customerservice.business.abstracts.LookupCacheService;
import com.etiya.crm.customerservice.business.dtos.requests.AddressEditRequest;
import com.etiya.crm.customerservice.business.dtos.requests.AddressInfo;
import com.etiya.crm.customerservice.business.dtos.requests.ContactInfo;
import com.etiya.crm.customerservice.business.dtos.requests.CreateBillingAccountRequest;
import com.etiya.crm.customerservice.business.dtos.requests.CustomerSearchRequest;
import com.etiya.crm.customerservice.business.dtos.requests.IndividualInfo;
import com.etiya.crm.customerservice.business.dtos.requests.OnboardCustomerRequest;
import com.etiya.crm.customerservice.business.dtos.requests.UpdateBillingAccountRequest;
import com.etiya.crm.customerservice.business.dtos.requests.UpdateIndividualInfo;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerAccountResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerSearchResponse;
import com.etiya.crm.customerservice.business.dtos.responses.IdentityVerificationResponse;
import com.etiya.crm.customerservice.business.exceptions.BillingAccountNotFoundException;
import com.etiya.crm.customerservice.business.exceptions.CustomerNotFoundException;
import com.etiya.crm.customerservice.business.exceptions.OnboardingFailedException;
import com.etiya.crm.customerservice.business.rules.CustomerBusinessRules;
import com.etiya.crm.shared.contracts.contactmedium.ContactMediumCommand;
import com.etiya.crm.shared.contracts.address.CreateAddressRequest;
import com.etiya.crm.shared.contracts.contactmedium.CreateContactCommand;
import com.etiya.crm.shared.contracts.contactmedium.CreateContactMediumRequest;
import com.etiya.crm.shared.contracts.individual.CreateIndividualCommand;
import com.etiya.crm.shared.contracts.address.UpdateAddressRequest;
import com.etiya.crm.shared.contracts.contactmedium.UpdateContactMediumRequest;
import com.etiya.crm.shared.contracts.individual.UpdateIndividualCommand;
import com.etiya.crm.customerservice.clients.controllers.ContactAddressClient;
import com.etiya.crm.customerservice.clients.controllers.PartyClient;
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.contactmedium.ContactMediumResponse;
import com.etiya.crm.shared.contracts.individual.IndividualResponse;
import com.etiya.crm.shared.contracts.individual.PartyRoleResponse;
import com.etiya.crm.customerservice.constants.AccountDefaults;
import com.etiya.crm.customerservice.constants.CacheNames;
import com.etiya.crm.customerservice.constants.LogMessages;
import com.etiya.crm.shared.contracts.lookup.LookupCodes;
import com.etiya.crm.shared.contracts.lookup.LookupGroups;
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
			createSearchView(customer, request.individual(), request.contact(), account.getAccountNo());

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
	public Page<CustomerSearchResponse> search(CustomerSearchRequest request, Pageable pageable) {
		return customerSearchViewRepository
				.findAll(CustomerSearchSpecifications.search(request.firstName(), request.lastName(),
						request.tcNo(), request.acctNo(), request.custId(), request.gsm()), pageable)
				.map(customerMapper::toResponse);
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
		List<CustomerAccount> accounts = customerAccountRepository.findByCustomer_CustIdAndActiveTrue(custId);
		rules.ensureNoActiveBillingAccount(accounts,
				lookupCacheService.resolveTypeId(LookupGroups.ACCOUNT_TYPE, LookupCodes.ACCOUNT_TYPE_BILL_ACCT),
				lookupCacheService.resolveStatusId(LookupGroups.ACCOUNT_STATUS, LookupCodes.ACCOUNT_STATUS_ACTIVE));

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
		rules.validateBirthDate(request.birthDate());
		UpdateIndividualCommand command = new UpdateIndividualCommand(request.firstName(), request.middleName(),
				request.lastName(), request.genderId(), request.motherName(), request.fatherName(),
				request.birthDate(), request.nationalId());
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

		CreateAddressRequest command = new CreateAddressRequest(custId, dataTypeId, request.cityId(),
				request.streetName(), request.buildingName(), request.addressDesc(), request.primary());
		return contactAddressClient.addAddress(command);
	}

	@Override
	@Transactional(readOnly = true)
	public AddressResponse updateAddress(Long custId, Long addressId, AddressEditRequest request) {
		getActiveCustomerOrThrow(custId);
		List<AddressResponse> existing = contactAddressClient.getAddressesByCustomer(custId, resolveCustomerDataTypeId());
		rules.ensureAddressBelongsToCustomer(custId, addressId, existing);

		UpdateAddressRequest command = new UpdateAddressRequest(request.cityId(), request.streetName(),
				request.buildingName(), request.addressDesc(), request.primary());
		return contactAddressClient.updateAddress(addressId, command);
	}

	@Override
	@Transactional(readOnly = true)
	public void deleteAddress(Long custId, Long addressId) {
		getActiveCustomerOrThrow(custId);
		List<AddressResponse> existing = contactAddressClient.getAddressesByCustomer(custId, resolveCustomerDataTypeId());
		AddressResponse address = rules.ensureAddressBelongsToCustomer(custId, addressId, existing);

		rules.ensureAddressNotPrimary(address);
		rules.ensureAddressNotLinkedToBillingAccount(customerAccountRepository.existsByAddressIdAndActiveTrue(addressId));

		contactAddressClient.deleteAddress(addressId);
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
	public Page<CustomerAccountResponse> getAccounts(Long custId, Pageable pageable) {
		getActiveCustomerOrThrow(custId);
		return customerAccountRepository.findByCustomer_CustIdAndActiveTrue(custId, pageable)
				.map(customerMapper::toResponse);
	}

	@Override
	@Transactional
	@CacheEvict(cacheManager = CacheNames.REDIS_CACHE_MANAGER, cacheNames = CacheNames.CUSTOMERS, key = "#custId")
	public CustomerAccountResponse createBillingAccount(Long custId, CreateBillingAccountRequest request) {
		Customer customer = getActiveCustomerOrThrow(custId);
		rules.ensureAddressProvided(request.addressId(), request.newAddress());

		Long addressId = resolveBillingAddressId(custId, request.addressId(), request.newAddress());

		CustomerAccount account = new CustomerAccount();
		account.setCustomer(customer);
		account.setAccountName(request.accountName());
		account.setAccountDesc(request.accountDesc());
		account.setAccountTpId(
				lookupCacheService.resolveTypeId(LookupGroups.ACCOUNT_TYPE, LookupCodes.ACCOUNT_TYPE_BILL_ACCT));
		account.setAddressId(addressId);
		account.setAcctStId(
				lookupCacheService.resolveStatusId(LookupGroups.ACCOUNT_STATUS, LookupCodes.ACCOUNT_STATUS_ACTIVE));
		// acct_no NOT NULL+UNIQUE oldugu icin gecici bir deger ile ilk kayit yapilir,
		// IDENTITY'den donen custAcctId ile asil numara ikinci kayitta yazilir.
		account.setAccountNo(UUID.randomUUID().toString());
		account = customerAccountRepository.save(account);
		account.setAccountNo(AccountDefaults.formatAccountNo(account.getCustAcctId()));
		account = customerAccountRepository.save(account);

		return customerMapper.toResponse(account);
	}

	@Override
	@Transactional
	@CacheEvict(cacheManager = CacheNames.REDIS_CACHE_MANAGER, cacheNames = CacheNames.CUSTOMERS, key = "#custId")
	public CustomerAccountResponse updateBillingAccount(Long custId, Long accountId,
			UpdateBillingAccountRequest request) {
		getActiveCustomerOrThrow(custId);
		rules.ensureAddressProvided(request.addressId(), request.newAddress());
		CustomerAccount account = customerAccountRepository
				.findByCustAcctIdAndCustomer_CustIdAndActiveTrue(accountId, custId)
				.orElseThrow(() -> new BillingAccountNotFoundException(custId, accountId));

		Long addressId = resolveBillingAddressId(custId, request.addressId(), request.newAddress());

		// accountNo/accountTpId burada DEGISTIRILMEZ - sadece name/desc/adres guncellenebilir.
		account.setAccountName(request.accountName());
		account.setAccountDesc(request.accountDesc());
		account.setAddressId(addressId);
		account = customerAccountRepository.save(account);

		return customerMapper.toResponse(account);
	}

	@Override
	@Transactional
	@CacheEvict(cacheManager = CacheNames.REDIS_CACHE_MANAGER, cacheNames = CacheNames.CUSTOMERS, key = "#custId")
	public void deleteBillingAccount(Long custId, Long accountId) {
		getActiveCustomerOrThrow(custId);
		CustomerAccount account = customerAccountRepository
				.findByCustAcctIdAndCustomer_CustIdAndActiveTrue(accountId, custId)
				.orElseThrow(() -> new BillingAccountNotFoundException(custId, accountId));

		// Urun guard'i (ACC-004, pasif hesaba bagli urun) order-service'i bekliyor - TODO,
		// burada uygulanmiyor (bkz. BRAIN SS3 FR-011).
		rules.ensureBillingAccountNotActive(account,
				lookupCacheService.resolveStatusId(LookupGroups.ACCOUNT_STATUS, LookupCodes.ACCOUNT_STATUS_ACTIVE));

		account.setActive(false);
		customerAccountRepository.save(account);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsAccountByAddressId(Long addressId) {
		return customerAccountRepository.existsByAddressIdAndActiveTrue(addressId);
	}

	private Long resolveBillingAddressId(Long custId, Long addressId, AddressInfo newAddress) {
		Long dataTypeId = resolveCustomerDataTypeId();
		if (newAddress != null) {
			CreateAddressRequest command = new CreateAddressRequest(custId, dataTypeId, newAddress.cityId(),
					newAddress.streetName(), newAddress.buildingName(), newAddress.addressDesc(), false);
			return contactAddressClient.addAddress(command).id();
		}
		List<AddressResponse> existing = contactAddressClient.getAddressesByCustomer(custId, dataTypeId);
		rules.ensureAddressBelongsToCustomer(custId, addressId, existing);
		return addressId;
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
			UpdateContactMediumRequest command = new UpdateContactMediumRequest(value, typeId);
			return contactAddressClient.updateContactMedium(current.id(), command);
		}

		if (!StringUtils.hasText(value)) {
			return null;
		}

		CreateContactMediumRequest command = new CreateContactMediumRequest(custId, dataTypeId, value, typeId);
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
		return lookupCacheService.resolveDataTypeId(LookupCodes.TABLE_NAME_CUSTOMER);
	}

	private Long resolveContactMediumTypeId(String code) {
		return lookupCacheService.resolveTypeId(LookupGroups.CONTACT_MEDIUM_TYPE, code);
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
		account.setAccountNo(AccountDefaults.formatAccountNo(customer.getCustId()));
		account.setAccountTpId(
				lookupCacheService.resolveTypeId(LookupGroups.ACCOUNT_TYPE, LookupCodes.ACCOUNT_TYPE_CUST_ACCT));
		account.setAcctStId(
				lookupCacheService.resolveStatusId(LookupGroups.ACCOUNT_STATUS, LookupCodes.ACCOUNT_STATUS_ACTIVE));
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

	/**
	 * firstName/middleName/lastName/tcNo/gsm burada senkron yazilir: hepsi bu
	 * istekte customer-service'e caller tarafindan verilen degerlerdir, baska
	 * bir servisin karari degildir. role BURADA YAZILMAZ: rol (partyRoleTypeId)
	 * party-service'in karari - PartyEventListener'in az sonra tuketecegi
	 * IndividualPartyCreated event'i ile async doldurulur (bkz. PartyEventListener).
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
				lookupCacheService.resolveTypeId(LookupGroups.CONTACT_MEDIUM_TYPE, LookupCodes.CONTACT_MEDIUM_EMAIL),
				contact.email()));
		mediums.add(new ContactMediumCommand(
				lookupCacheService.resolveTypeId(LookupGroups.CONTACT_MEDIUM_TYPE,
						LookupCodes.CONTACT_MEDIUM_MOBILE_PHONE),
				contact.mobilePhone()));
		if (StringUtils.hasText(contact.homePhone())) {
			mediums.add(new ContactMediumCommand(
					lookupCacheService.resolveTypeId(LookupGroups.CONTACT_MEDIUM_TYPE,
							LookupCodes.CONTACT_MEDIUM_HOME_PHONE),
					contact.homePhone()));
		}
		if (StringUtils.hasText(contact.fax())) {
			mediums.add(new ContactMediumCommand(
					lookupCacheService.resolveTypeId(LookupGroups.CONTACT_MEDIUM_TYPE, LookupCodes.CONTACT_MEDIUM_FAX),
					contact.fax()));
		}
		return mediums;
	}
}
