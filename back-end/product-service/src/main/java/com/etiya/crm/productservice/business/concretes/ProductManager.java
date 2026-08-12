package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.abstracts.ProductService;
import com.etiya.crm.productservice.business.dtos.requests.Product.CreateProductRequest;
import com.etiya.crm.productservice.business.dtos.requests.Product.UpdateProductRequest;
import com.etiya.crm.productservice.business.dtos.responses.Product.CreatedProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.GetAllProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.GetProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.UpdatedProductResponse;
import com.etiya.crm.productservice.business.exceptions.ProductNotFoundException;
import com.etiya.crm.productservice.business.rules.ProductRelationRules;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductRepository;
import com.etiya.crm.productservice.entities.concretes.Campaign;
import com.etiya.crm.productservice.entities.concretes.Product;
import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import com.etiya.crm.productservice.entities.concretes.ProductSpec;
import com.etiya.crm.productservice.mapper.ProductMapper;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductManager implements ProductService {
    private final ProductRepository productRepository;
    private final ProductRelationRules productRelationRules;
    private final ProductMapper productMapper;
    private final LookupCacheService lookupCacheService;

    public ProductManager(ProductRepository productRepository, ProductRelationRules productRelationRules, ProductMapper productMapper, LookupCacheService lookupCacheService) {
        this.productRepository = productRepository;
        this.productRelationRules = productRelationRules;
        this.productMapper = productMapper;
        this.lookupCacheService = lookupCacheService;
    }
    @Override
    public CreatedProductResponse create(CreateProductRequest request) {

        ProductOffering productOffering = productRelationRules.getProductOffering(request.getProductOfferingId());

        ProductSpec productSpec = productRelationRules.getProductSpec(request.getProductSpecId());

        Product product = productMapper.toEntity(request);


        product.setProductOffering(productOffering);
        product.setProductSpec(productSpec);
        product.setStatusId(
                lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT, request.getStatusCode()));
        product.setServiceStartDate(java.time.LocalDate.now());

        // OPSİYONEL ilişki: parentProduct (varsa bul + set)
        if (request.getParentProductId() != null) {
            Product parentProduct = productRelationRules.getParentProduct(request.getParentProductId());
            product.setParentProduct(parentProduct);
        }

        // OPSİYONEL ilişki: campaign (varsa bul + set)
        if (request.getCampaignId() != null) {
            Campaign campaign = productRelationRules.getCampaign(request.getCampaignId());
            product.setCampaign(campaign);
        }
        Product saved = productRepository.save(product); // id tanımlandı
        return productMapper.toCreatedResponse(saved);
    }

    @Override
    public UpdatedProductResponse update(Long productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        productMapper.updateEntityFromRequest(request, product);
        product.setStatusId(
                lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT, request.getStatusCode()));

        ProductOffering productOffering = productRelationRules.getProductOffering(request.getProductOfferingId());
        product.setProductOffering(productOffering);

        ProductSpec productSpec = productRelationRules.getProductSpec(request.getProductSpecId());
        product.setProductSpec(productSpec);

        if (request.getParentProductId() != null) {
            Product parentProduct = productRelationRules.getParentProduct(request.getParentProductId());
            product.setParentProduct(parentProduct);
        } else {
            product.setParentProduct(null);
        }

        if (request.getCampaignId() != null) {
            Campaign campaign = productRelationRules.getCampaign(request.getCampaignId());
            product.setCampaign(campaign);
        } else {
            product.setCampaign(null);
        }

        Product saved = productRepository.save(product);
        return productMapper.toUpdatedResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public GetProductResponse getById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        GetProductResponse response = productMapper.toGetResponse(product);
        response.setProductOfferingName(product.getProductOffering().getName());
        if (product.getCampaign() != null) {
            response.setCampaignName(product.getCampaign().getName());
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetAllProductResponse> getAll() {
        List<Product> products = productRepository.findAll();
        List<GetAllProductResponse> responses = productMapper.toGetAllResponseList(products);

        for (int i = 0; i < products.size(); i++) {
            responses.get(i).setProductOfferingName(products.get(i).getProductOffering().getName());
            if (products.get(i).getCampaign() != null) {
                responses.get(i).setCampaignName(products.get(i).getCampaign().getName());
            }
        }
        return responses;
    }

    @Override
    public void delete(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        product.setStatusId(
                lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT, GnlStCodes.DELETED));
        productRepository.save(product);
    }

}
