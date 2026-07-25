package com.etiya.crm.partyservice.business.exceptions;

import com.etiya.crm.partyservice.constants.MessageKeys;

public class LookupValueNotFoundException extends BusinessException {

	public LookupValueNotFoundException(String entCodeName, String shrtCode) {
		super(MessageKeys.LOOKUP_VALUE_NOT_FOUND, entCodeName, shrtCode);
	}
}
