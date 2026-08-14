package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.CampaignService;
import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.abstracts.TranslationService;
import com.etiya.crm.productservice.business.dtos.requests.Campaign.CreateCampaignRequest;
import com.etiya.crm.productservice.business.dtos.requests.Campaign.UpdateCampaignRequest;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.CreatedCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.GetAllCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.GetCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.UpdatedCampaignResponse;
import com.etiya.crm.productservice.business.exceptions.CampaignNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.CampaignRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.CampaignSpecifications;
import com.etiya.crm.productservice.entities.concretes.Campaign;
import com.etiya.crm.productservice.mapper.CampaignMapper;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CampaignManager implements CampaignService {

    private static final String ENTITY_NAME = "CMPG";

    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;
    private final LookupCacheService lookupCacheService;
    private final TranslationService translationService;

    public CampaignManager(CampaignRepository campaignRepository, CampaignMapper campaignMapper, LookupCacheService lookupCacheService, TranslationService translationService) {
        this.campaignRepository = campaignRepository;
        this.campaignMapper = campaignMapper;
        this.lookupCacheService = lookupCacheService;
        this.translationService = translationService;
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
        GetCampaignResponse response = campaignMapper.toGetResponse(campaign);
        applyTranslation(response);
        return response;
    }

    @Override
    public Page<GetAllCampaignResponse> getAll(Long campaignId, String name, Pageable pageable) {
        Page<Campaign> campaigns = campaignRepository.findAll(CampaignSpecifications.search(campaignId, name), pageable);
        return campaigns.map(campaign -> {
            GetAllCampaignResponse response = campaignMapper.toGetAllResponse(campaign);
            applyTranslation(response);
            return response;
        });
    }

    /** name/descr taban degerleri Ingilizce'dir - bkz. lookup-service GnlTpManager (ayni desen). */
    private void applyTranslation(GetCampaignResponse response) {
        response.setName(translationService.translate(ENTITY_NAME, response.getCampaignId(), "NAME", response.getName()));
        response.setDescr(translationService.translate(ENTITY_NAME, response.getCampaignId(), "DESCR", response.getDescr()));
    }

    private void applyTranslation(GetAllCampaignResponse response) {
        response.setName(translationService.translate(ENTITY_NAME, response.getCampaignId(), "NAME", response.getName()));
        response.setDescr(translationService.translate(ENTITY_NAME, response.getCampaignId(), "DESCR", response.getDescr()));
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
