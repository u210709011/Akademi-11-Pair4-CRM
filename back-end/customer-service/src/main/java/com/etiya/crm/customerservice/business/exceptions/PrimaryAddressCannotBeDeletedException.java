package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

/** FR-005 ACC-009: birincil (primary) adres silinemez. */
public class PrimaryAddressCannotBeDeletedException extends BusinessException {

	public PrimaryAddressCannotBeDeletedException() {
		super(MessageKeys.PRIMARY_ADDRESS_CANNOT_BE_DELETED);
	}
}
