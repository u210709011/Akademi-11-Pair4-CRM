package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

/** ACC-010: KPS dogrulamasi basarisiz olursa kayit ilerleyemez. */
public class IdentityVerificationFailedException extends BusinessException {

	public IdentityVerificationFailedException() {
		super(MessageKeys.IDENTITY_VERIFICATION_FAILED);
	}
}
