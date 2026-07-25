package com.etiya.crm.partyservice.business.exceptions;

import com.etiya.crm.partyservice.constants.MessageKeys;

public class PartyNotFoundException extends BusinessException {

	public PartyNotFoundException(Long partyId) {
		super(MessageKeys.PARTY_NOT_FOUND, partyId);
	}
}
