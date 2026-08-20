package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

/** FR-011/B-13b: onboarding'de acilan varsayilan (CUST_ACCT tipi) hesabin durumu degistirilemez. */
public class DefaultAccountCannotBeChangedException extends BusinessException {

	public DefaultAccountCannotBeChangedException() {
		super(MessageKeys.DEFAULT_ACCOUNT_CANNOT_BE_CHANGED);
	}
}
