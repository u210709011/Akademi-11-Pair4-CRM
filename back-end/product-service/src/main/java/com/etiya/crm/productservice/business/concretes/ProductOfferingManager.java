package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.ProductOfferingService;
import com.etiya.crm.productservice.business.dtos.requests.ProductOffering.CreateProductOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductOffering.UpdateProductOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.CreatedProductCatalogResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.CreatedProductOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.GetAllProductOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.GetProductOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.UpdatedProductOfferingResponse;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductOfferingRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductSpecRepository;
import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import com.etiya.crm.productservice.entities.concretes.ProductSpec;
import com.etiya.crm.productservice.mapper.ProductOfferingMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductOfferingManager implements ProductOfferingService {

    private final ProductOfferingRepository productOfferingRepository;
    private final ProductSpecRepository productSpecRepository;
    private final ProductOfferingMapper productOfferingMapper;

    public ProductOfferingManager(ProductOfferingRepository productOfferingRepository, ProductSpecRepository productSpecRepository, ProductOfferingMapper productOfferingMapper) {
        this.productOfferingRepository = productOfferingRepository;
        this.productSpecRepository = productSpecRepository;
        this.productOfferingMapper = productOfferingMapper;
    }

    public CreatedProductOfferingResponse create(CreateProductOfferingRequest request) {
        ProductSpec productSpec = productSpecRepository.findById(request.getProductSpecId())
                .orElseThrow(() -> new RuntimeException("Girilen id' ye ait ürün tanımı bulunamadı : " +request.getProductSpecId()));

        ProductOffering productOffering = productOfferingMapper.toEntity(request);
        productOffering.setProductSpec(productSpec);

        if(request.getParentOfferingId() != null){
            ProductOffering parentOffering = productOfferingRepository.findById(request.getParentOfferingId())
                    .orElseThrow(() -> new RuntimeException("Girilen id'ye ait üst teklif bulunamadı : " +request.getParentOfferingId()));
            productOffering.setParentOffering(parentOffering);
        }
        ProductOffering saved = productOfferingRepository.save(productOffering);
        return productOfferingMapper.toCreatedResponse(saved);
    }

   @Override
    public UpdatedProductOfferingResponse update(Long productOfferingId, UpdateProductOfferingRequest request) {
        ProductOffering productOffering = productOfferingRepository.findById(productOfferingId)
                .orElseThrow(() -> new RuntimeException("Girilen id'ye ait teklif bulunamadı: " +productOfferingId));

        productOfferingMapper.updateEntityFromRequest(request,productOffering);

        ProductSpec productSpec = productSpecRepository.findById(request.getProductSpecId())
                .orElseThrow(() -> new RuntimeException("Girilen id'ye ait ürün tanımı bulunamadı: " +request.getProductSpecId()));

        productOffering.setProductSpec(productSpec);

        if(request.getParentOfferingId() != null){
            ProductOffering parentOffering = productOfferingRepository.findById(request.getParentOfferingId())
                    .orElseThrow(() -> new RuntimeException("Girilen id'ye ait üst teklif bulunamadı! : " + request.getParentOfferingId()));
            productOffering.setParentOffering(parentOffering);
        }else {
            productOffering.setParentOffering(null);
        }

        ProductOffering saved = productOfferingRepository.save(productOffering);
        return productOfferingMapper.toUpdatedResponse(saved);
    }

    @Override
    public GetProductOfferingResponse getById(Long productOfferingId) {
        ProductOffering productOffering = productOfferingRepository.findById(productOfferingId)
                .orElseThrow(() -> new RuntimeException("Girilen id'ye ait teklif bulunamadı! : " + productOfferingId));
        return productOfferingMapper.toGetResponse(productOffering);
    }

    @Override
    public List<GetAllProductOfferingResponse> getAll() {
        List<ProductOffering> productOfferings = productOfferingRepository.findAll();
        return productOfferingMapper.toGetAllResponseList(productOfferings);
    }

    // (TODO) bağlı kayıt kontrolü: katalog/kampanya/prod ilişkisi varsa silme engellenecek
    // (TODO) soft delete (statusId=Pasif) mantığı düşünülecek (rules katmanında halledecğim)
    @Override
    public void delete(Long productOfferingId) {
        ProductOffering productOffering = productOfferingRepository.findById(productOfferingId)
                .orElseThrow(() -> new RuntimeException("Girilen id'ye ait teklif bulunamadı! : " + productOfferingId));

        productOfferingRepository.delete(productOffering);
    }
}
