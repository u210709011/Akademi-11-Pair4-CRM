package com.etiya.crm.productservice.business.abstracts;

import com.etiya.crm.productservice.business.dtos.requests.CampaignOffering.CreateCampaignOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.CampaignOffering.UpdateCampaignOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.CreatedCampaignOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.GetAllCampaignOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.GetCampaignOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.UpdatedCampaignOfferingResponse;

import java.util.List;

public interface CampaignOfferingService {

    CreatedCampaignOfferingResponse create(CreateCampaignOfferingRequest request);

    UpdatedCampaignOfferingResponse update(Long campaignOfferingId, UpdateCampaignOfferingRequest request);

    GetCampaignOfferingResponse getById(Long campaignOfferingId);

    List<GetAllCampaignOfferingResponse> getAll();

    void delete(Long campaignOfferingId);
}
