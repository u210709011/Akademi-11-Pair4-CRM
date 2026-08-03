package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

/** FR-010/FR-011 IDOR: accountId, custId'nin kendi hesaplari arasinda degilse. */
public class BillingAccountNotFoundException extends BusinessException {

	public BillingAccountNotFoundException(Long custId, Long accountId) {
		super(MessageKeys.BILLING_ACCOUNT_NOT_FOUND, custId, accountId);
	}
}
