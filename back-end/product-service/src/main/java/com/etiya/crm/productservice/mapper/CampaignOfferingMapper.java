package com.etiya.crm.productservice.mapper;

import com.etiya.crm.productservice.business.dtos.requests.CampaignOffering.CreateCampaignOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.CampaignOffering.UpdateCampaignOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.CreatedCampaignOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.GetAllCampaignOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.GetCampaignOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.UpdatedCampaignOfferingResponse;
import com.etiya.crm.productservice.entities.concretes.CampaignOffering;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

public interface CampaignOfferingMapper {

    // ---------- CREATE ----------

    /**
     * Request'ten entity uretir. Iki iliski (campaign, productOffering) ve
     * donmus isim (productOfferingName) burada doldurulmaz - hepsini Manager halleder.
     * (productOfferingName teklifin adindan kopyalanacak.)
     */
    @Mapping(target = "campaignOfferingId", ignore = true)
    @Mapping(target = "campaign", ignore = true)
    @Mapping(target = "productOffering", ignore = true)
    @Mapping(target = "productOfferingName", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    CampaignOffering toEntity(CreateCampaignOfferingRequest request);

    /** Entity -> response. Iki iliskiden ID cekiliyor; donmus isim direkt kopyalaniyor. */
    @Mapping(target = "campaignId", source = "campaign.campaignId")
    @Mapping(target = "productOfferingId", source = "productOffering.productOfferingId")
    CreatedCampaignOfferingResponse toCreatedResponse(CampaignOffering campaignOffering);


    // ---------- UPDATE ----------

    @Mapping(target = "campaignOfferingId", ignore = true)
    @Mapping(target = "campaign", ignore = true)
    @Mapping(target = "productOffering", ignore = true)
    @Mapping(target = "productOfferingName", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    void updateEntityFromRequest(UpdateCampaignOfferingRequest request, @MappingTarget CampaignOffering campaignOffering);

    @Mapping(target = "campaignId", source = "campaign.campaignId")
    @Mapping(target = "productOfferingId", source = "productOffering.productOfferingId")
    UpdatedCampaignOfferingResponse toUpdatedResponse(CampaignOffering campaignOffering);


    // ---------- GET ----------

    @Mapping(target = "campaignId", source = "campaign.campaignId")
    @Mapping(target = "productOfferingId", source = "productOffering.productOfferingId")
    GetCampaignOfferingResponse toGetResponse(CampaignOffering campaignOffering);

    @Mapping(target = "campaignId", source = "campaign.campaignId")
    @Mapping(target = "productOfferingId", source = "productOffering.productOfferingId")
    GetAllCampaignOfferingResponse toGetAllResponse(CampaignOffering campaignOffering);

    List<GetAllCampaignOfferingResponse> toGetAllResponseList(List<CampaignOffering> campaignOfferings);



}
