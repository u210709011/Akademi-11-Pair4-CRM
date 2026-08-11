package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.abstracts.ProductOfferingRelationService;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingRelation.CreateProductOfferingRelationRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingRelation.UpdateProductOfferingRelationRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.CreatedProductOfferingRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.GetAllProductOfferingRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.GetProductOfferingRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.UpdatedProductOfferingRelationResponse;
import com.etiya.crm.productservice.business.exceptions.ProductOfferingNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductOfferingRelationNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductOfferingRelationRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductOfferingRepository;
import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import com.etiya.crm.productservice.entities.concretes.ProductOfferingRelation;
import com.etiya.crm.productservice.mapper.ProductOfferingRelationMapper;
import com.etiya.crm.shared.contracts.gnltp.GnlTpCodes;
import com.etiya.crm.shared.contracts.gnltp.GnlTpGroups;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductOfferingRelationManager implements ProductOfferingRelationService {

    private final ProductOfferingRelationRepository productOfferingRelationRepository;
    private final ProductOfferingRepository productOfferingRepository;
    private final ProductOfferingRelationMapper productOfferingRelationMapper;
    private final LookupCacheService lookupCacheService;

    public ProductOfferingRelationManager(ProductOfferingRelationRepository productOfferingRelationRepository,
                                          ProductOfferingRepository productOfferingRepository,
                                          ProductOfferingRelationMapper productOfferingRelationMapper,
                                          LookupCacheService lookupCacheService) {
        this.productOfferingRelationRepository = productOfferingRelationRepository;
        this.productOfferingRepository = productOfferingRepository;
        this.productOfferingRelationMapper = productOfferingRelationMapper;
        this.lookupCacheService = lookupCacheService;
    }

    @Override
    public CreatedProductOfferingRelationResponse create(CreateProductOfferingRelationRequest request) {
        ProductOffering productOffering1 = productOfferingRepository.findById(request.getProductOfferingId1())
                .orElseThrow(() -> new ProductOfferingNotFoundException(request.getProductOfferingId1()));
        ProductOffering productOffering2 = productOfferingRepository.findById(request.getProductOfferingId2())
                .orElseThrow(() -> new ProductOfferingNotFoundException(request.getProductOfferingId2()));

        ProductOfferingRelation entity = productOfferingRelationMapper.toEntity(request);
        entity.setProductOffering1(productOffering1);
        entity.setProductOffering2(productOffering2);
        entity.setRelationTypeId(
                lookupCacheService.resolveTypeIdByCode(GnlTpGroups.PROD_OFR_REL, request.getRelationTypeCode()));

        ProductOfferingRelation saved = productOfferingRelationRepository.save(entity);
        CreatedProductOfferingRelationResponse response = productOfferingRelationMapper.toCreatedResponse(saved);
        response.setMandatory(GnlTpCodes.MANDATORY.equals(request.getRelationTypeCode()));
        response.setExclusive(GnlTpCodes.EXCL.equals(request.getRelationTypeCode()));
        return response;
    }

    @Override
    public UpdatedProductOfferingRelationResponse update(Long productOfferingRelationId, UpdateProductOfferingRelationRequest request) {
        ProductOfferingRelation entity = productOfferingRelationRepository.findById(productOfferingRelationId)
                .orElseThrow(() -> new ProductOfferingRelationNotFoundException(productOfferingRelationId));

        ProductOffering productOffering1 = productOfferingRepository.findById(request.getProductOfferingId1())
                .orElseThrow(() -> new ProductOfferingNotFoundException(request.getProductOfferingId1()));
        ProductOffering productOffering2 = productOfferingRepository.findById(request.getProductOfferingId2())
                .orElseThrow(() -> new ProductOfferingNotFoundException(request.getProductOfferingId2()));

        productOfferingRelationMapper.updateEntityFromRequest(request, entity);
        entity.setProductOffering1(productOffering1);
        entity.setProductOffering2(productOffering2);
        entity.setRelationTypeId(
                lookupCacheService.resolveTypeIdByCode(GnlTpGroups.PROD_OFR_REL, request.getRelationTypeCode()));

        ProductOfferingRelation saved = productOfferingRelationRepository.save(entity);
        UpdatedProductOfferingRelationResponse response = productOfferingRelationMapper.toUpdatedResponse(saved);
        response.setMandatory(GnlTpCodes.MANDATORY.equals(request.getRelationTypeCode()));
        response.setExclusive(GnlTpCodes.EXCL.equals(request.getRelationTypeCode()));
        return response;
    }

    @Override
    public GetProductOfferingRelationResponse getById(Long productOfferingRelationId) {
        ProductOfferingRelation entity = productOfferingRelationRepository.findById(productOfferingRelationId)
                .orElseThrow(() -> new ProductOfferingRelationNotFoundException(productOfferingRelationId));
        GetProductOfferingRelationResponse response = productOfferingRelationMapper.toGetResponse(entity);
        Long mandatoryTypeId = lookupCacheService.resolveTypeIdByCode(GnlTpGroups.PROD_OFR_REL, GnlTpCodes.MANDATORY);
        Long exclusiveTypeId = lookupCacheService.resolveTypeIdByCode(GnlTpGroups.PROD_OFR_REL, GnlTpCodes.EXCL);
        response.setMandatory(mandatoryTypeId.equals(entity.getRelationTypeId()));
        response.setExclusive(exclusiveTypeId.equals(entity.getRelationTypeId()));
        return response;
    }

    @Override
    public List<GetAllProductOfferingRelationResponse> getAll() {
        List<ProductOfferingRelation> entities = productOfferingRelationRepository.findAll();
        List<GetAllProductOfferingRelationResponse> responses = productOfferingRelationMapper.toGetAllResponseList(entities);
        applyMandatoryFlag(entities, responses);
        return responses;
    }

    @Override
    public List<GetAllProductOfferingRelationResponse> getByProductOfferingId(Long productOfferingId) {
        productOfferingRepository.findById(productOfferingId)
                .orElseThrow(() -> new ProductOfferingNotFoundException(productOfferingId));

        List<ProductOfferingRelation> entities =
                productOfferingRelationRepository.findByProductOffering1_ProductOfferingIdAndActiveTrue(productOfferingId);
        List<GetAllProductOfferingRelationResponse> responses = productOfferingRelationMapper.toGetAllResponseList(entities);
        applyMandatoryFlag(entities, responses);
        return responses;
    }

    private void applyMandatoryFlag(List<ProductOfferingRelation> entities, List<GetAllProductOfferingRelationResponse> responses) {
        Long mandatoryTypeId = lookupCacheService.resolveTypeIdByCode(GnlTpGroups.PROD_OFR_REL, GnlTpCodes.MANDATORY);
        Long exclusiveTypeId = lookupCacheService.resolveTypeIdByCode(GnlTpGroups.PROD_OFR_REL, GnlTpCodes.EXCL);
        for (int i = 0; i < entities.size(); i++) {
            responses.get(i).setMandatory(mandatoryTypeId.equals(entities.get(i).getRelationTypeId()));
            responses.get(i).setExclusive(exclusiveTypeId.equals(entities.get(i).getRelationTypeId()));
        }
    }

    @Override
    public void delete(Long productOfferingRelationId) {
        ProductOfferingRelation entity = productOfferingRelationRepository.findById(productOfferingRelationId)
                .orElseThrow(() -> new ProductOfferingRelationNotFoundException(productOfferingRelationId));
        entity.setActive(false);
        productOfferingRelationRepository.save(entity);
    }
}