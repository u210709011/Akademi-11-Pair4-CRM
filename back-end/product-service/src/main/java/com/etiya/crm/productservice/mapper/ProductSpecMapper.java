package com.etiya.crm.productservice.mapper;

import java.util.List;

import com.etiya.crm.productservice.business.dtos.requests.ProductSpec.CreateProductSpecRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpec.UpdateProductSpecRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.CreatedProductSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.GetAllProductSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.GetProductSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.UpdatedProductSpecResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.etiya.crm.productservice.entities.concretes.ProductSpec;

/**
 * ProductSpec entity'si ile DTO'lar arasindaki donusumler.
 * Govde yazmiyoruz; MapStruct derleme aninda ProductSpecMapperImpl sinifini uretir
 * (target/generated-sources/annotations/... altinda gorulebilir).
 */
@Mapper(componentModel = "spring")
public interface ProductSpecMapper {

    // ---------- CREATE ----------

    /** Gelen istekten yeni entity uretir. Id ve audit alanlari disarida birakilir. */
    @Mapping(target = "productSpecId", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    ProductSpec toEntity(CreateProductSpecRequest request);

    CreatedProductSpecResponse toCreatedResponse(ProductSpec productSpec);

    // ---------- UPDATE ----------

    /**
     * Var olan entity'nin uzerine yazar (yeni nesne uretmez).
     * @MappingTarget sayesinde productSpecId ve audit alanlari korunur.
     */
    @Mapping(target = "productSpecId", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    void updateEntityFromRequest(UpdateProductSpecRequest request, @MappingTarget ProductSpec productSpec);

    UpdatedProductSpecResponse toUpdatedResponse(ProductSpec productSpec);

    // ---------- GET ----------

    GetProductSpecResponse toGetResponse(ProductSpec productSpec);

    /** Liste donusumu: MapStruct tekil metodu bulup her eleman icin calistirir. */
    List<GetAllProductSpecResponse> toGetAllResponseList(List<ProductSpec> productSpecs);
}