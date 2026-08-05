package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.abstracts.ProductCatalogService;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalog.CreateProductCatalogRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalog.UpdateProductCatalogRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.CreatedProductCatalogResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.GetAllProductCatalogResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.GetProductCatalogResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.UpdatedProductCatalogResponse;
import com.etiya.crm.productservice.business.exceptions.ProductCatalogNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductCatalogRepository;
import com.etiya.crm.productservice.entities.concretes.ProductCatalog;
import com.etiya.crm.productservice.mapper.ProductCatalogMapper;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductCatalogManager implements ProductCatalogService {

    private final ProductCatalogRepository productCatalogRepository;
    private final ProductCatalogMapper productCatalogMapper;
    private final LookupCacheService lookupCacheService;

    public ProductCatalogManager(ProductCatalogRepository productCatalogRepository, ProductCatalogMapper productCatalogMapper, LookupCacheService lookupCacheService) {
        this.productCatalogRepository = productCatalogRepository;
        this.productCatalogMapper = productCatalogMapper;
        this.lookupCacheService = lookupCacheService;
    }


    @Override
    public CreatedProductCatalogResponse create(CreateProductCatalogRequest request) {
        ProductCatalog productCatalog = productCatalogMapper.toEntity(request);
        productCatalog.setStatusId(
                lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_CATALOG, request.getStatusCode()));
        ProductCatalog saved  = productCatalogRepository.save(productCatalog);
        return productCatalogMapper.toCreatedResponse(saved);
    }

    @Override
    public UpdatedProductCatalogResponse update(Long productCatalogId, UpdateProductCatalogRequest request) {
        ProductCatalog productCatalog = productCatalogRepository.findById(productCatalogId)
                .orElseThrow(() -> new ProductCatalogNotFoundException(productCatalogId));

        productCatalogMapper.updateEntityFromRequest(request, productCatalog);
        productCatalog.setStatusId(
                lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_CATALOG, request.getStatusCode()));
        ProductCatalog saved  = productCatalogRepository.save(productCatalog);
        return productCatalogMapper.toUpdatedResponse(saved);
    }

    @Override
    public GetProductCatalogResponse getById(Long productCatalogId) {
        ProductCatalog productCatalog = productCatalogRepository.findById(productCatalogId)
                .orElseThrow(() -> new ProductCatalogNotFoundException(productCatalogId));

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
                .orElseThrow(() -> new ProductCatalogNotFoundException(productCatalogId));

        productCatalogRepository.delete(productCatalog);
    }
}
