package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.CampaignOfferingService;
import com.etiya.crm.productservice.business.abstracts.TranslationService;
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
import com.etiya.crm.productservice.utils.DiscountPriceCalculator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CampaignOfferingManager implements CampaignOfferingService {

    // productOfferingName write-time'da donduruldugu icin (bkz. create/update - productOffering.getName()
    // o anin degeriyle kopyalanir), gnl_tp'deki role ile ayni sorun: PROD_OFR ceviri satirlarini
    // (V21) read-time'da yeniden uygulamazsak, kampanyalardaki teklif adi katalog sekmesindeki
    // (canli cevrilen) adla senkron kalmaz.
    private static final String PRODUCT_OFFERING_ENTITY_NAME = "PROD_OFR";

    private final CampaignOfferingRepository campaignOfferingRepository;
    private final CampaignRepository campaignRepository;
    private final ProductOfferingRepository productOfferingRepository;
    private final CampaignOfferingMapper campaignOfferingMapper;
    private final DiscountPriceCalculator discountPriceCalculator;
    private final TranslationService translationService;

    public CampaignOfferingManager(CampaignOfferingRepository campaignOfferingRepository, CampaignRepository campaignRepository, ProductOfferingRepository productOfferingRepository, CampaignOfferingMapper campaignOfferingMapper, DiscountPriceCalculator discountPriceCalculator, TranslationService translationService) {
        this.campaignOfferingRepository = campaignOfferingRepository;
        this.campaignRepository = campaignRepository;
        this.productOfferingRepository = productOfferingRepository;
        this.campaignOfferingMapper = campaignOfferingMapper;
        this.discountPriceCalculator = discountPriceCalculator;
        this.translationService = translationService;
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
        response.setDiscountedPrice(discountPriceCalculator.calculate(productOffering.getTotalPrice(), saved.getDiscountPct()));
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
        response.setDiscountedPrice(discountPriceCalculator.calculate(productOffering.getTotalPrice(), saved.getDiscountPct()));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public GetCampaignOfferingResponse getById(Long campaignOfferingId) {
        CampaignOffering campaignOffering = campaignOfferingRepository.findById(campaignOfferingId)
                .orElseThrow(() -> new CampaignOfferingNotFoundException(campaignOfferingId));

        GetCampaignOfferingResponse response = campaignOfferingMapper.toGetResponse(campaignOffering);
        response.setDiscountedPrice(discountPriceCalculator.calculate(
                campaignOffering.getProductOffering().getTotalPrice(), campaignOffering.getDiscountPct()));
        applyProductOfferingNameTranslation(response);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetAllCampaignOfferingResponse> getAll() {
        List<CampaignOffering> campaignOfferings = campaignOfferingRepository.findAll();
        List<GetAllCampaignOfferingResponse> responses = campaignOfferingMapper.toGetAllResponseList(campaignOfferings);

        for (int i = 0; i < campaignOfferings.size(); i++) {
            BigDecimal discountedPrice = discountPriceCalculator.calculate(
                    campaignOfferings.get(i).getProductOffering().getTotalPrice(),
                    campaignOfferings.get(i).getDiscountPct());
            responses.get(i).setDiscountedPrice(discountedPrice);
            applyProductOfferingNameTranslation(responses.get(i));
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
            BigDecimal discountedPrice = discountPriceCalculator.calculate(
                    campaignOfferings.get(i).getProductOffering().getTotalPrice(),
                    campaignOfferings.get(i).getDiscountPct());
            responses.get(i).setDiscountedPrice(discountedPrice);
            applyProductOfferingNameTranslation(responses.get(i));
        }
        return responses;
    }

    private void applyProductOfferingNameTranslation(GetCampaignOfferingResponse response) {
        response.setProductOfferingName(translationService.translate(
                PRODUCT_OFFERING_ENTITY_NAME, response.getProductOfferingId(), "NAME", response.getProductOfferingName()));
    }

    private void applyProductOfferingNameTranslation(GetAllCampaignOfferingResponse response) {
        response.setProductOfferingName(translationService.translate(
                PRODUCT_OFFERING_ENTITY_NAME, response.getProductOfferingId(), "NAME", response.getProductOfferingName()));
    }

    @Override
    public void delete(Long campaignOfferingId) {
        CampaignOffering campaignOffering = campaignOfferingRepository.findById(campaignOfferingId)
                .orElseThrow(() -> new CampaignOfferingNotFoundException(campaignOfferingId));

        campaignOffering.setActive(false);
        campaignOfferingRepository.save(campaignOffering);
    }
}
