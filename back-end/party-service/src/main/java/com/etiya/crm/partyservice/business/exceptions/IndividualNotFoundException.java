package com.etiya.crm.partyservice.business.exceptions;

import com.etiya.crm.partyservice.constants.MessageKeys;

public class IndividualNotFoundException extends BusinessException {

	public IndividualNotFoundException(Long partyRoleId) {
		super(MessageKeys.INDIVIDUAL_NOT_FOUND_FOR_PARTY_ROLE, partyRoleId);
	}
}
