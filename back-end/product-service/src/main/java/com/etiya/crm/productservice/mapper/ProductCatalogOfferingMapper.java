package com.etiya.crm.productservice.mapper;

import com.etiya.crm.productservice.business.dtos.requests.ProductCatalogOffering.CreateProductCatalogOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalogOffering.UpdateProductCatalogOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.CreatedProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.GetAllProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.GetProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.UpdatedProductCatalogOfferingResponse;
import com.etiya.crm.productservice.entities.concretes.ProductCatalogOffering;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductCatalogOfferingMapper {

    // ---------- CREATE ----------

    /**
     * Request'ten entity uretir. Iki iliski de (productCatalog, productOffering)
     * burada doldurulmaz - Manager findById ile getirip elle set eder.
     */
    @Mapping(target = "productCatalogOfferingId", ignore = true)
    @Mapping(target = "productCatalog", ignore = true)
    @Mapping(target = "productOffering", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    ProductCatalogOffering toEntity(CreateProductCatalogOfferingRequest request);

    /** Entity -> response. Iki iliski nesnesinden de ID cekiliyor (nested mapping). */
    @Mapping(target = "productCatalogId", source = "productCatalog.productCatalogId")
    @Mapping(target = "productOfferingId", source = "productOffering.productOfferingId")
    CreatedProductCatalogOfferingResponse toCreatedResponse(ProductCatalogOffering productCatalogOffering);


    // ---------- UPDATE ----------

    @Mapping(target = "productCatalogOfferingId", ignore = true)
    @Mapping(target = "productCatalog", ignore = true)
    @Mapping(target = "productOffering", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    void updateEntityFromRequest(UpdateProductCatalogOfferingRequest request, @MappingTarget ProductCatalogOffering productCatalogOffering);

    @Mapping(target = "productCatalogId", source = "productCatalog.productCatalogId")
    @Mapping(target = "productOfferingId", source = "productOffering.productOfferingId")
    UpdatedProductCatalogOfferingResponse toUpdatedResponse(ProductCatalogOffering productCatalogOffering);

    // ---------- GET ----------

    @Mapping(target = "productCatalogId", source = "productCatalog.productCatalogId")
    @Mapping(target = "productOfferingId", source = "productOffering.productOfferingId")
    GetProductCatalogOfferingResponse toGetResponse(ProductCatalogOffering productCatalogOffering);

    @Mapping(target = "productCatalogId", source = "productCatalog.productCatalogId")
    @Mapping(target = "productOfferingId", source = "productOffering.productOfferingId")
    GetAllProductCatalogOfferingResponse toGetAllResponse(ProductCatalogOffering productCatalogOffering);

    List<GetAllProductCatalogOfferingResponse> toGetAllResponseList(List<ProductCatalogOffering> productCatalogOfferings);
}
