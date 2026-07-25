package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

/** FR-011: onboarding'de acilan varsayilan (CUST_ACCT tipi) hesap fatura hesabi degildir, silinemez. */
public class DefaultAccountCannotBeDeletedException extends BusinessException {

	public DefaultAccountCannotBeDeletedException() {
		super(MessageKeys.DEFAULT_ACCOUNT_CANNOT_BE_DELETED);
	}
}
