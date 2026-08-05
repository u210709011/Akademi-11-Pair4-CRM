package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.ProductCatalogOfferingService;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalogOffering.CreateProductCatalogOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalogOffering.UpdateProductCatalogOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.CreatedProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.GetAllProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.GetProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.UpdatedProductCatalogOfferingResponse;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductCatalogOfferingRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductCatalogRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductOfferingRepository;
import com.etiya.crm.productservice.entities.concretes.ProductCatalog;
import com.etiya.crm.productservice.entities.concretes.ProductCatalogOffering;
import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import com.etiya.crm.productservice.mapper.ProductCatalogOfferingMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductCatalogOfferingManager implements ProductCatalogOfferingService {

    private final ProductCatalogOfferingRepository productCatalogOfferingRepository;
    private final ProductCatalogRepository productCatalogRepository;
    private final ProductOfferingRepository productOfferingRepository;
    private final ProductCatalogOfferingMapper productCatalogOfferingMapper;

    public ProductCatalogOfferingManager(ProductCatalogOfferingRepository productCatalogOfferingRepository, ProductCatalogRepository productCatalogRepository, ProductOfferingRepository productOfferingRepository, ProductCatalogOfferingMapper productCatalogOfferingMapper) {
        this.productCatalogOfferingRepository = productCatalogOfferingRepository;
        this.productCatalogRepository = productCatalogRepository;
        this.productOfferingRepository = productOfferingRepository;
        this.productCatalogOfferingMapper = productCatalogOfferingMapper;
    }

    @Override
    public CreatedProductCatalogOfferingResponse create(CreateProductCatalogOfferingRequest request) {
        ProductCatalog productCatalog = productCatalogRepository.findById(request.getProductCatalogId())
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait katalog bulunamadı! : " + request.getProductCatalogId()));

        ProductOffering productOffering = productOfferingRepository.findById(request.getProductOfferingId())
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait teklif bulunamadı! : " + request.getProductOfferingId()));


        //Mapper düz alanları çeviriyor (statusId), ilişkiler null
        ProductCatalogOffering productCatalogOffering = productCatalogOfferingMapper.toEntity(request);

        productCatalogOffering.setProductCatalog(productCatalog);
        productCatalogOffering.setProductOffering(productOffering);
        ProductCatalogOffering saved = productCatalogOfferingRepository.save(productCatalogOffering);
        return productCatalogOfferingMapper.toCreatedResponse(saved);
    }

    @Override
    public UpdatedProductCatalogOfferingResponse update(Long productCatalogOfferingId, UpdateProductCatalogOfferingRequest request) {
        // 1) Güncellenecek mevcut eşleşmeyi bul
        ProductCatalogOffering productCatalogOffering = productCatalogOfferingRepository
                .findById(productCatalogOfferingId)
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait eşleşme bulunamadı! : " + productCatalogOfferingId));

        // 2) Mapper düz alanları günceller (statusId), ilişkilere dokunmaz
        productCatalogOfferingMapper.updateEntityFromRequest(request, productCatalogOffering);

        // 3) İlişki 1'i güncelle
        ProductCatalog productCatalog = productCatalogRepository.findById(request.getProductCatalogId())
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait katalog bulunamadı! : " + request.getProductCatalogId()));
        productCatalogOffering.setProductCatalog(productCatalog);

        // 4) İlişki 2'yi güncelle
        ProductOffering productOffering = productOfferingRepository.findById(request.getProductOfferingId())
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait teklif bulunamadı! : " + request.getProductOfferingId()));
        productCatalogOffering.setProductOffering(productOffering);

        ProductCatalogOffering saved = productCatalogOfferingRepository.save(productCatalogOffering);
        return productCatalogOfferingMapper.toUpdatedResponse(saved);
    }

    @Override
    public GetProductCatalogOfferingResponse getById(Long productCatalogOfferingId) {
        ProductCatalogOffering productCatalogOffering = productCatalogOfferingRepository
                .findById(productCatalogOfferingId)
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait eşleşme bulunamadı! : " + productCatalogOfferingId));

        return productCatalogOfferingMapper.toGetResponse(productCatalogOffering);
    }

    @Override
    public List<GetAllProductCatalogOfferingResponse> getAll() {
        List<ProductCatalogOffering> productCatalogOfferings = productCatalogOfferingRepository.findAll();
        return productCatalogOfferingMapper.toGetAllResponseList(productCatalogOfferings);
    }


    @Override
    public void delete(Long productCatalogOfferingId) {
        ProductCatalogOffering productCatalogOffering = productCatalogOfferingRepository
                .findById(productCatalogOfferingId)
                .orElseThrow(() -> new RuntimeException(
                        "Girilen id'ye ait eşleşme bulunamadı! : " + productCatalogOfferingId));
        productCatalogOfferingRepository.delete(productCatalogOffering);
    }
}
