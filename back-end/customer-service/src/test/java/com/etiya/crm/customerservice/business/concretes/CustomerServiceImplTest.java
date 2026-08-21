package com.etiya.crm.customerservice.business.concretes;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.etiya.crm.customerservice.business.abstracts.BillingAccountService;
import com.etiya.crm.customerservice.business.abstracts.CustomerDeletionSagaOrchestrator;
import com.etiya.crm.customerservice.business.abstracts.CustomerFinder;
import com.etiya.crm.customerservice.business.abstracts.CustomerLookupResolver;
import com.etiya.crm.customerservice.business.abstracts.LookupCacheService;
import com.etiya.crm.customerservice.business.dtos.requests.CustomerSearchRequest;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerSearchResponse;
import com.etiya.crm.customerservice.business.exceptions.SearchFilterRequiredException;
import com.etiya.crm.customerservice.business.rules.CustomerBusinessRules;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerAccountRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerSearchViewRepository;
import com.etiya.crm.customerservice.entities.concretes.Customer;
import com.etiya.crm.customerservice.entities.concretes.CustomerAccount;
import com.etiya.crm.customerservice.entities.concretes.CustomerSearchView;
import com.etiya.crm.customerservice.mapper.CustomerMapper;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private CustomerAccountRepository customerAccountRepository;

	@Mock
	private CustomerSearchViewRepository customerSearchViewRepository;

	@Mock
	private CustomerMapper customerMapper;

	@Mock
	private OutboxEventPublisher outboxEventPublisher;

	@Mock
	private CustomerLookupResolver lookupResolver;

	@Mock
	private CustomerFinder customerFinder;

	@Mock
	private BillingAccountService billingAccountService;

	@Mock
	private CustomerBusinessRules rules;

	@Mock
	private LookupCacheService lookupCacheService;

	@Mock
	private CustomerDeletionSagaOrchestrator deletionSagaOrchestrator;

	@InjectMocks
	private CustomerServiceImpl service;

	private final Pageable pageable = PageRequest.of(0, 10);

	@Test
	void search_throws_whenNoFilterProvided() {
		CustomerSearchRequest request = new CustomerSearchRequest(null, null, null, null, null, null);
		org.mockito.Mockito.doThrow(new SearchFilterRequiredException()).when(rules)
				.ensureAtLeastOneFilterProvided(request);

		assertThatThrownBy(() -> service.search(request, pageable))
				.isInstanceOf(SearchFilterRequiredException.class);
	}

	@Test
	void search_returnsUntranslatedRole_whenPartyRoleTypeIdIsNull() {
		CustomerSearchRequest request = new CustomerSearchRequest("Ahmet", null, null, null, null, null);
		CustomerSearchView view = new CustomerSearchView();
		view.setCustId(10L);
		view.setPartyRoleTypeId(null);
		when(customerSearchViewRepository.findAll(any(Specification.class), eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(view)));
		CustomerSearchResponse mapped = new CustomerSearchResponse(10L, "Ahmet", null, "Yilmaz", null, null, null,
				null, null, null);
		when(customerMapper.toResponse(view)).thenReturn(mapped);

		Page<CustomerSearchResponse> result = service.search(request, pageable);

		assertThat(result.getContent()).containsExactly(mapped);
		verify(lookupCacheService, never()).resolveTypeValue(any());
	}

	@Test
	void search_translatesRole_whenPartyRoleTypeIdPresent() {
		CustomerSearchRequest request = new CustomerSearchRequest("Ahmet", null, null, null, null, null);
		CustomerSearchView view = new CustomerSearchView();
		view.setCustId(10L);
		view.setPartyRoleTypeId(1L);
		when(customerSearchViewRepository.findAll(any(Specification.class), eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(view)));
		CustomerSearchResponse mapped = new CustomerSearchResponse(10L, "Ahmet", null, "Yilmaz", null, null, "CUST",
				null, null, null);
		when(customerMapper.toResponse(view)).thenReturn(mapped);
		when(lookupCacheService.resolveTypeValue(1L)).thenReturn("Musteri");
		when(lookupCacheService.resolveTypeShrtCode(1L)).thenReturn("CUST");

		Page<CustomerSearchResponse> result = service.search(request, pageable);

		assertThat(result.getContent().get(0).role()).isEqualTo("Musteri");
		assertThat(result.getContent().get(0).roleShrtCode()).isEqualTo("CUST");
	}

	@Test
	void getById_mapsCustomerWithNonDeletedAccounts() {
		Customer customer = new Customer();
		customer.setCustId(10L);
		when(customerFinder.getActiveCustomerOrThrow(10L)).thenReturn(customer);
		when(lookupResolver.resolveDeletedAccountStatusId()).thenReturn(603L);
		List<CustomerAccount> accounts = List.of(new CustomerAccount());
		when(customerAccountRepository.findByCustomer_CustIdAndAcctStIdNotDeleted(10L, 603L)).thenReturn(accounts);
		when(lookupResolver.resolveActiveAccountStatusId()).thenReturn(601L);
		CustomerResponse response = new CustomerResponse(10L, "000010", 100L, 1L, true, List.of());
		when(customerMapper.toResponse(customer, accounts, 601L)).thenReturn(response);

		CustomerResponse result = service.getById(10L);

		assertThat(result).isSameAs(response);
	}

	@Test
	void softDelete_deactivatesCustomerAccountsAndSearchView_thenPublishesEvent() {
		Customer customer = new Customer();
		customer.setCustId(10L);
		customer.setPartyRoleId(100L);
		when(customerFinder.getActiveCustomerOrThrow(10L)).thenReturn(customer);
		when(lookupResolver.resolveDeletedAccountStatusId()).thenReturn(603L);
		List<CustomerAccount> accounts = List.of(new CustomerAccount());
		when(customerAccountRepository.findByCustomer_CustIdAndAcctStIdNotDeleted(10L, 603L)).thenReturn(accounts);
		CustomerSearchView view = new CustomerSearchView();
		when(customerSearchViewRepository.findById(10L)).thenReturn(Optional.of(view));
		when(lookupResolver.resolveCustomerDataTypeId()).thenReturn(1L);

		service.softDelete(10L);

		verify(billingAccountService).ensureNoActiveBillingAccount(accounts);
		verify(billingAccountService).ensureNoBillingAccountWithLinkedProducts(accounts);
		assertThat(customer.isActive()).isFalse();
		verify(customerRepository).save(customer);
		verify(customerAccountRepository).softDeleteByCustId(10L, 603L);
		assertThat(view.isDeleted()).isTrue();
		verify(customerSearchViewRepository).save(view);
		verify(outboxEventPublisher, times(1)).publish(any(), eq("10"), any(), any());
		verify(deletionSagaOrchestrator).start(10L);
	}

	@Test
	void softDelete_skipsSearchViewUpdate_whenNoSearchViewRow() {
		Customer customer = new Customer();
		customer.setCustId(10L);
		when(customerFinder.getActiveCustomerOrThrow(10L)).thenReturn(customer);
		when(lookupResolver.resolveDeletedAccountStatusId()).thenReturn(603L);
		when(customerAccountRepository.findByCustomer_CustIdAndAcctStIdNotDeleted(10L, 603L)).thenReturn(List.of());
		when(customerSearchViewRepository.findById(10L)).thenReturn(Optional.empty());
		when(lookupResolver.resolveCustomerDataTypeId()).thenReturn(1L);

		service.softDelete(10L);

		verify(customerSearchViewRepository, never()).save(any());
	}
}
