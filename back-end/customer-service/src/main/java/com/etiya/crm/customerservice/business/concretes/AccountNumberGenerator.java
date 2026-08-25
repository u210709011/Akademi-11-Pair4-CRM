package com.etiya.crm.customerservice.business.concretes;

import org.springframework.stereotype.Component;

import com.etiya.crm.customerservice.business.exceptions.AccountNumberCollisionException;
import com.etiya.crm.customerservice.constants.AccountDefaults;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerAccountRepository;

import lombok.RequiredArgsConstructor;

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
