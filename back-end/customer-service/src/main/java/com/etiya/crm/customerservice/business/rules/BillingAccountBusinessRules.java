package com.etiya.crm.customerservice.business.rules;

import java.util.List;

import org.springframework.stereotype.Component;

import com.etiya.crm.customerservice.business.dtos.requests.AddressInfo;
import com.etiya.crm.customerservice.business.exceptions.BillingAccountActiveCannotBeDeletedException;
import com.etiya.crm.customerservice.business.exceptions.BillingAccountAddressRequiredException;
import com.etiya.crm.customerservice.business.exceptions.CustomerHasActiveBillingAccountException;
import com.etiya.crm.customerservice.business.exceptions.DefaultAccountCannotBeDeletedException;
import com.etiya.crm.customerservice.entities.concretes.CustomerAccount;

/** Fatura hesabi (BILL_ACCT) CRUD'una ozel FR-007..011 kurallari. */
@Component
public class BillingAccountBusinessRules {

	/** ACC-004/009: fatura hesabi icin ya mevcut bir adres ya da yeni adres bilgisi verilmeli. */
	public void ensureAddressProvided(Long addressId, AddressInfo newAddress) {
		if (addressId == null && newAddress == null) {
			throw new BillingAccountAddressRequiredException();
		}
	}

	/**
	 * FR-007 ACC-003: aktif fatura hesabi (BILL_ACCT tipi, acct_st_id null ya da ACTIVE)
	 * bulunan musteri silinemez. "Fatura hesabi" burada FR-008..011'deki Billing Account
	 * (BILL_ACCT) ile ayni kavram sayilir - onboarding'de acilan varsayilan CUST_ACCT tipi
	 * hesap DAHIL DEGILDIR. BRAIN SS6 madde 2 hala acik soru: ekip varsayilan hesabin da
	 * sayilmasina karar verirse burada sadece billingAccountTypeId filtresi kaldirilir.
	 * Urun guard'i (ACC-004, pasif hesaba bagli urun) order-service bekliyor - TODO,
	 * bu metotta uygulanmiyor.
	 */
	public void ensureNoActiveBillingAccount(List<CustomerAccount> accounts, Long billingAccountTypeId,
			Long activeStatusId) {
		boolean hasActiveBillingAccount = accounts.stream()
				.anyMatch(account -> billingAccountTypeId.equals(account.getAccountTpId())
						&& (account.getAcctStId() == null || activeStatusId.equals(account.getAcctStId())));
		if (hasActiveBillingAccount) {
			throw new CustomerHasActiveBillingAccountException();
		}
	}

	/**
	 * FR-011 ACC-003: aktif (acct_st_id null ya da ACTIVE) fatura hesabi silinemez.
	 * Urun guard'i (ACC-004) order-service bekliyor - TODO, bu metotta uygulanmiyor.
	 */
	public void ensureBillingAccountNotActive(CustomerAccount account, Long activeStatusId) {
		boolean isActive = account.getAcctStId() == null || activeStatusId.equals(account.getAcctStId());
		if (isActive) {
			throw new BillingAccountActiveCannotBeDeletedException();
		}
	}

	/**
	 * FR-011: onboarding'de acilan varsayilan CUST_ACCT tipi hesap fatura hesabi (BILL_ACCT)
	 * degildir, hicbir zaman silinemez - bu kontrol aktiflik guard'indan ONCE calisir.
	 */
	public void ensureAccountIsBillingType(CustomerAccount account, Long billingAccountTypeId) {
		if (!billingAccountTypeId.equals(account.getAccountTpId())) {
			throw new DefaultAccountCannotBeDeletedException();
		}
	}
}
