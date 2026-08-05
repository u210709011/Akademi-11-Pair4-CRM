package com.etiya.crm.productservice.business.exceptions;

import com.etiya.crm.productservice.constants.MessageKeys;

public class CampaignNotFoundException extends BusinessException {

    public CampaignNotFoundException(Long campaignId) {
        super(MessageKeys.CAMPAIGN_NOT_FOUND, campaignId);
    }
}
