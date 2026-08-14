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

	/**
	 * ACC-004/009: fatura hesabi icin addressId (mevcut adres) ile newAddress (yeni adres)
	 * alanlarindan TAM OLARAK biri doldurulmali - ikisi de bos ya da ikisi de dolu 400 doner.
	 * Onceden sadece "ikisi de bos" kontrol ediliyordu; "ikisi de dolu" durumunda newAddress
	 * sessizce addressId'yi eziyordu (order-service'teki BasketValidationRules.ensureAddressProvided
	 * ile ayni XOR deseni burada da uygulanir).
	 */
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

	/**
	 * FR-007 ACC-003: aktif fatura hesabi (BILL_ACCT tipi, acct_st_id null ya da ACTIVE)
	 * bulunan musteri silinemez. "Fatura hesabi" burada FR-008..011'deki Billing Account
	 * (BILL_ACCT) ile ayni kavram sayilir - onboarding'de acilan varsayilan CUST_ACCT tipi
	 * hesap DAHIL DEGILDIR. BRAIN SS6 madde 2 hala acik soru: ekip varsayilan hesabin da
	 * sayilmasina karar verirse burada sadece billingAccountTypeId filtresi kaldirilir.
	 * Urun guard'i (ACC-004, pasif hesaba bagli urun) ayri bir kural olan ensureNoLinkedProducts'tadir,
	 * musteri silme akisinda cagrilmaz - bu guard sadece billing account'un kendisi silinirken calisir.
	 */
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

	/**
	 * FR-011: onboarding'de acilan varsayilan CUST_ACCT tipi hesap fatura hesabi (BILL_ACCT)
	 * degildir, hicbir zaman silinemez - bu kontrol aktiflik guard'indan ONCE calisir.
	 */
	public void ensureAccountIsBillingType(CustomerAccount account, Long billingAccountTypeId) {
		if (!billingAccountTypeId.equals(account.getAccountTpId())) {
			throw new DefaultAccountCannotBeDeletedException();
		}
	}

	/**
	 * B-13b: durum degistirme (PATCH status) akisi icin ensureAccountIsBillingType'in ayni
	 * kontrolu ama "silinemez" yerine "durumu degistirilemez" mesaji donen kopyasi -
	 * DefaultAccountCannotBeDeletedException'in silme ve durum degisimi arasinda paylasilmasi
	 * kullaniciya durum degisimi denerken yanlislikla "silinemez" hatasi gosteriyordu.
	 */
	public void ensureAccountIsBillingTypeForStatusChange(CustomerAccount account, Long billingAccountTypeId) {
		if (!billingAccountTypeId.equals(account.getAccountTpId())) {
			throw new DefaultAccountCannotBeChangedException();
		}
	}
}
