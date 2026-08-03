package com.etiya.crm.orderservice.business.exceptions;

import com.etiya.crm.orderservice.constants.MessageKeys;

public class AccountNotBelongToCustomerException extends BusinessException {

	public AccountNotBelongToCustomerException(Long custAcctId) {
		super(MessageKeys.ACCOUNT_NOT_BELONG_TO_CUSTOMER, custAcctId);
	}
}
