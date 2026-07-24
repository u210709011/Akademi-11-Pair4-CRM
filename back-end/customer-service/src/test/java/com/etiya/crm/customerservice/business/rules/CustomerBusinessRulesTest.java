package com.etiya.crm.customerservice.business.rules;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.etiya.crm.customerservice.business.exceptions.BillingAccountActiveCannotBeDeletedException;
import com.etiya.crm.customerservice.business.exceptions.CustomerHasActiveBillingAccountException;
import com.etiya.crm.customerservice.entities.concretes.CustomerAccount;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * FR-007/FR-011: acct_st_id nullable (migration oncesi kayitlar null gelir).
 * Guard'lar fail-closed olmali - null, "durum bilinmiyor" degil "ACTIVE
 * varsay" anlamina gelir, aksi halde eski (migration oncesi) bir fatura
 * hesabi acct_st_id=null oldugu icin "pasif" sanilip yanlislikla silinebilir/
 * musteri yanlislikla silinebilir hale gelir.
 */
class CustomerBusinessRulesTest {

	private static final Long BILL_ACCT_TYPE_ID = 500L;
	private static final Long OTHER_TYPE_ID = 501L;
	private static final Long ACTIVE_STATUS_ID = 601L;
	private static final Long PASSIVE_STATUS_ID = 602L;

	private final CustomerBusinessRules rules = new CustomerBusinessRules();

	// --- FR-011: ensureBillingAccountNotActive (tek hesap) ---

	@Test
	void ensureBillingAccountNotActive_throws_whenAcctStIdIsNull() {
		CustomerAccount account = billingAccount(null);

		assertThatThrownBy(() -> rules.ensureBillingAccountNotActive(account, ACTIVE_STATUS_ID))
				.isInstanceOf(BillingAccountActiveCannotBeDeletedException.class);
	}

	@Test
	void ensureBillingAccountNotActive_throws_whenAcctStIdIsActive() {
		CustomerAccount account = billingAccount(ACTIVE_STATUS_ID);

		assertThatThrownBy(() -> rules.ensureBillingAccountNotActive(account, ACTIVE_STATUS_ID))
				.isInstanceOf(BillingAccountActiveCannotBeDeletedException.class);
	}

	@Test
	void ensureBillingAccountNotActive_passes_whenAcctStIdIsPassive() {
		CustomerAccount account = billingAccount(PASSIVE_STATUS_ID);

		assertThatCode(() -> rules.ensureBillingAccountNotActive(account, ACTIVE_STATUS_ID))
				.doesNotThrowAnyException();
	}

	// --- FR-007: ensureNoActiveBillingAccount (musterinin tum hesaplari) ---

	@Test
	void ensureNoActiveBillingAccount_throws_whenBillingAccountAcctStIdIsNull() {
		List<CustomerAccount> accounts = List.of(billingAccount(null));

		assertThatThrownBy(() -> rules.ensureNoActiveBillingAccount(accounts, BILL_ACCT_TYPE_ID, ACTIVE_STATUS_ID))
				.isInstanceOf(CustomerHasActiveBillingAccountException.class);
	}

	@Test
	void ensureNoActiveBillingAccount_throws_whenBillingAccountIsActive() {
		List<CustomerAccount> accounts = List.of(billingAccount(ACTIVE_STATUS_ID));

		assertThatThrownBy(() -> rules.ensureNoActiveBillingAccount(accounts, BILL_ACCT_TYPE_ID, ACTIVE_STATUS_ID))
				.isInstanceOf(CustomerHasActiveBillingAccountException.class);
	}

	@Test
	void ensureNoActiveBillingAccount_passes_whenBillingAccountIsPassive() {
		List<CustomerAccount> accounts = List.of(billingAccount(PASSIVE_STATUS_ID));

		assertThatCode(() -> rules.ensureNoActiveBillingAccount(accounts, BILL_ACCT_TYPE_ID, ACTIVE_STATUS_ID))
				.doesNotThrowAnyException();
	}

	@Test
	void ensureNoActiveBillingAccount_passes_whenOnlyNonBillingAccountsExist() {
		// Varsayilan (CUST_ACCT) hesap acct_st_id=null olsa bile guard'i tetiklemez -
		// filtre accountTpId=BILL_ACCT'e gore calisir (bkz. BRAIN SS6 madde 2, acik soru).
		List<CustomerAccount> accounts = List.of(account(OTHER_TYPE_ID, null));

		assertThatCode(() -> rules.ensureNoActiveBillingAccount(accounts, BILL_ACCT_TYPE_ID, ACTIVE_STATUS_ID))
				.doesNotThrowAnyException();
	}

	private CustomerAccount billingAccount(Long acctStId) {
		return account(BILL_ACCT_TYPE_ID, acctStId);
	}

	private CustomerAccount account(Long accountTpId, Long acctStId) {
		CustomerAccount account = new CustomerAccount();
		account.setAccountTpId(accountTpId);
		account.setAcctStId(acctStId);
		return account;
	}
}
