package com.etiya.crm.productservice.business.exceptions;
import com.etiya.crm.productservice.constants.MessageKeys;

public class CampaignOfferingNotFoundException extends BusinessException {
    public CampaignOfferingNotFoundException(Long campaignOfferingId) {
        super(MessageKeys.CAMPAIGN_OFFERING_NOT_FOUND, campaignOfferingId);
    }
}