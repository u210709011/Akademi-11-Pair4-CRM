package com.etiya.crm.customerservice.business.concretes;

import org.springframework.stereotype.Component;

import com.etiya.crm.customerservice.business.exceptions.AccountNumberCollisionException;
import com.etiya.crm.customerservice.constants.AccountDefaults;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerAccountRepository;

import lombok.RequiredArgsConstructor;

/**
 * B-06: cust_acct.acct_no uretimini TEK bir yerde toplar - onceden onboarding'in varsayilan
 * hesabi custId'den, billing account'lar custAcctId'den turetiyordu; iki ayri sequence oldugu
 * icin (cust_id/cust_acct_id) ayni sayiya denk gelip UNIQUE constraint'i kirabiliyordu. Artik
 * HERKES custAcctId (cust_acct'in kendi PK'si) kullanir - kendi tablosunun PK'si oldugu icin
 * yapisal olarak asla cakismaz. existsByAccountNo kontrolu (eskiden hic cagrilmiyordu) sadece
 * savunma amacli: bu artik "olmamasi gereken" bir durumu net bir hataya cevirir.
 */
@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {

	private final CustomerAccountRepository customerAccountRepository;

	public String generate(Long custAcctId) {
		String accountNo = AccountDefaults.formatAccountNo(custAcctId);
		if (customerAccountRepository.existsByAccountNo(accountNo)) {
			throw new AccountNumberCollisionException(accountNo);
		}
		return accountNo;
	}
}
