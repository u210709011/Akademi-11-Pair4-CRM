package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.CampaignOfferingService;
import com.etiya.crm.productservice.business.dtos.requests.CampaignOffering.CreateCampaignOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.CampaignOffering.UpdateCampaignOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.CreatedCampaignOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.GetAllCampaignOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.GetCampaignOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.UpdatedCampaignOfferingResponse;
import com.etiya.crm.productservice.dataAccess.abstracts.CampaignOfferingRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.CampaignRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductOfferingRepository;
import com.etiya.crm.productservice.entities.concretes.Campaign;
import com.etiya.crm.productservice.entities.concretes.CampaignOffering;
import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import com.etiya.crm.productservice.mapper.CampaignOfferingMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignOfferingManager implements CampaignOfferingService {

    private final CampaignOfferingRepository campaignOfferingRepository;
    private final CampaignRepository campaignRepository;
    private final ProductOfferingRepository productOfferingRepository;
    private final CampaignOfferingMapper campaignOfferingMapper;

    public CampaignOfferingManager(CampaignOfferingRepository campaignOfferingRepository, CampaignRepository campaignRepository, ProductOfferingRepository productOfferingRepository, CampaignOfferingMapper campaignOfferingMapper) {
        this.campaignOfferingRepository = campaignOfferingRepository;
        this.campaignRepository = campaignRepository;
        this.productOfferingRepository = productOfferingRepository;
        this.campaignOfferingMapper = campaignOfferingMapper;
    }


    @Override
    public CreatedCampaignOfferingResponse create(CreateCampaignOfferingRequest request) {
        // 1) İlişki 1: campaignId ile Campaign nesnesini bul
        Campaign campaign = campaignRepository.findById(request.getCampaignId())
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait kampanya bulunamadı! : " + request.getCampaignId()));

        // 2) İlişki 2: productOfferingId ile ProductOffering nesnesini bul
        ProductOffering productOffering = productOfferingRepository.findById(request.getProductOfferingId())
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait teklif bulunamadı! : " + request.getProductOfferingId()));

        CampaignOffering campaignOffering = campaignOfferingMapper.toEntity(request);

        campaignOffering.setCampaign(campaign);
        campaignOffering.setProductOffering(productOffering);
        campaignOffering.setProductOfferingName(productOffering.getName());

        CampaignOffering saved = campaignOfferingRepository.save(campaignOffering);
        return campaignOfferingMapper.toCreatedResponse(saved);

    }

    @Override
    public UpdatedCampaignOfferingResponse update(Long campaignOfferingId, UpdateCampaignOfferingRequest request) {
        CampaignOffering campaignOffering = campaignOfferingRepository.findById(campaignOfferingId)
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait eşleşme bulunamadı! : " + campaignOfferingId));

        campaignOfferingMapper.updateEntityFromRequest(request, campaignOffering);

        Campaign campaign = campaignRepository.findById(request.getCampaignId())
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait kampanya bulunamadı! : " + request.getCampaignId()));
        campaignOffering.setCampaign(campaign);

        ProductOffering productOffering = productOfferingRepository.findById(request.getProductOfferingId())
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait teklif bulunamadı! : " + request.getProductOfferingId()));
        campaignOffering.setProductOffering(productOffering);

        campaignOffering.setProductOfferingName(productOffering.getName());

        CampaignOffering saved = campaignOfferingRepository.save(campaignOffering);
        return campaignOfferingMapper.toUpdatedResponse(saved);
    }

    @Override
    public GetCampaignOfferingResponse getById(Long campaignOfferingId) {
        CampaignOffering campaignOffering = campaignOfferingRepository.findById(campaignOfferingId)
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait eşleşme bulunamadı! : " + campaignOfferingId));

        return campaignOfferingMapper.toGetResponse(campaignOffering);
    }

    @Override
    public List<GetAllCampaignOfferingResponse> getAll() {
        List<CampaignOffering> campaignOfferings = campaignOfferingRepository.findAll();
        return campaignOfferingMapper.toGetAllResponseList(campaignOfferings);
    }

    @Override
    public void delete(Long campaignOfferingId) {
        CampaignOffering campaignOffering = campaignOfferingRepository.findById(campaignOfferingId)
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait eşleşme bulunamadı! : " + campaignOfferingId));

        campaignOfferingRepository.delete(campaignOffering);
    }
}
