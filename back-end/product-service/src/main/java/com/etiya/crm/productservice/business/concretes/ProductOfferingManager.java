package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.abstracts.ProductOfferingService;
import com.etiya.crm.productservice.business.dtos.requests.ProductOffering.CreateProductOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductOffering.UpdateProductOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.CreatedProductOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.GetAllProductOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.GetProductOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.UpdatedProductOfferingResponse;
import com.etiya.crm.productservice.business.exceptions.ProductOfferingNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductSpecNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductOfferingRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductSpecRepository;
import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import com.etiya.crm.productservice.entities.concretes.ProductSpec;
import com.etiya.crm.productservice.mapper.ProductOfferingMapper;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductOfferingManager implements ProductOfferingService {

    private final ProductOfferingRepository productOfferingRepository;
    private final ProductSpecRepository productSpecRepository;
    private final ProductOfferingMapper productOfferingMapper;
    private final LookupCacheService lookupCacheService;

    public ProductOfferingManager(ProductOfferingRepository productOfferingRepository, ProductSpecRepository productSpecRepository, ProductOfferingMapper productOfferingMapper, LookupCacheService lookupCacheService) {
        this.productOfferingRepository = productOfferingRepository;
        this.productSpecRepository = productSpecRepository;
        this.productOfferingMapper = productOfferingMapper;
        this.lookupCacheService = lookupCacheService;
    }

    @Override
    public CreatedProductOfferingResponse create(CreateProductOfferingRequest request) {
        ProductSpec productSpec = productSpecRepository.findById(request.getProductSpecId())
                .orElseThrow(() -> new ProductSpecNotFoundException(request.getProductSpecId()));

        ProductOffering productOffering = productOfferingMapper.toEntity(request);
        productOffering.setProductSpec(productSpec);
        productOffering.setStatusId(
                lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_OFFER, request.getStatusCode()));

        if(request.getParentOfferingId() != null){
            ProductOffering parentOffering = productOfferingRepository.findById(request.getParentOfferingId())
                    .orElseThrow(() -> new ProductOfferingNotFoundException(request.getParentOfferingId()));
            productOffering.setParentOffering(parentOffering);
        }
        ProductOffering saved = productOfferingRepository.save(productOffering);
        return productOfferingMapper.toCreatedResponse(saved);
    }

   @Override
    public UpdatedProductOfferingResponse update(Long productOfferingId, UpdateProductOfferingRequest request) {
        ProductOffering productOffering = productOfferingRepository.findById(productOfferingId)
                .orElseThrow(() -> new ProductOfferingNotFoundException(productOfferingId));

        productOfferingMapper.updateEntityFromRequest(request,productOffering);
        productOffering.setStatusId(
                lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_OFFER, request.getStatusCode()));

        ProductSpec productSpec = productSpecRepository.findById(request.getProductSpecId())
                .orElseThrow(() -> new ProductSpecNotFoundException(request.getProductSpecId()));

        productOffering.setProductSpec(productSpec);

        if(request.getParentOfferingId() != null){
            ProductOffering parentOffering = productOfferingRepository.findById(request.getParentOfferingId())
                    .orElseThrow(() -> new ProductOfferingNotFoundException(request.getParentOfferingId()));
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
                .orElseThrow(() -> new ProductOfferingNotFoundException(productOfferingId));
        return productOfferingMapper.toGetResponse(productOffering);
    }

    @Override
    public List<GetAllProductOfferingResponse> getAll() {
        List<ProductOffering> productOfferings = productOfferingRepository.findAll();
        return productOfferingMapper.toGetAllResponseList(productOfferings);
    }

    // (TODO) bağlı kayıt kontrolü: katalog/kampanya/prod ilişkisi varsa silme engellenecek
    @Override
    public void delete(Long productOfferingId) {
        ProductOffering productOffering = productOfferingRepository.findById(productOfferingId)
                .orElseThrow(() -> new ProductOfferingNotFoundException(productOfferingId));

        productOffering.setStatusId(
                lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_OFFER, GnlStCodes.DELETED));
        productOfferingRepository.save(productOffering);
    }
}
