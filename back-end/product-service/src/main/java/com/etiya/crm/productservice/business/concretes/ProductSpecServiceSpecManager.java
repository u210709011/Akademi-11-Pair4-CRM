package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.abstracts.ProductSpecServiceSpecService;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecServiceSpec.CreateProductSpecServiceSpecRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecServiceSpec.UpdateProductSpecServiceSpecRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.CreatedProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.GetAllProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.GetProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.UpdatedProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.exceptions.ProductSpecNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductSpecServiceSpecNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductSpecRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductSpecServiceSpecRepository;
import com.etiya.crm.productservice.entities.concretes.ProductSpec;
import com.etiya.crm.productservice.entities.concretes.ProductSpecServiceSpec;
import com.etiya.crm.productservice.mapper.ProductSpecServiceSpecMapper;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;
import com.etiya.crm.shared.contracts.gnltp.GnlTpGroups;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSpecServiceSpecManager implements ProductSpecServiceSpecService {

    private final ProductSpecServiceSpecRepository productSpecServiceSpecRepository;
    private final ProductSpecRepository productSpecRepository;
    private final ProductSpecServiceSpecMapper productSpecServiceSpecMapper;
    private final LookupCacheService lookupCacheService;

    public ProductSpecServiceSpecManager(ProductSpecServiceSpecRepository productSpecServiceSpecRepository, ProductSpecRepository productSpecRepository, ProductSpecServiceSpecMapper productSpecServiceSpecMapper, LookupCacheService lookupCacheService) {
        this.productSpecServiceSpecRepository = productSpecServiceSpecRepository;
        this.productSpecRepository = productSpecRepository;
        this.productSpecServiceSpecMapper = productSpecServiceSpecMapper;
        this.lookupCacheService = lookupCacheService;
    }

    @Override
    public CreatedProductSpecServiceSpecResponse create(CreateProductSpecServiceSpecRequest request) {
        ProductSpec productSpec = productSpecRepository.findById(request.getProductSpecId())
                .orElseThrow(() -> new ProductSpecNotFoundException(request.getProductSpecId()));

        ProductSpecServiceSpec entity = productSpecServiceSpecMapper.toEntity(request);
        entity.setProductSpec(productSpec);
        entity.setStatusId(
                lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_SPEC_SERVICE_SPEC, request.getStatusCode()));
        entity.setRelationTypeId(
                lookupCacheService.resolveTypeIdByCode(GnlTpGroups.PROD_SPEC_SRVC_SPEC, request.getRelationTypeCode()));
        entity.setServiceSpecId(lookupCacheService.validateServiceSpecId(request.getServiceSpecId()));


        ProductSpecServiceSpec saved = productSpecServiceSpecRepository.save(entity);
        return productSpecServiceSpecMapper.toCreatedResponse(saved);
    }

    @Override
    public UpdatedProductSpecServiceSpecResponse update(Long productSpecServiceSpecId,
                                                        UpdateProductSpecServiceSpecRequest request) {
        ProductSpecServiceSpec entity = productSpecServiceSpecRepository.findById(productSpecServiceSpecId)
                .orElseThrow(() -> new ProductSpecServiceSpecNotFoundException(productSpecServiceSpecId));

        productSpecServiceSpecMapper.updateEntityFromRequest(request, entity);

        ProductSpec productSpec = productSpecRepository.findById(request.getProductSpecId())
                .orElseThrow(() -> new ProductSpecNotFoundException(request.getProductSpecId()));
        entity.setProductSpec(productSpec);
        entity.setStatusId(
                lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_SPEC_SERVICE_SPEC, request.getStatusCode()));
        entity.setRelationTypeId(
                lookupCacheService.resolveTypeIdByCode(GnlTpGroups.PROD_SPEC_SRVC_SPEC, request.getRelationTypeCode()));
        entity.setServiceSpecId(lookupCacheService.validateServiceSpecId(request.getServiceSpecId()));


        ProductSpecServiceSpec saved = productSpecServiceSpecRepository.save(entity);
        return productSpecServiceSpecMapper.toUpdatedResponse(saved);
    }

    @Override
    public GetProductSpecServiceSpecResponse getById(Long productSpecServiceSpecId) {
        ProductSpecServiceSpec entity = productSpecServiceSpecRepository.findById(productSpecServiceSpecId)
                .orElseThrow(() -> new ProductSpecServiceSpecNotFoundException(productSpecServiceSpecId));
        return productSpecServiceSpecMapper.toGetResponse(entity);
    }

    @Override
    public List<GetAllProductSpecServiceSpecResponse> getAll() {
        List<ProductSpecServiceSpec> entities = productSpecServiceSpecRepository.findAll();
        return productSpecServiceSpecMapper.toGetAllResponseList(entities);
    }

    @Override
    public void delete(Long productSpecServiceSpecId) {
        ProductSpecServiceSpec entity = productSpecServiceSpecRepository.findById(productSpecServiceSpecId)
                .orElseThrow(() -> new ProductSpecServiceSpecNotFoundException(productSpecServiceSpecId));
        entity.setStatusId(
                lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_SPEC_SERVICE_SPEC, GnlStCodes.DELETED));
        productSpecServiceSpecRepository.save(entity);
    }
}
