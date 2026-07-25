package com.etiya.crm.partyservice.business.exceptions;

import com.etiya.crm.partyservice.constants.MessageKeys;

/** FR-003/FR-004: nationalId baska bir bireyle cakisiyor - caller'a 409 olarak yansitilir. */
public class DuplicateNationalIdException extends BusinessException {

	public DuplicateNationalIdException() {
		super(MessageKeys.DUPLICATE_NATIONAL_ID);
	}
}
