package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.abstracts.ProductCatalogService;
import com.etiya.crm.productservice.business.abstracts.TranslationService;
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
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductCatalogManager implements ProductCatalogService {

    private static final String ENTITY_NAME = "PROD_CATAL";

    private final ProductCatalogRepository productCatalogRepository;
    private final ProductCatalogMapper productCatalogMapper;
    private final LookupCacheService lookupCacheService;
    private final TranslationService translationService;

    public ProductCatalogManager(ProductCatalogRepository productCatalogRepository, ProductCatalogMapper productCatalogMapper, LookupCacheService lookupCacheService, TranslationService translationService) {
        this.productCatalogRepository = productCatalogRepository;
        this.productCatalogMapper = productCatalogMapper;
        this.lookupCacheService = lookupCacheService;
        this.translationService = translationService;
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

        GetProductCatalogResponse response = productCatalogMapper.toGetResponse(productCatalog);
        applyTranslation(response);
        return response;
    }

    @Override
    public List<GetAllProductCatalogResponse> getAll() {
        List<ProductCatalog> productCatalogs = productCatalogRepository.findAll();
        List<GetAllProductCatalogResponse> responses = productCatalogMapper.toGetAllResponseList(productCatalogs);
        responses.forEach(this::applyTranslation);
        return responses;
    }

    /** name/descr taban degerleri Ingilizce'dir - bkz. lookup-service GnlTpManager (ayni desen). */
    private void applyTranslation(GetProductCatalogResponse response) {
        response.setName(translationService.translate(ENTITY_NAME, response.getProductCatalogId(), "NAME", response.getName()));
        response.setDescr(translationService.translate(ENTITY_NAME, response.getProductCatalogId(), "DESCR", response.getDescr()));
    }

    private void applyTranslation(GetAllProductCatalogResponse response) {
        response.setName(translationService.translate(ENTITY_NAME, response.getProductCatalogId(), "NAME", response.getName()));
        response.setDescr(translationService.translate(ENTITY_NAME, response.getProductCatalogId(), "DESCR", response.getDescr()));
    }

    @Override
    public void delete(Long productCatalogId) {
        ProductCatalog productCatalog = productCatalogRepository.findById(productCatalogId)
                .orElseThrow(() -> new ProductCatalogNotFoundException(productCatalogId));

        productCatalog.setStatusId(
                lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_CATALOG, GnlStCodes.DELETED));
        productCatalogRepository.save(productCatalog);
    }
}
