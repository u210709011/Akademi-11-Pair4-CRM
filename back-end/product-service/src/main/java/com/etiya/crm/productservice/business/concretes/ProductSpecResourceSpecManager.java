package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.abstracts.ProductSpecResourceSpecService;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecResourceSpec.CreateProductSpecResourceSpecRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecResourceSpec.UpdateProductSpecResourceSpecRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.CreatedProductSpecResourceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.GetAllProductSpecResourceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.GetProductSpecResourceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.UpdatedProductSpecResourceSpecResponse;
import com.etiya.crm.productservice.business.exceptions.ProductSpecNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductSpecResourceSpecNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductSpecRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductSpecResourceSpecRepository;
import com.etiya.crm.productservice.entities.concretes.ProductSpec;
import com.etiya.crm.productservice.entities.concretes.ProductSpecResourceSpec;
import com.etiya.crm.productservice.mapper.ProductSpecResourceSpecMapper;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;
import com.etiya.crm.shared.contracts.gnltp.GnlTpGroups;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSpecResourceSpecManager implements ProductSpecResourceSpecService {

    private final ProductSpecResourceSpecRepository productSpecResourceSpecRepository;
    private final ProductSpecRepository productSpecRepository;
    private final ProductSpecResourceSpecMapper productSpecResourceSpecMapper;
    private final LookupCacheService lookupCacheService;

    public ProductSpecResourceSpecManager(ProductSpecResourceSpecRepository productSpecResourceSpecRepository, ProductSpecRepository productSpecRepository, ProductSpecResourceSpecMapper productSpecResourceSpecMapper, LookupCacheService lookupCacheService) {
        this.productSpecResourceSpecRepository = productSpecResourceSpecRepository;
        this.productSpecRepository = productSpecRepository;
        this.productSpecResourceSpecMapper = productSpecResourceSpecMapper;
        this.lookupCacheService = lookupCacheService;
    }

    @Override
    public CreatedProductSpecResourceSpecResponse create(CreateProductSpecResourceSpecRequest request) {
        ProductSpec productSpec = productSpecRepository.findById(request.getProductSpecId())
                .orElseThrow(() -> new ProductSpecNotFoundException(request.getProductSpecId()));

        ProductSpecResourceSpec entity = productSpecResourceSpecMapper.toEntity(request);
        entity.setProductSpec(productSpec);

        entity.setStatusId(
                lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_SPEC_RESOURCE_SPEC, request.getStatusCode()));
        entity.setRelationTypeId(
                lookupCacheService.resolveTypeIdByCode(GnlTpGroups.PROD_SPEC_RSRC_SPEC, request.getRelationTypeCode()));
        ProductSpecResourceSpec saved = productSpecResourceSpecRepository.save(entity);
        return productSpecResourceSpecMapper.toCreatedResponse(saved);
    }

    @Override
    public UpdatedProductSpecResourceSpecResponse update(Long productSpecResourceSpecId, UpdateProductSpecResourceSpecRequest request) {
        ProductSpecResourceSpec entity = productSpecResourceSpecRepository.findById(productSpecResourceSpecId)
                .orElseThrow(() -> new ProductSpecResourceSpecNotFoundException(productSpecResourceSpecId));

        // Mapper duz alanlari gunceller
        productSpecResourceSpecMapper.updateEntityFromRequest(request, entity);

        ProductSpec productSpec = productSpecRepository.findById(request.getProductSpecId())
                .orElseThrow(() -> new ProductSpecNotFoundException(request.getProductSpecId()));
        entity.setProductSpec(productSpec);
        entity.setStatusId(
                lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_SPEC_RESOURCE_SPEC, request.getStatusCode()));
        entity.setRelationTypeId(
                lookupCacheService.resolveTypeIdByCode(GnlTpGroups.PROD_SPEC_RSRC_SPEC, request.getRelationTypeCode()));

        ProductSpecResourceSpec saved = productSpecResourceSpecRepository.save(entity);
        return productSpecResourceSpecMapper.toUpdatedResponse(saved);
    }

    @Override
    public GetProductSpecResourceSpecResponse getById(Long productSpecResourceSpecId) {
        ProductSpecResourceSpec entity = productSpecResourceSpecRepository.findById(productSpecResourceSpecId)
                .orElseThrow(() -> new ProductSpecResourceSpecNotFoundException(productSpecResourceSpecId));
        return productSpecResourceSpecMapper.toGetResponse(entity);
    }

    @Override
    public List<GetAllProductSpecResourceSpecResponse> getAll() {
        List<ProductSpecResourceSpec> entities = productSpecResourceSpecRepository.findAll();
        return productSpecResourceSpecMapper.toGetAllResponseList(entities);
    }

    @Override
    public void delete(Long productSpecResourceSpecId) {
        ProductSpecResourceSpec entity = productSpecResourceSpecRepository.findById(productSpecResourceSpecId)
                .orElseThrow(() -> new ProductSpecResourceSpecNotFoundException(productSpecResourceSpecId));
        productSpecResourceSpecRepository.delete(entity);
    }
}
