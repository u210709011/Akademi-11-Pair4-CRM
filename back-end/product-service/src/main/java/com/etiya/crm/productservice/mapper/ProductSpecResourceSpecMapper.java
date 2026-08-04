package com.etiya.crm.productservice.mapper;

import com.etiya.crm.productservice.business.dtos.requests.ProductSpecResourceSpec.CreateProductSpecResourceSpecRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecResourceSpec.UpdateProductSpecResourceSpecRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.CreatedProductSpecResourceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.GetAllProductSpecResourceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.GetProductSpecResourceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.UpdatedProductSpecResourceSpecResponse;
import com.etiya.crm.productservice.entities.concretes.ProductSpecResourceSpec;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

public interface ProductSpecResourceSpecMapper {
    // ---------- CREATE ----------

    /**
     * Request'ten entity uretir. Sadece productSpec iliskisi ignore edilir (Manager kurar).
     * resourceSpecId, relationTypeId, statusId duz Long'dur (lookup) - otomatik kopyalanir.
     */
    @Mapping(target = "productSpecResourceSpecId", ignore = true)
    @Mapping(target = "productSpec", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    ProductSpecResourceSpec toEntity(CreateProductSpecResourceSpecRequest request);

    /** Entity -> response. Sadece productSpec'ten ID cekiliyor (tek nested mapping). */
    @Mapping(target = "productSpecId", source = "productSpec.productSpecId")
    CreatedProductSpecResourceSpecResponse toCreatedResponse(ProductSpecResourceSpec productSpecResourceSpec);

    // ---------- UPDATE ----------

    @Mapping(target = "productSpecResourceSpecId", ignore = true)
    @Mapping(target = "productSpec", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    void updateEntityFromRequest(UpdateProductSpecResourceSpecRequest request, @MappingTarget ProductSpecResourceSpec productSpecResourceSpec);

    @Mapping(target = "productSpecId", source = "productSpec.productSpecId")
    UpdatedProductSpecResourceSpecResponse toUpdatedResponse(ProductSpecResourceSpec productSpecResourceSpec);

    // ---------- GET ----------

    @Mapping(target = "productSpecId", source = "productSpec.productSpecId")
    GetProductSpecResourceSpecResponse toGetResponse(ProductSpecResourceSpec productSpecResourceSpec);

    @Mapping(target = "productSpecId", source = "productSpec.productSpecId")
    GetAllProductSpecResourceSpecResponse toGetAllResponse(ProductSpecResourceSpec productSpecResourceSpec);

    List<GetAllProductSpecResourceSpecResponse> toGetAllResponseList(List<ProductSpecResourceSpec> productSpecResourceSpecs);
}
