package com.etiya.crm.partyservice.business.exceptions;

/** FR-003/FR-004: nationalId baska bir bireyle cakisiyor - caller'a 409 olarak yansitilir. */
public class DuplicateNationalIdException extends BusinessException {

	public DuplicateNationalIdException(String message) {
		super(message);
	}
}
