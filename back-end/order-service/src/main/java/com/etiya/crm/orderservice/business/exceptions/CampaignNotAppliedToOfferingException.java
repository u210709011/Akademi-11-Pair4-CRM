package com.etiya.crm.orderservice.business.exceptions;

import com.etiya.crm.orderservice.constants.MessageKeys;

public class CampaignNotAppliedToOfferingException extends BusinessException {

	public CampaignNotAppliedToOfferingException(Long cmpgId, Long prodOfrId) {
		super(MessageKeys.CAMPAIGN_NOT_APPLIED_TO_OFFERING, cmpgId, prodOfrId);
	}
}
