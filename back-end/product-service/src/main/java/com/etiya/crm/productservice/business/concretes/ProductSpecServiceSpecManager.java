package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.ProductSpecServiceSpecService;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecServiceSpec.CreateProductSpecServiceSpecRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecServiceSpec.UpdateProductSpecServiceSpecRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.CreatedProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.GetAllProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.GetProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.UpdatedProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductSpecRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductSpecServiceSpecRepository;
import com.etiya.crm.productservice.entities.concretes.ProductSpec;
import com.etiya.crm.productservice.entities.concretes.ProductSpecServiceSpec;
import com.etiya.crm.productservice.mapper.ProductSpecServiceSpecMapper;

import java.util.List;

public class ProductSpecServiceSpecManager implements ProductSpecServiceSpecService {

    private final ProductSpecServiceSpecRepository productSpecServiceSpecRepository;
    private final ProductSpecRepository productSpecRepository;
    private final ProductSpecServiceSpecMapper productSpecServiceSpecMapper;

    public ProductSpecServiceSpecManager(ProductSpecServiceSpecRepository productSpecServiceSpecRepository, ProductSpecRepository productSpecRepository, ProductSpecServiceSpecMapper productSpecServiceSpecMapper) {
        this.productSpecServiceSpecRepository = productSpecServiceSpecRepository;
        this.productSpecRepository = productSpecRepository;
        this.productSpecServiceSpecMapper = productSpecServiceSpecMapper;
    }

    @Override
    public CreatedProductSpecServiceSpecResponse create(CreateProductSpecServiceSpecRequest request) {
        ProductSpec productSpec = productSpecRepository.findById(request.getProductSpecId())
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait urun tanimi bulunamadi! : " + request.getProductSpecId()));

        ProductSpecServiceSpec entity = productSpecServiceSpecMapper.toEntity(request);
        entity.setProductSpec(productSpec);

        ProductSpecServiceSpec saved = productSpecServiceSpecRepository.save(entity);
        return productSpecServiceSpecMapper.toCreatedResponse(saved);
    }

    @Override
    public UpdatedProductSpecServiceSpecResponse update(Long productSpecServiceSpecId,
                                                        UpdateProductSpecServiceSpecRequest request) {
        ProductSpecServiceSpec entity = productSpecServiceSpecRepository.findById(productSpecServiceSpecId)
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait kayit bulunamadi! : " + productSpecServiceSpecId));

        productSpecServiceSpecMapper.updateEntityFromRequest(request, entity);

        ProductSpec productSpec = productSpecRepository.findById(request.getProductSpecId())
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait urun tanimi bulunamadi! : " + request.getProductSpecId()));
        entity.setProductSpec(productSpec);

        ProductSpecServiceSpec saved = productSpecServiceSpecRepository.save(entity);
        return productSpecServiceSpecMapper.toUpdatedResponse(saved);
    }

    @Override
    public GetProductSpecServiceSpecResponse getById(Long productSpecServiceSpecId) {
        ProductSpecServiceSpec entity = productSpecServiceSpecRepository.findById(productSpecServiceSpecId)
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait kayit bulunamadi! : " + productSpecServiceSpecId));

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
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait kayit bulunamadi! : " + productSpecServiceSpecId));

        productSpecServiceSpecRepository.delete(entity);
    }
}
