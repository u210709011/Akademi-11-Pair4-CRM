package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.ProductCatalogService;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalog.CreateProductCatalogRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalog.UpdateProductCatalogRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.CreatedProductCatalogResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.GetAllProductCatalogResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.GetProductCatalogResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.UpdatedProductCatalogResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.UpdatedProductSpecResponse;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductCatalogRepository;
import com.etiya.crm.productservice.entities.concretes.ProductCatalog;
import com.etiya.crm.productservice.entities.concretes.ProductSpec;
import com.etiya.crm.productservice.mapper.ProductCatalogMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductCatalogManager implements ProductCatalogService {

    private final ProductCatalogRepository productCatalogRepository;
    private final ProductCatalogMapper productCatalogMapper;

    public ProductCatalogManager(ProductCatalogRepository productCatalogRepository, ProductCatalogMapper productCatalogMapper) {
        this.productCatalogRepository = productCatalogRepository;
        this.productCatalogMapper = productCatalogMapper;
    }


    @Override
    public CreatedProductCatalogResponse create(CreateProductCatalogRequest request) {
        ProductCatalog productCatalog = productCatalogMapper.toEntity(request);
        ProductCatalog saved  = productCatalogRepository.save(productCatalog);
        return productCatalogMapper.toCreatedResponse(saved);
    }

    @Override
    public UpdatedProductCatalogResponse update(Long productCatalogId, UpdateProductCatalogRequest request) {
        ProductCatalog productCatalog = productCatalogRepository.findById(productCatalogId)
                .orElseThrow(() -> new RuntimeException("Girilen id' ye ait urun kataloğu bulunamadi! : " +productCatalogId));

        productCatalogMapper.updateEntityFromRequest(request, productCatalog);
        ProductCatalog saved  = productCatalogRepository.save(productCatalog);
        return productCatalogMapper.toUpdatedResponse(saved);
    }

    @Override
    public GetProductCatalogResponse getById(Long productCatalogId) {
        ProductCatalog productCatalog = productCatalogRepository.findById(productCatalogId)
                .orElseThrow(() -> new RuntimeException("Girilen id' ye ait urun kataloğu bulunamadi! : " +productCatalogId));

        return productCatalogMapper.toGetResponse(productCatalog);
    }

    @Override
    public List<GetAllProductCatalogResponse> getAll() {
        List<ProductCatalog> productCatalogs = productCatalogRepository.findAll();
        return productCatalogMapper.toGetAllResponseList(productCatalogs);
    }

    @Override
    public void delete(Long productCatalogId) {
        ProductCatalog productCatalog = productCatalogRepository.findById(productCatalogId)
                .orElseThrow(() -> new RuntimeException("Girilen id' ye ait urun kataloğu bulunamadi! : " +productCatalogId));

        productCatalogRepository.delete(productCatalog);
    }
}
