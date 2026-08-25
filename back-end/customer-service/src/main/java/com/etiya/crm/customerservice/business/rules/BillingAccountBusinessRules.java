package com.etiya.crm.customerservice.business.rules;

import java.util.List;

import org.springframework.stereotype.Component;

import com.etiya.crm.customerservice.business.dtos.requests.AddressInfo;
import com.etiya.crm.customerservice.business.exceptions.BillingAccountActiveCannotBeDeletedException;
import com.etiya.crm.customerservice.business.exceptions.BillingAccountAddressConflictException;
import com.etiya.crm.customerservice.business.exceptions.BillingAccountAddressRequiredException;
import com.etiya.crm.customerservice.business.exceptions.BillingAccountHasActiveProductsException;
import com.etiya.crm.customerservice.business.exceptions.CustomerHasActiveBillingAccountException;
import com.etiya.crm.customerservice.business.exceptions.DefaultAccountCannotBeChangedException;
import com.etiya.crm.customerservice.business.exceptions.DefaultAccountCannotBeDeletedException;
import com.etiya.crm.customerservice.entities.concretes.CustomerAccount;

/** Fatura hesabi (BILL_ACCT) CRUD'una ozel FR-007..011 kurallari. */
@Component
public class BillingAccountBusinessRules {


	public void ensureAddressProvided(Long addressId, AddressInfo newAddress) {
		boolean hasExisting = addressId != null;
		boolean hasNew = newAddress != null;
		if (hasExisting == hasNew) {
			throw hasExisting ? new BillingAccountAddressConflictException() : new BillingAccountAddressRequiredException();
		}
	}

	/** ACC-004: pasif hesaba bagli aktif urun varsa fatura hesabi silinemez - bkz. BillingAccountProductGuard. */
	public void ensureNoLinkedProducts(boolean hasLinkedProducts) {
		if (hasLinkedProducts) {
			throw new BillingAccountHasActiveProductsException();
		}
	}

	/** acct_st_id null-safe aktiflik yorumu: null ya da activeStatusId ise aktif sayilir (bkz. sinif ustu not). */
	public boolean isActive(CustomerAccount account, Long activeStatusId) {
		return account.getAcctStId() == null || activeStatusId.equals(account.getAcctStId());
	}


	public void ensureNoActiveBillingAccount(List<CustomerAccount> accounts, Long billingAccountTypeId,
			Long activeStatusId) {
		boolean hasActiveBillingAccount = accounts.stream()
				.anyMatch(account -> billingAccountTypeId.equals(account.getAccountTpId())
						&& isActive(account, activeStatusId));
		if (hasActiveBillingAccount) {
			throw new CustomerHasActiveBillingAccountException();
		}
	}

	/** FR-011 ACC-003: aktif (acct_st_id null ya da ACTIVE) fatura hesabi silinemez. */
	public void ensureBillingAccountNotActive(CustomerAccount account, Long activeStatusId) {
		if (isActive(account, activeStatusId)) {
			throw new BillingAccountActiveCannotBeDeletedException();
		}
	}

	public void ensureAccountIsBillingType(CustomerAccount account, Long billingAccountTypeId) {
		if (!billingAccountTypeId.equals(account.getAccountTpId())) {
			throw new DefaultAccountCannotBeDeletedException();
		}
	}


	public void ensureAccountIsBillingTypeForStatusChange(CustomerAccount account, Long billingAccountTypeId) {
		if (!billingAccountTypeId.equals(account.getAccountTpId())) {
			throw new DefaultAccountCannotBeChangedException();
		}
	}
}
