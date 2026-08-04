package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.ProductService;
import com.etiya.crm.productservice.business.dtos.requests.Product.CreateProductRequest;
import com.etiya.crm.productservice.business.dtos.requests.Product.UpdateProductRequest;
import com.etiya.crm.productservice.business.dtos.responses.Product.CreatedProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.GetAllProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.GetProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.UpdatedProductResponse;
import com.etiya.crm.productservice.dataAccess.abstracts.CampaignRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductOfferingRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductSpecRepository;
import com.etiya.crm.productservice.entities.concretes.Campaign;
import com.etiya.crm.productservice.entities.concretes.Product;
import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import com.etiya.crm.productservice.entities.concretes.ProductSpec;
import com.etiya.crm.productservice.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductManager implements ProductService {
    private final ProductRepository productRepository;
    private final ProductOfferingRepository productOfferingRepository;
    private final ProductSpecRepository productSpecRepository;
    private final CampaignRepository campaignRepository;
    private final ProductMapper productMapper;

    public ProductManager(ProductRepository productRepository, ProductOfferingRepository productOfferingRepository, ProductSpecRepository productSpecRepository, CampaignRepository campaignRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productOfferingRepository = productOfferingRepository;
        this.productSpecRepository = productSpecRepository;
        this.campaignRepository = campaignRepository;
        this.productMapper = productMapper;
    }
    @Override
    public CreatedProductResponse create(CreateProductRequest request) {

        ProductOffering productOffering = productOfferingRepository.findById(request.getProductOfferingId())
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait teklif bulunamadı! : " + request.getProductOfferingId()));


        ProductSpec productSpec = productSpecRepository.findById(request.getProductSpecId())
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait ürün tanımı bulunamadı! : " + request.getProductSpecId()));


        Product product = productMapper.toEntity(request);


        product.setProductOffering(productOffering);
        product.setProductSpec(productSpec);

        // OPSİYONEL ilişki: parentProduct (varsa bul + set)
        if (request.getParentProductId() != null) {
            Product parentProduct = productRepository.findById(request.getParentProductId())
                    .orElseThrow(() -> new RuntimeException(
                            "Girilen id'ye ait üst ürün bulunamadı! : " + request.getParentProductId()));
            product.setParentProduct(parentProduct);
        }

        // OPSİYONEL ilişki: campaign (varsa bul + set)
        if (request.getCampaignId() != null) {
            Campaign campaign = campaignRepository.findById(request.getCampaignId())
                    .orElseThrow(() -> new RuntimeException(
                            "Girilen id'ye ait kampanya bulunamadı! : " + request.getCampaignId()));
            product.setCampaign(campaign);
        }
        Product saved = productRepository.save(product); // id tanımlandı
        return productMapper.toCreatedResponse(saved);
    }

    @Override
    public UpdatedProductResponse update(Long productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait ürün bulunamadı! : " + productId));
        productMapper.updateEntityFromRequest(request, product);

        ProductOffering productOffering = productOfferingRepository.findById(request.getProductOfferingId())
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait teklif bulunamadı! : " + request.getProductOfferingId()));
        product.setProductOffering(productOffering);

        ProductSpec productSpec = productSpecRepository.findById(request.getProductSpecId())
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait ürün tanımı bulunamadı! : " + request.getProductSpecId()));
        product.setProductSpec(productSpec);

        if (request.getParentProductId() != null) {
            Product parentProduct = productRepository.findById(request.getParentProductId())
                    .orElseThrow(() -> new RuntimeException(
                            "Girilen id'ye ait üst ürün bulunamadı! : " + request.getParentProductId()));
            product.setParentProduct(parentProduct);
        } else {
            product.setParentProduct(null);
        }

        if (request.getCampaignId() != null) {
            Campaign campaign = campaignRepository.findById(request.getCampaignId())
                    .orElseThrow(() -> new RuntimeException(
                            "Girilen id'ye ait kampanya bulunamadı! : " + request.getCampaignId()));
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
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait ürün bulunamadı! : " + productId));

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
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait ürün bulunamadı! : " + productId));

        productRepository.delete(product);
    }

}
