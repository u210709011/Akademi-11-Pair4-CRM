package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.ProductSpecResourceSpecService;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecResourceSpec.CreateProductSpecResourceSpecRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecResourceSpec.UpdateProductSpecResourceSpecRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.CreatedProductSpecResourceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.GetAllProductSpecResourceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.GetProductSpecResourceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.UpdatedProductSpecResourceSpecResponse;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductSpecRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductSpecResourceSpecRepository;
import com.etiya.crm.productservice.entities.concretes.ProductSpec;
import com.etiya.crm.productservice.entities.concretes.ProductSpecResourceSpec;
import com.etiya.crm.productservice.mapper.ProductSpecResourceSpecMapper;

import java.util.List;

public class ProductSpecResourceSpecManager implements ProductSpecResourceSpecService {

    private final ProductSpecResourceSpecRepository productSpecResourceSpecRepository;
    private final ProductSpecRepository productSpecRepository;
    private final ProductSpecResourceSpecMapper productSpecResourceSpecMapper;

    public ProductSpecResourceSpecManager(ProductSpecResourceSpecRepository productSpecResourceSpecRepository, ProductSpecRepository productSpecRepository, ProductSpecResourceSpecMapper productSpecResourceSpecMapper) {
        this.productSpecResourceSpecRepository = productSpecResourceSpecRepository;
        this.productSpecRepository = productSpecRepository;
        this.productSpecResourceSpecMapper = productSpecResourceSpecMapper;
    }

    @Override
    public CreatedProductSpecResourceSpecResponse create(CreateProductSpecResourceSpecRequest request) {
        ProductSpec productSpec = productSpecRepository.findById(request.getProductSpecId())
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait urun tanimi bulunamadi! : " + request.getProductSpecId()));

        // Mapper duz alanlari cevirir (resourceSpecId, relationTypeId, startDate, endDate, statusId)
        ProductSpecResourceSpec entity = productSpecResourceSpecMapper.toEntity(request);

        // Tek iliskiyi elle kur
        entity.setProductSpec(productSpec);

        ProductSpecResourceSpec saved = productSpecResourceSpecRepository.save(entity);
        return productSpecResourceSpecMapper.toCreatedResponse(saved);
    }

    @Override
    public UpdatedProductSpecResourceSpecResponse update(Long productSpecResourceSpecId, UpdateProductSpecResourceSpecRequest request) {
        ProductSpecResourceSpec entity = productSpecResourceSpecRepository.findById(productSpecResourceSpecId)
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait kayit bulunamadi! : " + productSpecResourceSpecId));

        // Mapper duz alanlari gunceller
        productSpecResourceSpecMapper.updateEntityFromRequest(request, entity);

        // Iliskiyi guncelle
        ProductSpec productSpec = productSpecRepository.findById(request.getProductSpecId())
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait urun tanimi bulunamadi! : " + request.getProductSpecId()));
        entity.setProductSpec(productSpec);

        ProductSpecResourceSpec saved = productSpecResourceSpecRepository.save(entity);
        return productSpecResourceSpecMapper.toUpdatedResponse(saved);
    }

    @Override
    public GetProductSpecResourceSpecResponse getById(Long productSpecResourceSpecId) {
        ProductSpecResourceSpec entity = productSpecResourceSpecRepository.findById(productSpecResourceSpecId)
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait kayit bulunamadi! : " + productSpecResourceSpecId));

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
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait kayit bulunamadi! : " + productSpecResourceSpecId));

        productSpecResourceSpecRepository.delete(entity);
    }
}
