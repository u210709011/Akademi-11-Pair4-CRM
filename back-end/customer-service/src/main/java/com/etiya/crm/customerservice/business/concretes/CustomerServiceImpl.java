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
import com.etiya.crm.customerservice.business.abstracts.LookupCacheService;
import com.etiya.crm.customerservice.business.dtos.requests.CustomerSearchRequest;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerSearchResponse;
import com.etiya.crm.customerservice.business.rules.CustomerBusinessRules;
import com.etiya.crm.customerservice.constants.CacheNames;
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
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
/** Müşteri arama, görüntüleme ve silme işlemlerini yürütür. */
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepository customerRepository;
	private final CustomerAccountRepository customerAccountRepository;
	private final CustomerSearchViewRepository customerSearchViewRepository;
	private final CustomerMapper customerMapper;
	private final OutboxEventPublisher outboxEventPublisher;
	private final CustomerLookupResolver lookupResolver;
	private final CustomerFinder customerFinder;
	private final BillingAccountService billingAccountService;
	private final CustomerBusinessRules rules;
	private final LookupCacheService lookupCacheService;

	@Override
	@Transactional(readOnly = true)
	/** Filtreli arama sonucunu yerelleştirilmiş rol bilgisiyle döner. */
	public Page<CustomerSearchResponse> search(CustomerSearchRequest request, Pageable pageable) {
		rules.ensureAtLeastOneFilterProvided(request);
		return customerSearchViewRepository
				.findAll(CustomerSearchSpecifications.search(request.firstName(), request.lastName(),
						request.tcNo(), request.acctNo(), request.custId(), request.gsm()), pageable)
				.map(this::toTranslatedSearchResponse);
	}


	private CustomerSearchResponse toTranslatedSearchResponse(CustomerSearchView searchView) {
		CustomerSearchResponse response = customerMapper.toResponse(searchView);
		if (searchView.getPartyRoleTypeId() == null) {
			return response;
		}
		String translatedRole = lookupCacheService.resolveTypeValue(searchView.getPartyRoleTypeId());
		String roleShrtCode = lookupCacheService.resolveTypeShrtCode(searchView.getPartyRoleTypeId());
		return new CustomerSearchResponse(response.custId(), response.firstName(), response.middleName(),
				response.lastName(), response.tcNo(), response.acctNo(), translatedRole, roleShrtCode,
				response.gsm(), response.status());
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheManager = CacheNames.REDIS_CACHE_MANAGER, cacheNames = CacheNames.CUSTOMERS, key = "#custId")
	/** Aktif müşteriyi hesaplarıyla birlikte getirir. */
	public CustomerResponse getById(Long custId) {
		Customer customer = customerFinder.getActiveCustomerOrThrow(custId);
		List<CustomerAccount> accounts = customerAccountRepository
				.findByCustomer_CustIdAndAcctStIdNotDeleted(custId, lookupResolver.resolveDeletedAccountStatusId());
		return customerMapper.toResponse(customer, accounts, lookupResolver.resolveActiveAccountStatusId());
	}

	@Override
	@Transactional
	@CacheEvict(cacheManager = CacheNames.REDIS_CACHE_MANAGER, cacheNames = CacheNames.CUSTOMERS, key = "#custId")
	/** Müşteriyi, hesaplarını ve arama görünümünü pasifleştirir. */
	public void softDelete(Long custId) {
		Customer customer = customerFinder.getActiveCustomerOrThrow(custId);
		List<CustomerAccount> accounts = customerAccountRepository
				.findByCustomer_CustIdAndAcctStIdNotDeleted(custId, lookupResolver.resolveDeletedAccountStatusId());
		billingAccountService.ensureNoActiveBillingAccount(accounts);
		billingAccountService.ensureNoBillingAccountWithLinkedProducts(accounts);

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
