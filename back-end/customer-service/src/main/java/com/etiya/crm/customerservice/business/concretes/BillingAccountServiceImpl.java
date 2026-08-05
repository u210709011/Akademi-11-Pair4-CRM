package com.etiya.crm.customerservice.business.concretes;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etiya.crm.customerservice.business.abstracts.BillingAccountProductGuard;
import com.etiya.crm.customerservice.business.abstracts.BillingAccountService;
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
import com.etiya.crm.customerservice.constants.AccountDefaults;
import com.etiya.crm.customerservice.constants.CacheNames;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerAccountRepository;
import com.etiya.crm.customerservice.entities.concretes.Customer;
import com.etiya.crm.customerservice.entities.concretes.CustomerAccount;
import com.etiya.crm.customerservice.mapper.CustomerMapper;
import com.etiya.crm.shared.contracts.address.AddressResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillingAccountServiceImpl implements BillingAccountService {

	private final CustomerAccountRepository customerAccountRepository;
	private final CustomerMapper customerMapper;
	private final BillingAccountBusinessRules rules;
	private final CustomerLookupResolver lookupResolver;
	private final CustomerAddressService addressService;
	private final CustomerFinder customerFinder;
	private final BillingAccountProductGuard productGuard;

	@Override
	@Transactional(readOnly = true)
	public Page<CustomerAccountResponse> getAccounts(Long custId, Pageable pageable) {
		Long activeStatusId = lookupResolver.resolveActiveAccountStatusId();
		return customerAccountRepository
				.findByCustomer_CustIdAndAcctStIdNotDeleted(custId, lookupResolver.resolveDeletedAccountStatusId(),
						pageable)
				.map(account -> customerMapper.toResponse(account, activeStatusId));
	}

	@Override
	@Transactional
	@CacheEvict(cacheManager = CacheNames.REDIS_CACHE_MANAGER, cacheNames = CacheNames.CUSTOMERS, key = "#custId")
	public CustomerAccountResponse createBillingAccount(Long custId, CreateBillingAccountRequest request) {
		Customer customer = customerFinder.getActiveCustomerOrThrow(custId);
		rules.ensureAddressProvided(request.addressId(), request.newAddress());

		AddressResponse address = addressService.resolveBillingAddress(custId, request.addressId(),
				request.newAddress());

		CustomerAccount account = new CustomerAccount();
		account.setCustomer(customer);
		account.setAccountName(request.accountName());
		account.setAccountDesc(request.accountDesc());
		account.setAccountTpId(lookupResolver.resolveBillingAccountTypeId());
		account.setAddressId(address.id());
		account.setAcctStId(lookupResolver.resolveActiveAccountStatusId());
		// acct_no NOT NULL+UNIQUE oldugu icin gecici bir deger ile ilk kayit yapilir,
		// IDENTITY'den donen custAcctId ile asil numara ikinci kayitta yazilir.
		account.setAccountNo(UUID.randomUUID().toString());
		account = customerAccountRepository.save(account);
		account.setAccountNo(AccountDefaults.formatAccountNo(account.getCustAcctId()));
		account = customerAccountRepository.save(account);

		return customerMapper.toResponse(account, lookupResolver.resolveActiveAccountStatusId());
	}

	@Override
	@Transactional
	@CacheEvict(cacheManager = CacheNames.REDIS_CACHE_MANAGER, cacheNames = CacheNames.CUSTOMERS, key = "#custId")
	public CustomerAccountResponse updateBillingAccount(Long custId, Long accountId,
			UpdateBillingAccountRequest request) {
		customerFinder.getActiveCustomerOrThrow(custId);
		rules.ensureAddressProvided(request.addressId(), request.newAddress());
		CustomerAccount account = customerAccountRepository
				.findByCustAcctIdAndCustomer_CustIdAndAcctStIdNotDeleted(accountId, custId,
						lookupResolver.resolveDeletedAccountStatusId())
				.orElseThrow(() -> new BillingAccountNotFoundException(custId, accountId));

		AddressResponse address = addressService.resolveBillingAddress(custId, request.addressId(), request.newAddress());

		// accountNo/accountTpId burada DEGISTIRILMEZ - sadece name/desc/adres guncellenebilir.
		account.setAccountName(request.accountName());
		account.setAccountDesc(request.accountDesc());
		account.setAddressId(address.id());
		account = customerAccountRepository.save(account);

		return customerMapper.toResponse(account, lookupResolver.resolveActiveAccountStatusId());
	}

	@Override
	@Transactional
	@CacheEvict(cacheManager = CacheNames.REDIS_CACHE_MANAGER, cacheNames = CacheNames.CUSTOMERS, key = "#custId")
	public CustomerAccountResponse updateBillingAccountStatus(Long custId, Long accountId,
			UpdateBillingAccountStatusRequest request) {
		customerFinder.getActiveCustomerOrThrow(custId);
		CustomerAccount account = customerAccountRepository
				.findByCustAcctIdAndCustomer_CustIdAndAcctStIdNotDeleted(accountId, custId,
						lookupResolver.resolveDeletedAccountStatusId())
				.orElseThrow(() -> new BillingAccountNotFoundException(custId, accountId));

		// Onboarding'de acilan varsayilan CUST_ACCT tipi hesabin durumu da degistirilemez -
		// deleteBillingAccount'taki ile ayni guard.
		rules.ensureAccountIsBillingType(account, lookupResolver.resolveBillingAccountTypeId());

		Long activeStatusId = lookupResolver.resolveActiveAccountStatusId();
		Long targetStatusId = "ACTIVE".equals(request.status()) ? activeStatusId
				: lookupResolver.resolvePassiveAccountStatusId();
		account.setAcctStId(targetStatusId);
		account = customerAccountRepository.save(account);

		return customerMapper.toResponse(account, activeStatusId);
	}

	@Override
	@Transactional
	@CacheEvict(cacheManager = CacheNames.REDIS_CACHE_MANAGER, cacheNames = CacheNames.CUSTOMERS, key = "#custId")
	public void deleteBillingAccount(Long custId, Long accountId) {
		customerFinder.getActiveCustomerOrThrow(custId);
		CustomerAccount account = customerAccountRepository
				.findByCustAcctIdAndCustomer_CustIdAndAcctStIdNotDeleted(accountId, custId,
						lookupResolver.resolveDeletedAccountStatusId())
				.orElseThrow(() -> new BillingAccountNotFoundException(custId, accountId));

		// Onboarding'de acilan varsayilan CUST_ACCT tipi hesap "fatura hesabi" degildir, hicbir
		// zaman FR-011 ile silinemez.
		rules.ensureAccountIsBillingType(account, lookupResolver.resolveBillingAccountTypeId());

		rules.ensureBillingAccountNotActive(account, lookupResolver.resolveActiveAccountStatusId());

		// ACC-004: order-service entegrasyonu gelene kadar productGuard (NoOpBillingAccountProductGuard)
		// hep false doner - bkz. BillingAccountProductGuard.
		rules.ensureNoLinkedProducts(productGuard.hasLinkedProducts(account.getCustAcctId()));

		account.setAcctStId(lookupResolver.resolveDeletedAccountStatusId());
		customerAccountRepository.save(account);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsAccountByAddressId(Long addressId) {
		return customerAccountRepository.existsByAddressIdAndAcctStIdNotDeleted(addressId,
				lookupResolver.resolveDeletedAccountStatusId());
	}

	@Override
	@Transactional(readOnly = true)
	public AddressBillingAccountsResponse getAccountsByAddressId(Long addressId) {
		Long activeStatusId = lookupResolver.resolveActiveAccountStatusId();
		List<CustomerAccountResponse> accounts = customerAccountRepository
				.findByAddressIdAndAcctStIdNotDeleted(addressId, lookupResolver.resolveDeletedAccountStatusId())
				.stream()
				.map(account -> customerMapper.toResponse(account, activeStatusId))
				.toList();
		return new AddressBillingAccountsResponse(accounts.size(), accounts);
	}

	@Override
	public void ensureNoActiveBillingAccount(List<CustomerAccount> accounts) {
		rules.ensureNoActiveBillingAccount(accounts, lookupResolver.resolveBillingAccountTypeId(),
				lookupResolver.resolveActiveAccountStatusId());
	}

	@Override
	public void ensureNoBillingAccountWithLinkedProducts(List<CustomerAccount> accounts) {
		Long billingAccountTypeId = lookupResolver.resolveBillingAccountTypeId();
		boolean hasLinkedProducts = accounts.stream()
				.filter(account -> billingAccountTypeId.equals(account.getAccountTpId()))
				.anyMatch(account -> productGuard.hasLinkedProducts(account.getCustAcctId()));
		rules.ensureNoLinkedProducts(hasLinkedProducts);
	}
}
