package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.abstracts.ProductRelationService;
import com.etiya.crm.productservice.business.dtos.requests.ProductRelation.CreateProductRelationRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductRelation.UpdateProductRelationRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.CreatedProductRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.GetAllProductRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.GetProductRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.UpdatedProductRelationResponse;
import com.etiya.crm.productservice.business.exceptions.ProductNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductRelationNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductRelationRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductRepository;
import com.etiya.crm.productservice.entities.concretes.Product;
import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import com.etiya.crm.productservice.entities.concretes.ProductRelation;
import com.etiya.crm.productservice.mapper.ProductRelationMapper;
import com.etiya.crm.shared.contracts.gnltp.GnlTpGroups;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductRelationManager implements ProductRelationService {

    private final ProductRelationRepository productRelationRepository;
    private final ProductRepository productRepository;
    private final ProductRelationMapper productRelationMapper;
    private final LookupCacheService lookupCacheService;

    public ProductRelationManager(ProductRelationRepository productRelationRepository, ProductRepository productRepository, ProductRelationMapper productRelationMapper, LookupCacheService lookupCacheService) {
        this.productRelationRepository = productRelationRepository;
        this.productRepository = productRepository;
        this.productRelationMapper = productRelationMapper;
        this.lookupCacheService = lookupCacheService;
    }
    @Override
    public CreatedProductRelationResponse create(CreateProductRelationRequest request) {
        Product product1 = productRepository.findById(request.getProductId1())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId1()));
        Product product2 = productRepository.findById(request.getProductId2())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId2()));


        ProductRelation productRelation = productRelationMapper.toEntity(request);
        productRelation.setProduct1(product1);
        productRelation.setProduct2(product2);
        productRelation.setRelationTypeId(
                lookupCacheService.resolveTypeIdByCode(GnlTpGroups.PROD_REL, request.getRelationTypeCode()));

        ProductRelation saved = productRelationRepository.save(productRelation);
        return productRelationMapper.toCreatedResponse(saved);
    }

    @Override
    public UpdatedProductRelationResponse update(Long productRelationId, UpdateProductRelationRequest request) {
        ProductRelation entity = productRelationRepository.findById(productRelationId)
                .orElseThrow(() -> new ProductRelationNotFoundException(productRelationId));

        Product product1 = productRepository.findById(request.getProductId1())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId1()));
        Product product2 = productRepository.findById(request.getProductId2())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId2()));

        productRelationMapper.updateEntityFromRequest(request, entity);
        entity.setProduct1(product1);
        entity.setProduct2(product2);
        entity.setRelationTypeId(
                lookupCacheService.resolveTypeIdByCode(GnlTpGroups.PROD_REL, request.getRelationTypeCode()));

        ProductRelation updated = productRelationRepository.save(entity);
        return productRelationMapper.toUpdatedResponse(updated);
    }

    @Override
    public GetProductRelationResponse getById(Long productRelationId) {
        ProductRelation productRelation = productRelationRepository.findById(productRelationId)
                .orElseThrow(() -> new ProductRelationNotFoundException(productRelationId));
        return productRelationMapper.toGetResponse(productRelation);
    }

    @Override
    public List<GetAllProductRelationResponse> getAll() {
        List<ProductRelation> productRelations = productRelationRepository.findAll();
        return productRelationMapper.toGetAllResponseList(productRelations);
    }

    @Override
    public void delete(Long productRelationId) {
        ProductRelation productRelation = productRelationRepository.findById(productRelationId)
                .orElseThrow(() -> new ProductRelationNotFoundException(productRelationId));
        productRelationRepository.delete(productRelation);
    }


}
