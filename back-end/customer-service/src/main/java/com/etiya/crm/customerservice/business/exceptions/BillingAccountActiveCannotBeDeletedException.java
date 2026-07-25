package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

/** FR-011 ACC-003: aktif fatura hesabi silinemez. */
public class BillingAccountActiveCannotBeDeletedException extends BusinessException {

	public BillingAccountActiveCannotBeDeletedException() {
		super(MessageKeys.BILLING_ACCOUNT_ACTIVE_CANNOT_BE_DELETED);
	}
}
