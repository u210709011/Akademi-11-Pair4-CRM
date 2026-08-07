package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.CampaignOfferingService;
import com.etiya.crm.productservice.business.dtos.requests.CampaignOffering.CreateCampaignOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.CampaignOffering.UpdateCampaignOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.CreatedCampaignOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.GetAllCampaignOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.GetCampaignOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.UpdatedCampaignOfferingResponse;
import com.etiya.crm.productservice.business.exceptions.CampaignNotFoundException;
import com.etiya.crm.productservice.business.exceptions.CampaignOfferingNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductOfferingNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.CampaignOfferingRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.CampaignRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductOfferingRepository;
import com.etiya.crm.productservice.entities.concretes.Campaign;
import com.etiya.crm.productservice.entities.concretes.CampaignOffering;
import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import com.etiya.crm.productservice.mapper.CampaignOfferingMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
                .orElseThrow(() -> new CampaignNotFoundException(request.getCampaignId()));

        // 2) İlişki 2: productOfferingId ile ProductOffering nesnesini bul
        ProductOffering productOffering = productOfferingRepository.findById(request.getProductOfferingId())
                .orElseThrow(() -> new ProductOfferingNotFoundException(request.getProductOfferingId()));

        CampaignOffering campaignOffering = campaignOfferingMapper.toEntity(request);

        campaignOffering.setCampaign(campaign);
        campaignOffering.setProductOffering(productOffering);
        campaignOffering.setProductOfferingName(productOffering.getName());
        CampaignOffering saved = campaignOfferingRepository.save(campaignOffering);

        CreatedCampaignOfferingResponse response = campaignOfferingMapper.toCreatedResponse(saved);
        response.setDiscountedPrice(calculateDiscountedPrice(productOffering.getTotalPrice(), saved.getDiscountPct()));
        return response;

    }

    @Override
    public UpdatedCampaignOfferingResponse update(Long campaignOfferingId, UpdateCampaignOfferingRequest request) {
        CampaignOffering campaignOffering = campaignOfferingRepository.findById(campaignOfferingId)
                .orElseThrow(() -> new CampaignOfferingNotFoundException(campaignOfferingId));

        campaignOfferingMapper.updateEntityFromRequest(request, campaignOffering);

        Campaign campaign = campaignRepository.findById(request.getCampaignId())
                .orElseThrow(() -> new CampaignNotFoundException(request.getCampaignId()));
        campaignOffering.setCampaign(campaign);

        ProductOffering productOffering = productOfferingRepository.findById(request.getProductOfferingId())
                .orElseThrow(() -> new ProductOfferingNotFoundException(request.getProductOfferingId()));
        campaignOffering.setProductOffering(productOffering);

        campaignOffering.setProductOfferingName(productOffering.getName());

        CampaignOffering saved = campaignOfferingRepository.save(campaignOffering);
        UpdatedCampaignOfferingResponse response = campaignOfferingMapper.toUpdatedResponse(saved);
        response.setDiscountedPrice(calculateDiscountedPrice(productOffering.getTotalPrice(), saved.getDiscountPct()));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public GetCampaignOfferingResponse getById(Long campaignOfferingId) {
        CampaignOffering campaignOffering = campaignOfferingRepository.findById(campaignOfferingId)
                .orElseThrow(() -> new CampaignOfferingNotFoundException(campaignOfferingId));

        GetCampaignOfferingResponse response = campaignOfferingMapper.toGetResponse(campaignOffering);
        response.setDiscountedPrice(calculateDiscountedPrice(
                campaignOffering.getProductOffering().getTotalPrice(), campaignOffering.getDiscountPct()));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetAllCampaignOfferingResponse> getAll() {
        List<CampaignOffering> campaignOfferings = campaignOfferingRepository.findAll();
        List<GetAllCampaignOfferingResponse> responses = campaignOfferingMapper.toGetAllResponseList(campaignOfferings);

        for (int i = 0; i < campaignOfferings.size(); i++) {
            BigDecimal discountedPrice = calculateDiscountedPrice(
                    campaignOfferings.get(i).getProductOffering().getTotalPrice(),
                    campaignOfferings.get(i).getDiscountPct());
            responses.get(i).setDiscountedPrice(discountedPrice);
        }
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetAllCampaignOfferingResponse> getByCampaignId(Long campaignId) {
        campaignRepository.findById(campaignId)
                .orElseThrow(() -> new CampaignNotFoundException(campaignId));

        List<CampaignOffering> campaignOfferings = campaignOfferingRepository.findByCampaign_CampaignId(campaignId);
        List<GetAllCampaignOfferingResponse> responses = campaignOfferingMapper.toGetAllResponseList(campaignOfferings);

        for (int i = 0; i < campaignOfferings.size(); i++) {
            BigDecimal discountedPrice = calculateDiscountedPrice(
                    campaignOfferings.get(i).getProductOffering().getTotalPrice(),
                    campaignOfferings.get(i).getDiscountPct());
            responses.get(i).setDiscountedPrice(discountedPrice);
        }
        return responses;
    }

    @Override
    public void delete(Long campaignOfferingId) {
        CampaignOffering campaignOffering = campaignOfferingRepository.findById(campaignOfferingId)
                .orElseThrow(() -> new CampaignOfferingNotFoundException(campaignOfferingId));

        campaignOffering.setActive(false);
        campaignOfferingRepository.save(campaignOffering);
    }

    private BigDecimal calculateDiscountedPrice(BigDecimal totalPrice, BigDecimal discountPct) {
        BigDecimal multiplier = BigDecimal.ONE.subtract(
                discountPct.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        return totalPrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }
}
