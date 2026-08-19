package com.etiya.crm.customerservice.business.concretes;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.customerservice.business.abstracts.BillingAccountProductGuard;
import com.etiya.crm.customerservice.business.abstracts.CustomerAddressService;
import com.etiya.crm.customerservice.business.abstracts.CustomerFinder;
import com.etiya.crm.customerservice.business.abstracts.CustomerLookupResolver;
import com.etiya.crm.customerservice.business.dtos.requests.CreateBillingAccountRequest;
import com.etiya.crm.customerservice.business.dtos.requests.UpdateBillingAccountRequest;
import com.etiya.crm.customerservice.business.dtos.requests.UpdateBillingAccountStatusRequest;
import com.etiya.crm.customerservice.business.dtos.responses.AddressBillingAccountsResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerAccountResponse;
import com.etiya.crm.customerservice.business.exceptions.BillingAccountNotFoundException;
import com.etiya.crm.customerservice.business.rules.BillingAccountBusinessRules;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerAccountRepository;
import com.etiya.crm.customerservice.entities.concretes.Customer;
import com.etiya.crm.customerservice.entities.concretes.CustomerAccount;
import com.etiya.crm.customerservice.mapper.CustomerMapper;
import com.etiya.crm.shared.contracts.address.AddressResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillingAccountServiceImplTest {

	@Mock
	private CustomerAccountRepository customerAccountRepository;

	@Mock
	private CustomerMapper customerMapper;

	@Mock
	private BillingAccountBusinessRules rules;

	@Mock
	private CustomerLookupResolver lookupResolver;

	@Mock
	private CustomerAddressService addressService;

	@Mock
	private CustomerFinder customerFinder;

	@Mock
	private BillingAccountProductGuard productGuard;

	@Mock
	private AccountNumberGenerator accountNumberGenerator;

	@InjectMocks
	private BillingAccountServiceImpl service;

	@Test
	void createBillingAccount_savesTwice_toAssignFinalAccountNo() {
		Customer customer = new Customer();
		customer.setCustId(10L);
		when(customerFinder.getActiveCustomerOrThrow(10L)).thenReturn(customer);
		when(addressService.resolveBillingAddress(eq(10L), any(), any())).thenReturn(address(5L));
		when(lookupResolver.resolveBillingAccountTypeId()).thenReturn(501L);
		when(lookupResolver.resolveActiveAccountStatusId()).thenReturn(601L);
		when(customerAccountRepository.save(any())).thenAnswer(invocation -> {
			CustomerAccount account = invocation.getArgument(0);
			account.setCustAcctId(42L);
			return account;
		});
		when(accountNumberGenerator.generate(42L)).thenReturn("000042");
		CustomerAccountResponse mapped = new CustomerAccountResponse(42L, "000042", "Home", "desc", 501L, 5L, 601L,
				true);
		when(customerMapper.toResponse(any(CustomerAccount.class), eq(601L))).thenReturn(mapped);
		CreateBillingAccountRequest request = new CreateBillingAccountRequest("Home", "desc", 5L, null);

		CustomerAccountResponse result = service.createBillingAccount(10L, request);

		assertThat(result).isSameAs(mapped);
		verify(rules).ensureAddressProvided(5L, null);
		verify(customerAccountRepository, times(2)).save(any());
	}

	@Test
	void updateBillingAccount_updatesNameDescAndAddress_notAccountNoOrType() {
		when(addressService.resolveBillingAddress(eq(10L), any(), any())).thenReturn(address(6L));
		CustomerAccount existing = new CustomerAccount();
		existing.setCustAcctId(42L);
		existing.setAccountNo("000042");
		existing.setAccountTpId(501L);
		when(lookupResolver.resolveDeletedAccountStatusId()).thenReturn(603L);
		when(customerAccountRepository.findByCustAcctIdAndCustomer_CustIdAndAcctStIdNotDeleted(42L, 10L, 603L))
				.thenReturn(Optional.of(existing));
		when(customerAccountRepository.save(existing)).thenReturn(existing);
		when(lookupResolver.resolveActiveAccountStatusId()).thenReturn(601L);
		CustomerAccountResponse mapped = new CustomerAccountResponse(42L, "000042", "New Name", "New Desc", 501L, 6L,
				601L, true);
		when(customerMapper.toResponse(existing, 601L)).thenReturn(mapped);
		UpdateBillingAccountRequest request = new UpdateBillingAccountRequest("New Name", "New Desc", 6L, null);

		CustomerAccountResponse result = service.updateBillingAccount(10L, 42L, request);

		assertThat(result).isSameAs(mapped);
		assertThat(existing.getAccountName()).isEqualTo("New Name");
		assertThat(existing.getAddressId()).isEqualTo(6L);
		assertThat(existing.getAccountNo()).isEqualTo("000042");
		assertThat(existing.getAccountTpId()).isEqualTo(501L);
	}

	@Test
	void updateBillingAccount_throws_whenAccountNotFoundForCustomer() {
		when(lookupResolver.resolveDeletedAccountStatusId()).thenReturn(603L);
		when(customerAccountRepository.findByCustAcctIdAndCustomer_CustIdAndAcctStIdNotDeleted(99L, 10L, 603L))
				.thenReturn(Optional.empty());
		UpdateBillingAccountRequest request = new UpdateBillingAccountRequest("Name", "Desc", 6L, null);

		assertThatThrownBy(() -> service.updateBillingAccount(10L, 99L, request))
				.isInstanceOf(BillingAccountNotFoundException.class);
	}

	@Test
	void updateBillingAccountStatus_setsActiveStatusId_whenRequestIsActive() {
		CustomerAccount existing = new CustomerAccount();
		existing.setCustAcctId(42L);
		when(lookupResolver.resolveDeletedAccountStatusId()).thenReturn(603L);
		when(customerAccountRepository.findByCustAcctIdAndCustomer_CustIdAndAcctStIdNotDeleted(42L, 10L, 603L))
				.thenReturn(Optional.of(existing));
		when(lookupResolver.resolveBillingAccountTypeId()).thenReturn(501L);
		when(lookupResolver.resolveActiveAccountStatusId()).thenReturn(601L);
		when(customerAccountRepository.save(existing)).thenReturn(existing);
		UpdateBillingAccountStatusRequest request = new UpdateBillingAccountStatusRequest(
				UpdateBillingAccountStatusRequest.ACTIVE);

		service.updateBillingAccountStatus(10L, 42L, request);

		assertThat(existing.getAcctStId()).isEqualTo(601L);
	}

	@Test
	void updateBillingAccountStatus_setsPassiveStatusId_whenRequestIsPassive() {
		CustomerAccount existing = new CustomerAccount();
		existing.setCustAcctId(42L);
		when(lookupResolver.resolveDeletedAccountStatusId()).thenReturn(603L);
		when(customerAccountRepository.findByCustAcctIdAndCustomer_CustIdAndAcctStIdNotDeleted(42L, 10L, 603L))
				.thenReturn(Optional.of(existing));
		when(lookupResolver.resolveBillingAccountTypeId()).thenReturn(501L);
		when(lookupResolver.resolveActiveAccountStatusId()).thenReturn(601L);
		when(lookupResolver.resolvePassiveAccountStatusId()).thenReturn(602L);
		when(customerAccountRepository.save(existing)).thenReturn(existing);
		UpdateBillingAccountStatusRequest request = new UpdateBillingAccountStatusRequest(
				UpdateBillingAccountStatusRequest.PASSIVE);

		service.updateBillingAccountStatus(10L, 42L, request);

		assertThat(existing.getAcctStId()).isEqualTo(602L);
	}

	@Test
	void deleteBillingAccount_runsAllGuards_thenMarksDeleted() {
		CustomerAccount existing = new CustomerAccount();
		existing.setCustAcctId(42L);
		when(lookupResolver.resolveDeletedAccountStatusId()).thenReturn(603L);
		when(customerAccountRepository.findByCustAcctIdAndCustomer_CustIdAndAcctStIdNotDeleted(42L, 10L, 603L))
				.thenReturn(Optional.of(existing));
		when(lookupResolver.resolveBillingAccountTypeId()).thenReturn(501L);
		when(lookupResolver.resolveActiveAccountStatusId()).thenReturn(601L);
		when(productGuard.hasLinkedProducts(42L)).thenReturn(false);

		service.deleteBillingAccount(10L, 42L);

		verify(rules).ensureAccountIsBillingType(existing, 501L);
		verify(rules).ensureBillingAccountNotActive(existing, 601L);
		verify(rules).ensureNoLinkedProducts(false);
		assertThat(existing.getAcctStId()).isEqualTo(603L);
		verify(customerAccountRepository).save(existing);
	}

	@Test
	void existsAccountByAddressId_delegatesToRepository() {
		when(lookupResolver.resolveDeletedAccountStatusId()).thenReturn(603L);
		when(customerAccountRepository.existsByAddressIdAndAcctStIdNotDeleted(5L, 603L)).thenReturn(true);

		assertThat(service.existsAccountByAddressId(5L)).isTrue();
	}

	@Test
	void getAccountsByAddressId_returnsCountAndMappedAccounts() {
		when(lookupResolver.resolveActiveAccountStatusId()).thenReturn(601L);
		when(lookupResolver.resolveDeletedAccountStatusId()).thenReturn(603L);
		CustomerAccount account = new CustomerAccount();
		account.setCustAcctId(42L);
		when(customerAccountRepository.findByAddressIdAndAcctStIdNotDeleted(5L, 603L)).thenReturn(List.of(account));
		CustomerAccountResponse mapped = new CustomerAccountResponse(42L, "000042", "Home", "desc", 501L, 5L, 601L,
				true);
		when(customerMapper.toResponse(account, 601L)).thenReturn(mapped);

		AddressBillingAccountsResponse result = service.getAccountsByAddressId(5L);

		assertThat(result.count()).isEqualTo(1);
		assertThat(result.accounts()).containsExactly(mapped);
	}

	@Test
	void ensureNoActiveBillingAccount_delegatesToRules() {
		when(lookupResolver.resolveBillingAccountTypeId()).thenReturn(501L);
		when(lookupResolver.resolveActiveAccountStatusId()).thenReturn(601L);
		List<CustomerAccount> accounts = List.of(new CustomerAccount());

		service.ensureNoActiveBillingAccount(accounts);

		verify(rules).ensureNoActiveBillingAccount(accounts, 501L, 601L);
	}

	@Test
	void ensureNoBillingAccountWithLinkedProducts_true_whenAnyBillingAccountHasLinkedProducts() {
		when(lookupResolver.resolveBillingAccountTypeId()).thenReturn(501L);
		CustomerAccount billingAccount = new CustomerAccount();
		billingAccount.setCustAcctId(42L);
		billingAccount.setAccountTpId(501L);
		CustomerAccount defaultAccount = new CustomerAccount();
		defaultAccount.setCustAcctId(43L);
		defaultAccount.setAccountTpId(999L);
		when(productGuard.hasLinkedProducts(42L)).thenReturn(true);

		service.ensureNoBillingAccountWithLinkedProducts(List.of(billingAccount, defaultAccount));

		verify(rules).ensureNoLinkedProducts(true);
		verify(productGuard, times(0)).hasLinkedProducts(43L);
	}

	@Test
	void ensureNoBillingAccountWithLinkedProducts_false_whenNoBillingAccountsPresent() {
		when(lookupResolver.resolveBillingAccountTypeId()).thenReturn(501L);
		CustomerAccount defaultAccount = new CustomerAccount();
		defaultAccount.setAccountTpId(999L);

		service.ensureNoBillingAccountWithLinkedProducts(List.of(defaultAccount));

		verify(rules).ensureNoLinkedProducts(false);
	}

	private AddressResponse address(Long id) {
		return new AddressResponse(id, 10L, 1L, 5L, "Cad", "No", "Ev", false, Instant.now(), "sys", Instant.now(),
				"sys");
	}
}
