package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

/** FR-007 ACC-003: aktif fatura hesabi bulunan musteri silinemez. */
public class CustomerHasActiveBillingAccountException extends BusinessException {

	public CustomerHasActiveBillingAccountException() {
		super(MessageKeys.CUSTOMER_HAS_ACTIVE_BILLING_ACCOUNT);
	}
}
