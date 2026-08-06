package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.abstracts.ProductService;
import com.etiya.crm.productservice.business.dtos.requests.Product.CreateProductRequest;
import com.etiya.crm.productservice.business.dtos.requests.Product.UpdateProductRequest;
import com.etiya.crm.productservice.business.dtos.responses.Product.CreatedProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.GetAllProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.GetProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.UpdatedProductResponse;
import com.etiya.crm.productservice.business.exceptions.CampaignNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductOfferingNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductSpecNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.CampaignRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductOfferingRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductSpecRepository;
import com.etiya.crm.productservice.entities.concretes.Campaign;
import com.etiya.crm.productservice.entities.concretes.Product;
import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import com.etiya.crm.productservice.entities.concretes.ProductSpec;
import com.etiya.crm.productservice.mapper.ProductMapper;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductManager implements ProductService {
    private final ProductRepository productRepository;
    private final ProductOfferingRepository productOfferingRepository;
    private final ProductSpecRepository productSpecRepository;
    private final CampaignRepository campaignRepository;
    private final ProductMapper productMapper;
    private final LookupCacheService lookupCacheService;

    public ProductManager(ProductRepository productRepository, ProductOfferingRepository productOfferingRepository, ProductSpecRepository productSpecRepository, CampaignRepository campaignRepository, ProductMapper productMapper, LookupCacheService lookupCacheService) {
        this.productRepository = productRepository;
        this.productOfferingRepository = productOfferingRepository;
        this.productSpecRepository = productSpecRepository;
        this.campaignRepository = campaignRepository;
        this.productMapper = productMapper;
        this.lookupCacheService = lookupCacheService;
    }
    @Override
    public CreatedProductResponse create(CreateProductRequest request) {

        ProductOffering productOffering = productOfferingRepository.findById(request.getProductOfferingId())
                .orElseThrow(() -> new ProductOfferingNotFoundException(request.getProductOfferingId()));


        ProductSpec productSpec = productSpecRepository.findById(request.getProductSpecId())
                .orElseThrow(() -> new ProductSpecNotFoundException(request.getProductSpecId()));


        Product product = productMapper.toEntity(request);


        product.setProductOffering(productOffering);
        product.setProductSpec(productSpec);
        product.setStatusId(
                lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT, request.getStatusCode()));

        // OPSİYONEL ilişki: parentProduct (varsa bul + set)
        if (request.getParentProductId() != null) {
            Product parentProduct = productRepository.findById(request.getParentProductId())
                    .orElseThrow(() -> new ProductNotFoundException(request.getParentProductId()));
            product.setParentProduct(parentProduct);
        }

        // OPSİYONEL ilişki: campaign (varsa bul + set)
        if (request.getCampaignId() != null) {
            Campaign campaign = campaignRepository.findById(request.getCampaignId())
                    .orElseThrow(() -> new CampaignNotFoundException(request.getCampaignId()));
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

        ProductOffering productOffering = productOfferingRepository.findById(request.getProductOfferingId())
                .orElseThrow(() -> new ProductOfferingNotFoundException(request.getProductOfferingId()));
        product.setProductOffering(productOffering);

        ProductSpec productSpec = productSpecRepository.findById(request.getProductSpecId())
                .orElseThrow(() -> new ProductSpecNotFoundException(request.getProductSpecId()));
        product.setProductSpec(productSpec);

        if (request.getParentProductId() != null) {
            Product parentProduct = productRepository.findById(request.getParentProductId())
                    .orElseThrow(() -> new ProductNotFoundException(request.getParentProductId()));
            product.setParentProduct(parentProduct);
        } else {
            product.setParentProduct(null);
        }

        if (request.getCampaignId() != null) {
            Campaign campaign = campaignRepository.findById(request.getCampaignId())
                    .orElseThrow(() -> new CampaignNotFoundException(request.getCampaignId()));
            product.setCampaign(campaign);
        } else {
            product.setCampaign(null);
        }

        Product saved = productRepository.save(product);
        return productMapper.toUpdatedResponse(saved);
    }

    @Override
    public GetProductResponse getById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        return productMapper.toGetResponse(product);
    }

    @Override
    public List<GetAllProductResponse> getAll() {
        List<Product> products = productRepository.findAll();
        return productMapper.toGetAllResponseList(products);
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
