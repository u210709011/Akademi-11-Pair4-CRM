package com.etiya.crm.customerservice.business.concretes;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etiya.crm.customerservice.business.abstracts.BillingAccountService;
import com.etiya.crm.customerservice.business.abstracts.CustomerFinder;
import com.etiya.crm.customerservice.business.abstracts.CustomerLookupResolver;
import com.etiya.crm.customerservice.business.abstracts.CustomerService;
import com.etiya.crm.customerservice.business.dtos.requests.CustomerSearchRequest;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerSearchResponse;
import com.etiya.crm.customerservice.constants.CacheNames;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerAccountRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerSearchSpecifications;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerSearchViewRepository;
import com.etiya.crm.customerservice.entities.concretes.Customer;
import com.etiya.crm.customerservice.entities.concretes.CustomerAccount;
import com.etiya.crm.customerservice.mapper.CustomerMapper;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.customer.CustomerDeletedEvent;
import com.etiya.crm.shared.events.customer.CustomerEventTypes;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;

import lombok.RequiredArgsConstructor;

/**
 * "Customer" kaynaginin front-door'u (bkz. CustomerController). Kendisi sadece
 * Customer aggregate'inin oz yasam donguсunu (arama, okuma, soft-delete) tutar;
 * onboarding saga'si, adres/contact/billing-account yonetimi ve lookup-service ID
 * cozumleme her biri kendi arayuzu arkasindaki ayri bir collaborator'a
 * devredilir - boylece bu sinifin degisme sebebi tek kalir: "Customer aggregate'i
 * nasil aranir/okunur/silinir".
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepository customerRepository;
	private final CustomerAccountRepository customerAccountRepository;
	private final CustomerSearchViewRepository customerSearchViewRepository;
	private final CustomerMapper customerMapper;
	private final OutboxEventPublisher outboxEventPublisher;
	private final CustomerLookupResolver lookupResolver;
	private final CustomerFinder customerFinder;
	private final BillingAccountService billingAccountService;

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
		Customer customer = customerFinder.getActiveCustomerOrThrow(custId);
		List<CustomerAccount> accounts = customerAccountRepository
				.findByCustomer_CustIdAndAcctStIdNotDeleted(custId, lookupResolver.resolveDeletedAccountStatusId());
		return customerMapper.toResponse(customer, accounts);
	}

	@Override
	@Transactional
	@CacheEvict(cacheManager = CacheNames.REDIS_CACHE_MANAGER, cacheNames = CacheNames.CUSTOMERS, key = "#custId")
	public void softDelete(Long custId) {
		Customer customer = customerFinder.getActiveCustomerOrThrow(custId);
		List<CustomerAccount> accounts = customerAccountRepository
				.findByCustomer_CustIdAndAcctStIdNotDeleted(custId, lookupResolver.resolveDeletedAccountStatusId());
		billingAccountService.ensureNoActiveBillingAccount(accounts);

		customer.setActive(false);
		customerRepository.save(customer);
		customerAccountRepository.softDeleteByCustId(custId, lookupResolver.resolveDeletedAccountStatusId());
		customerSearchViewRepository.findById(custId).ifPresent(view -> {
			view.setDeleted(true);
			customerSearchViewRepository.save(view);
		});

		outboxEventPublisher.publish(KafkaTopics.CUSTOMER_AGGREGATE_TYPE, custId.toString(),
				CustomerEventTypes.CUSTOMER_DELETED,
				new CustomerDeletedEvent(UUID.randomUUID(), CustomerEventTypes.CUSTOMER_DELETED, custId,
						customer.getPartyRoleId(), lookupResolver.resolveCustomerDataTypeId()));
	}
}
