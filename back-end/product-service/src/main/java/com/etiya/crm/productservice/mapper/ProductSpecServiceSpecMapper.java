package com.etiya.crm.productservice.mapper;

import com.etiya.crm.productservice.business.dtos.requests.ProductSpecServiceSpec.CreateProductSpecServiceSpecRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecServiceSpec.UpdateProductSpecServiceSpecRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.CreatedProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.GetAllProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.GetProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.UpdatedProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.entities.concretes.ProductSpecServiceSpec;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductSpecServiceSpecMapper {

    // ---------- CREATE ----------

    @Mapping(target = "productSpecServiceSpecId", ignore = true)
    @Mapping(target = "productSpec", ignore = true)
    @Mapping(target = "relationTypeId", ignore = true)
    @Mapping(target = "statusId", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    ProductSpecServiceSpec toEntity(CreateProductSpecServiceSpecRequest request);

    @Mapping(target = "productSpecId", source = "productSpec.productSpecId")
    CreatedProductSpecServiceSpecResponse toCreatedResponse(ProductSpecServiceSpec productSpecServiceSpec);

    // ---------- UPDATE ----------

    @Mapping(target = "productSpecServiceSpecId", ignore = true)
    @Mapping(target = "productSpec", ignore = true)
    @Mapping(target = "relationTypeId", ignore = true)
    @Mapping(target = "statusId", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    void updateEntityFromRequest(UpdateProductSpecServiceSpecRequest request, @MappingTarget ProductSpecServiceSpec productSpecServiceSpec);

    @Mapping(target = "productSpecId", source = "productSpec.productSpecId")
    UpdatedProductSpecServiceSpecResponse toUpdatedResponse(ProductSpecServiceSpec productSpecServiceSpec);

    // ---------- GET ----------

    @Mapping(target = "productSpecId", source = "productSpec.productSpecId")
    GetProductSpecServiceSpecResponse toGetResponse(ProductSpecServiceSpec productSpecServiceSpec);

    @Mapping(target = "productSpecId", source = "productSpec.productSpecId")
    GetAllProductSpecServiceSpecResponse toGetAllResponse(ProductSpecServiceSpec productSpecServiceSpec);

    List<GetAllProductSpecServiceSpecResponse> toGetAllResponseList(List<ProductSpecServiceSpec> productSpecServiceSpecs);
}
