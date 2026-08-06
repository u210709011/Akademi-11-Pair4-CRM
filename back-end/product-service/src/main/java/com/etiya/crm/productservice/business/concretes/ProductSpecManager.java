package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.abstracts.ProductSpecService;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpec.CreateProductSpecRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpec.UpdateProductSpecRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.CreatedProductSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.GetAllProductSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.GetProductSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.UpdatedProductSpecResponse;
import com.etiya.crm.productservice.business.exceptions.ProductSpecNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductSpecRepository;
import com.etiya.crm.productservice.entities.concretes.ProductSpec;
import com.etiya.crm.productservice.mapper.ProductSpecMapper;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSpecManager implements ProductSpecService {

    private final ProductSpecRepository productSpecRepository;
    private final ProductSpecMapper productSpecMapper;
    private final LookupCacheService lookupCacheService;

    public ProductSpecManager(ProductSpecRepository productSpecRepository, ProductSpecMapper productSpecMapper, LookupCacheService lookupCacheService) {
        this.productSpecRepository = productSpecRepository;
        this.productSpecMapper = productSpecMapper;
        this.lookupCacheService = lookupCacheService;
    }

    @Override
    public CreatedProductSpecResponse create(CreateProductSpecRequest request) {
        ProductSpec productSpec = productSpecMapper.toEntity(request);
        productSpec.setStatusId(
                lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_SPEC, request.getStatusCode()));
        ProductSpec saved = productSpecRepository.save(productSpec);
        return productSpecMapper.toCreatedResponse(saved);
    }

    @Override
    public UpdatedProductSpecResponse update(Long productSpecId, UpdateProductSpecRequest request) {
        ProductSpec productSpec = productSpecRepository.findById(productSpecId)
                .orElseThrow(() -> new ProductSpecNotFoundException(productSpecId));

        productSpecMapper.updateEntityFromRequest(request, productSpec);
        productSpec.setStatusId(
                lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_SPEC, request.getStatusCode()));

        ProductSpec saved = productSpecRepository.save(productSpec);
        return productSpecMapper.toUpdatedResponse(saved);
    }

    @Override
    public GetProductSpecResponse getById(Long productSpecId) {
        ProductSpec productSpec = productSpecRepository.findById(productSpecId)
                .orElseThrow(() -> new ProductSpecNotFoundException(productSpecId));
        return productSpecMapper.toGetResponse(productSpec);
    }

    @Override
    public List<GetAllProductSpecResponse> getAll() {
        List<ProductSpec> productSpecs = productSpecRepository.findAll();
        return productSpecMapper.toGetAllResponseList(productSpecs);
    }

    //(TODO) şimdilik kalıcı silme soft delete işlemi ayarlanacak
    //(TODO) bağlı olduğu offer'lar varsa silme işlemi kontrolü ayarlanacak
    @Override
    public void delete(Long productSpecId) {
        ProductSpec productSpec = productSpecRepository.findById(productSpecId)
                .orElseThrow(() -> new ProductSpecNotFoundException(productSpecId));
        productSpecRepository.delete(productSpec);
    }
}
