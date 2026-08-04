package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.ProductCharacteristicValueService;
import com.etiya.crm.productservice.business.dtos.requests.ProductCharacteristicValue.CreateProductCharacteristicValueRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCharacteristicValue.UpdateProductCharacteristicValueRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue.CreatedProductCharacteristicValueResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue.GetAllProductCharacteristicValueResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue.GetProductCharacteristicValueResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue.UpdatedProductCharacteristicValueResponse;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductCharacteristicValueRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductRepository;
import com.etiya.crm.productservice.entities.concretes.Product;
import com.etiya.crm.productservice.entities.concretes.ProductCharacteristicValue;
import com.etiya.crm.productservice.mapper.ProductCharacteristicValueMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductCharacteristicValueManager implements ProductCharacteristicValueService {

    private final ProductCharacteristicValueRepository productCharacteristicValueRepository;
    private final ProductRepository productRepository;
    private final ProductCharacteristicValueMapper productCharacteristicValueMapper;

    public ProductCharacteristicValueManager(ProductCharacteristicValueRepository productCharacteristicValueRepository, ProductRepository productRepository, ProductCharacteristicValueMapper productCharacteristicValueMapper) {
        this.productCharacteristicValueRepository = productCharacteristicValueRepository;
        this.productRepository = productRepository;
        this.productCharacteristicValueMapper = productCharacteristicValueMapper;
    }


    @Override
    public CreatedProductCharacteristicValueResponse create(CreateProductCharacteristicValueRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait ürün bulunamadı! : " + request.getProductId()));

        ProductCharacteristicValue entity = productCharacteristicValueMapper.toEntity(request);

        entity.setProduct(product);

        ProductCharacteristicValue saved = productCharacteristicValueRepository.save(entity);
        return productCharacteristicValueMapper.toCreatedResponse(saved);
    }

    @Override
    public UpdatedProductCharacteristicValueResponse update(Long productCharacteristicValueId, UpdateProductCharacteristicValueRequest request) {
        ProductCharacteristicValue entity = productCharacteristicValueRepository.findById(productCharacteristicValueId)
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait kayıt bulunamadı! : " + productCharacteristicValueId));

        productCharacteristicValueMapper.updateEntityFromRequest(request, entity);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait ürün bulunamadı! : " + request.getProductId()));
        entity.setProduct(product);

        ProductCharacteristicValue saved = productCharacteristicValueRepository.save(entity);
        return productCharacteristicValueMapper.toUpdatedResponse(saved);
    }

    @Override
    public GetProductCharacteristicValueResponse getById(Long productCharacteristicValueId) {
        ProductCharacteristicValue entity = productCharacteristicValueRepository.findById(productCharacteristicValueId)
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait kayıt bulunamadı! : " + productCharacteristicValueId));
        return productCharacteristicValueMapper.toGetResponse(entity);
    }

    @Override
    public List<GetAllProductCharacteristicValueResponse> getAll() {
        List<ProductCharacteristicValue> entities = productCharacteristicValueRepository.findAll();
        return productCharacteristicValueMapper.toGetAllResponseList(entities);
    }

    @Override
    public void delete(Long productCharacteristicValueId) {
        ProductCharacteristicValue entity = productCharacteristicValueRepository.findById(productCharacteristicValueId)
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait kayıt bulunamadı! : " + productCharacteristicValueId));
        productCharacteristicValueRepository.delete(entity);
    }
}
