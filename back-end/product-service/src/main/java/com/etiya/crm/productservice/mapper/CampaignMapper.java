package com.etiya.crm.productservice.mapper;

import com.etiya.crm.productservice.business.dtos.requests.Campaign.CreateCampaignRequest;
import com.etiya.crm.productservice.business.dtos.requests.Campaign.UpdateCampaignRequest;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.CreatedCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.GetAllCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.GetCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.UpdatedCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.GetAllProductCatalogResponse;
import com.etiya.crm.productservice.constants.ProductServiceDefaults;
import com.etiya.crm.productservice.entities.concretes.Campaign;
import com.etiya.crm.productservice.entities.concretes.ProductCatalog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CampaignMapper {

    /** Gelen istekten yeni entity uretir. Id ve audit alanlari disarida birakilir. */
    @Mapping(target = "campaignId", ignore = true)
    @Mapping(target = "statusId", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    Campaign toEntity(CreateCampaignRequest request);

    @Mapping(target = "campaignNo", expression = "java(formatNo(campaign.getCampaignId()))")
    CreatedCampaignResponse toCreatedResponse(Campaign campaign);

    // ---------- UPDATE ----------

    /**
     * Var olan entity'nin uzerine yazar (yeni nesne uretmez).
     * @MappingTarget sayesinde productSpecId ve audit alanlari korunur.
     */
    @Mapping(target = "campaignId", ignore = true)
    @Mapping(target = "statusId", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    void updateEntityFromRequest(UpdateCampaignRequest request, @MappingTarget Campaign campaign);

    @Mapping(target = "campaignNo", expression = "java(formatNo(campaign.getCampaignId()))")
    UpdatedCampaignResponse toUpdatedResponse(Campaign campaign);

    // ---------- GET ----------

    @Mapping(target = "campaignNo", expression = "java(formatNo(campaign.getCampaignId()))")
    GetCampaignResponse toGetResponse(Campaign campaign);

    @Mapping(target = "campaignNo", expression = "java(formatNo(campaign.getCampaignId()))")
    GetAllCampaignResponse toGetAllResponse(Campaign campaign);

    /** Liste donusumu: MapStruct tekil metodu bulup her eleman icin calistirir. */
    List<GetAllCampaignResponse> toGetAllResponseList(List<Campaign> campaigns);

    /** customer-service AccountDefaults.formatAccountNo ile ayni kural (bkz. ProductServiceDefaults). */
    default String formatNo(Long campaignId) {
        return campaignId == null ? null : ProductServiceDefaults.formatNo(campaignId);
    }
}
