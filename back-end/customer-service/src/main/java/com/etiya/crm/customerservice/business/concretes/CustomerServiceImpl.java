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
import com.etiya.crm.customerservice.business.dtos.requests.ContactInfo;
import com.etiya.crm.customerservice.business.dtos.requests.CustomerSearchRequest;
import com.etiya.crm.customerservice.business.dtos.requests.IndividualInfo;
import com.etiya.crm.customerservice.business.dtos.requests.OnboardCustomerRequest;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerSearchResponse;
import com.etiya.crm.customerservice.business.dtos.responses.IdentityVerificationResponse;
import com.etiya.crm.customerservice.business.exceptions.CustomerNotFoundException;
import com.etiya.crm.customerservice.business.exceptions.OnboardingFailedException;
import com.etiya.crm.customerservice.business.rules.CustomerBusinessRules;
import com.etiya.crm.customerservice.clients.ContactAddressClient;
import com.etiya.crm.customerservice.clients.ContactMediumCommand;
import com.etiya.crm.customerservice.clients.CreateContactCommand;
import com.etiya.crm.customerservice.clients.CreateIndividualCommand;
import com.etiya.crm.customerservice.clients.PartyClient;
import com.etiya.crm.customerservice.clients.PartyRoleResponse;
import com.etiya.crm.customerservice.constants.AccountDefaults;
import com.etiya.crm.customerservice.constants.CacheNames;
import com.etiya.crm.customerservice.constants.CustomerEventTypes;
import com.etiya.crm.customerservice.constants.DefaultLookupValues;
import com.etiya.crm.customerservice.constants.KafkaTopics;
import com.etiya.crm.customerservice.constants.LogMessages;
import com.etiya.crm.customerservice.constants.LookupCodes;
import com.etiya.crm.customerservice.constants.LookupGroups;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerAccountRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerSearchSpecifications;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerSearchViewRepository;
import com.etiya.crm.customerservice.entities.concretes.Customer;
import com.etiya.crm.customerservice.entities.concretes.CustomerAccount;
import com.etiya.crm.customerservice.entities.concretes.CustomerSearchView;
import com.etiya.crm.customerservice.events.CustomerDeletedPayload;
import com.etiya.crm.customerservice.events.CustomerOnboardedPayload;
import com.etiya.crm.customerservice.mapper.CustomerMapper;
import com.etiya.crm.customerservice.outbox.OutboxEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
					new CustomerOnboardedPayload(customer.getCustId(), customer.getPartyRoleId()));

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
		System.out.println("hello");
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
				CustomerEventTypes.CUSTOMER_DELETED, new CustomerDeletedPayload(custId, customer.getPartyRoleId()));
	}

	private Customer getActiveCustomerOrThrow(Long custId) {
		return customerRepository.findByCustIdAndActiveTrue(custId)
				.orElseThrow(() -> new CustomerNotFoundException(custId));
	}

	private Customer createCustomerWithDefaultAccount(Long partyRoleId) {
		Long activeStatusId = lookupCacheService.resolveId(LookupGroups.CUSTOMER_STATUS, LookupCodes.STATUS_ACTIVE);

		Customer customer = new Customer();
		customer.setPartyRoleId(partyRoleId);
		customer.setStId(activeStatusId);
		customer = customerRepository.save(customer); // IDENTITY: save sonrasi custId dolu gelir.

		Long accountStatusId = lookupCacheService.resolveId(LookupGroups.ACCOUNT_STATUS, LookupCodes.STATUS_ACTIVE);

		// ACC-025: musteri olusturulurken otomatik olarak varsayilan tipte tek bir hesap acilir.
		CustomerAccount account = new CustomerAccount();
		account.setCustomer(customer);
		account.setAccountNo(AccountDefaults.ACCOUNT_NO_PREFIX + customer.getCustId());
		account.setAccountTpId(DefaultLookupValues.DEFAULT_ACCOUNT_TYPE_ID);
		account.setStId(accountStatusId);
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
				lookupCacheService.resolveId(LookupGroups.CONTACT_MEDIUM_TYPE, LookupCodes.CONTACT_MEDIUM_EMAIL),
				contact.email()));
		mediums.add(new ContactMediumCommand(
				lookupCacheService.resolveId(LookupGroups.CONTACT_MEDIUM_TYPE,
						LookupCodes.CONTACT_MEDIUM_MOBILE_PHONE),
				contact.mobilePhone()));
		if (StringUtils.hasText(contact.homePhone())) {
			mediums.add(new ContactMediumCommand(
					lookupCacheService.resolveId(LookupGroups.CONTACT_MEDIUM_TYPE,
							LookupCodes.CONTACT_MEDIUM_HOME_PHONE),
					contact.homePhone()));
		}
		if (StringUtils.hasText(contact.fax())) {
			mediums.add(new ContactMediumCommand(
					lookupCacheService.resolveId(LookupGroups.CONTACT_MEDIUM_TYPE, LookupCodes.CONTACT_MEDIUM_FAX),
					contact.fax()));
		}
		return mediums;
	}
}
