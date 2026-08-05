package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.ProductRelationService;
import com.etiya.crm.productservice.business.dtos.requests.ProductRelation.CreateProductRelationRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductRelation.UpdateProductRelationRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.CreatedProductRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.GetAllProductRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.GetProductRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.UpdatedProductRelationResponse;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductRelationRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductRepository;
import com.etiya.crm.productservice.entities.concretes.Product;
import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import com.etiya.crm.productservice.entities.concretes.ProductRelation;
import com.etiya.crm.productservice.mapper.ProductRelationMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductRelationManager implements ProductRelationService {

    private final ProductRelationRepository productRelationRepository;
    private final ProductRepository productRepository;
    private final ProductRelationMapper productRelationMapper;

    public ProductRelationManager(ProductRelationRepository productRelationRepository, ProductRepository productRepository, ProductRelationMapper productRelationMapper) {
        this.productRelationRepository = productRelationRepository;
        this.productRepository = productRepository;
        this.productRelationMapper = productRelationMapper;
    }
    @Override
    public CreatedProductRelationResponse create(CreateProductRelationRequest request) {
        Product product1 = productRepository.findById(request.getProductId1())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product bulunamadı, id: " + request.getProductId1()));

        Product product2 = productRepository.findById(request.getProductId2())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product bulunamadı, id: " + request.getProductId2()));

        ProductRelation productRelation = productRelationMapper.toEntity(request);
        productRelation.setProduct1(product1);
        productRelation.setProduct2(product2);

        ProductRelation saved = productRelationRepository.save(productRelation);
        return productRelationMapper.toCreatedResponse(saved);
    }

    @Override
    public UpdatedProductRelationResponse update(Long productRelationId, UpdateProductRelationRequest request) {
        ProductRelation entity = productRelationRepository.findById(productRelationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ProductRelation bulunamadı, id: " + productRelationId));

        Product product1 = productRepository.findById(request.getProductId1())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product bulunamadı, id: " + request.getProductId1()));

        Product product2 = productRepository.findById(request.getProductId2())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product bulunamadı, id: " + request.getProductId2()));

        productRelationMapper.updateEntityFromRequest(request, entity);
        entity.setProduct1(product1);
        entity.setProduct2(product2);

        ProductRelation updated = productRelationRepository.save(entity);
        return productRelationMapper.toUpdatedResponse(updated);
    }

    @Override
    public GetProductRelationResponse getById(Long productRelationId) {
        ProductRelation productRelation = productRelationRepository.findById(productRelationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ProductRelation bulunamadı, id: " + productRelationId));
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
                .orElseThrow(() -> new RuntimeException("Girilen id'ye ait teklif bulunamadı! : " + productRelationId));

        productRelationRepository.delete(productRelation);
    }


}
