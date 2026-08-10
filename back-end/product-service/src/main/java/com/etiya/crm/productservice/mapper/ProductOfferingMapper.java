package com.etiya.crm.productservice.mapper;

import com.etiya.crm.productservice.business.dtos.requests.ProductOffering.CreateProductOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductOffering.UpdateProductOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.CreatedProductOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.GetAllProductOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.GetProductOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.UpdatedProductOfferingResponse;
import com.etiya.crm.productservice.constants.ProductServiceDefaults;
import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductOfferingMapper {


    // ---------- CREATE ----------

    /**
     * Request'ten entity uretir. Iliskiler (productSpec, parentOffering) burada
     * doldurulmaz - Manager findById ile getirip elle set eder. O yuzden ignore.
     */

    @Mapping(target = "productOfferingId", ignore = true)
    @Mapping(target = "productSpec", ignore = true)
    @Mapping(target = "parentOffering", ignore = true)
    @Mapping(target = "statusId", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    ProductOffering toEntity(CreateProductOfferingRequest request);

    /**
     * Entity -> response. Iliski nesnelerinden ID'leri cekiyoruz (nested mapping).
     * parentOffering null olabilir; MapStruct null-safe davranir, parentOfferingId null kalir.
     */

    @Mapping(target = "productOfferingNo", expression = "java(formatNo(productOffering.getProductOfferingId()))")
    @Mapping(target = "productSpecId", source = "productSpec.productSpecId")
    @Mapping(target = "parentOfferingId", source = "parentOffering.productOfferingId")
    CreatedProductOfferingResponse toCreatedResponse(ProductOffering productOffering);


    // ---------- UPDATE ----------

    @Mapping(target = "productOfferingId", ignore = true)
    @Mapping(target = "productSpec", ignore = true)
    @Mapping(target = "parentOffering", ignore = true)
    @Mapping(target = "statusId", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    void updateEntityFromRequest(UpdateProductOfferingRequest request, @MappingTarget ProductOffering productOffering);

    @Mapping(target = "productOfferingNo", expression = "java(formatNo(productOffering.getProductOfferingId()))")
    @Mapping(target = "productSpecId", source = "productSpec.productSpecId")
    @Mapping(target = "parentOfferingId", source = "parentOffering.productOfferingId")
    UpdatedProductOfferingResponse toUpdatedResponse(ProductOffering productOffering);

    // ---------- GET ----------

    @Mapping(target = "productOfferingNo", expression = "java(formatNo(productOffering.getProductOfferingId()))")
    @Mapping(target = "productSpecId", source = "productSpec.productSpecId")
    @Mapping(target = "parentOfferingId", source = "parentOffering.productOfferingId")
    GetProductOfferingResponse toGetResponse(ProductOffering productOffering);

    @Mapping(target = "productOfferingNo", expression = "java(formatNo(productOffering.getProductOfferingId()))")
    @Mapping(target = "productSpecId", source = "productSpec.productSpecId")
    @Mapping(target = "parentOfferingId", source = "parentOffering.productOfferingId")
    GetAllProductOfferingResponse toGetAllResponse(ProductOffering productOffering);

    List<GetAllProductOfferingResponse> toGetAllResponseList(List<ProductOffering> productOfferings);

    default String formatNo(Long productOfferingId) {
        return productOfferingId == null ? null : ProductServiceDefaults.formatNo(productOfferingId);
    }
}
