package com.etiya.crm.partyservice.business.exceptions;

import com.etiya.crm.partyservice.constants.MessageKeys;

public class PartyRoleNotFoundException extends BusinessException {

	public PartyRoleNotFoundException(Long partyRoleId) {
		super(MessageKeys.PARTY_ROLE_NOT_FOUND, partyRoleId);
	}
}
