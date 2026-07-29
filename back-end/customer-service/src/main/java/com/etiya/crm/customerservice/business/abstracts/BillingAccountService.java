package com.etiya.crm.customerservice.business.abstracts;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.etiya.crm.customerservice.business.dtos.requests.CreateBillingAccountRequest;
import com.etiya.crm.customerservice.business.dtos.requests.UpdateBillingAccountRequest;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerAccountResponse;
import com.etiya.crm.customerservice.entities.concretes.CustomerAccount;

/** Musterinin fatura hesaplarini (CUST_ACCT/BILL_ACCT tipi) ve buna ozel FR-009..011 kurallarini yonetir. */
public interface BillingAccountService {

	Page<CustomerAccountResponse> getAccounts(Long custId, Pageable pageable);

	CustomerAccountResponse createBillingAccount(Long custId, CreateBillingAccountRequest request);

	CustomerAccountResponse updateBillingAccount(Long custId, Long accountId, UpdateBillingAccountRequest request);

	void deleteBillingAccount(Long custId, Long accountId);

	boolean existsAccountByAddressId(Long addressId);

	/** FR-007 ACC-003: musteri silinirken aktif bir fatura hesabi varsa engellenir. */
	void ensureNoActiveBillingAccount(List<CustomerAccount> accounts);
}
