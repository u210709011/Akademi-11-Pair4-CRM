package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.CampaignService;
import com.etiya.crm.productservice.business.dtos.requests.Campaign.CreateCampaignRequest;
import com.etiya.crm.productservice.business.dtos.requests.Campaign.UpdateCampaignRequest;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.CreatedCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.GetAllCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.GetCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.UpdatedCampaignResponse;
import com.etiya.crm.productservice.dataAccess.abstracts.CampaignRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductCatalogRepository;
import com.etiya.crm.productservice.entities.concretes.Campaign;
import com.etiya.crm.productservice.entities.concretes.ProductCatalog;
import com.etiya.crm.productservice.mapper.CampaignMapper;
import com.etiya.crm.productservice.mapper.ProductCatalogMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignManager implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;

    public CampaignManager(CampaignRepository campaignRepository, CampaignMapper campaignMapper) {
        this.campaignRepository = campaignRepository;
        this.campaignMapper = campaignMapper;
    }


    @Override
    public CreatedCampaignResponse create(CreateCampaignRequest request) {
        Campaign campaign = campaignMapper.toEntity(request);
        Campaign saved = campaignRepository.save(campaign);
        return campaignMapper.toCreatedResponse(saved);
    }

    @Override
    public UpdatedCampaignResponse update(Long campaignId, UpdateCampaignRequest request) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Girilen id' ye ait kampanya bulunamadi! : " +campaignId));

        campaignMapper.updateEntityFromRequest(request, campaign);
        Campaign saved = campaignRepository.save(campaign);
        return campaignMapper.toUpdatedResponse(saved);
    }

    @Override
    public GetCampaignResponse getById(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Girilen id' ye ait kampanya bulunamadi! : " +campaignId));
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
                .orElseThrow(() -> new RuntimeException("Girilen id' ye ait kampanya bulunamadi! : " +campaignId));

        campaignRepository.delete(campaign);
    }
}
