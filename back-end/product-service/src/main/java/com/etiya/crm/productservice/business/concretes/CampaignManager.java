package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.CampaignService;
import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.dtos.requests.Campaign.CreateCampaignRequest;
import com.etiya.crm.productservice.business.dtos.requests.Campaign.UpdateCampaignRequest;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.CreatedCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.GetAllCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.GetCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.UpdatedCampaignResponse;
import com.etiya.crm.productservice.business.exceptions.CampaignNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.CampaignRepository;
import com.etiya.crm.productservice.entities.concretes.Campaign;
import com.etiya.crm.productservice.mapper.CampaignMapper;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignManager implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;
    private final LookupCacheService lookupCacheService;

    public CampaignManager(CampaignRepository campaignRepository, CampaignMapper campaignMapper, LookupCacheService lookupCacheService) {
        this.campaignRepository = campaignRepository;
        this.campaignMapper = campaignMapper;
        this.lookupCacheService = lookupCacheService;
    }


    @Override
    public CreatedCampaignResponse create(CreateCampaignRequest request) {
        Campaign campaign = campaignMapper.toEntity(request);
        campaign.setStatusId(
                lookupCacheService.resolveStatusIdByCode(GnlStGroups.CAMPAIGN, request.getStatusCode()));
        Campaign saved = campaignRepository.save(campaign);
        return campaignMapper.toCreatedResponse(saved);
    }

    @Override
    public UpdatedCampaignResponse update(Long campaignId, UpdateCampaignRequest request) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new CampaignNotFoundException(campaignId));

        campaignMapper.updateEntityFromRequest(request, campaign);
        campaign.setStatusId(
                lookupCacheService.resolveStatusIdByCode(GnlStGroups.CAMPAIGN, request.getStatusCode()));
        Campaign saved = campaignRepository.save(campaign);
        return campaignMapper.toUpdatedResponse(saved);
    }

    @Override
    public GetCampaignResponse getById(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new CampaignNotFoundException(campaignId));
        return campaignMapper.toGetResponse(campaign);
    }

    @Override
    public List<GetAllCampaignResponse> getAll() {
        List<Campaign> campaigns = campaignRepository.findAll();
        return campaignMapper.toGetAllResponseList(campaigns);
    }

    @Override
    public void delete(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new CampaignNotFoundException(campaignId));

        campaign.setStatusId(
                lookupCacheService.resolveStatusIdByCode(GnlStGroups.CAMPAIGN, GnlStCodes.DELETED));
        campaignRepository.save(campaign);
    }
}
