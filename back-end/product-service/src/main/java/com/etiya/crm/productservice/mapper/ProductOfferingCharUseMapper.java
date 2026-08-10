package com.etiya.crm.productservice.mapper;

import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingCharUse.CreateProductOfferingCharUseRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingCharUse.UpdateProductOfferingCharUseRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingCharUse.*;
import com.etiya.crm.productservice.entities.concretes.ProductOfferingCharUse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductOfferingCharUseMapper {

    @Mapping(target = "productOfferingCharUseId", ignore = true)
    @Mapping(target = "productOffering", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    ProductOfferingCharUse toEntity(CreateProductOfferingCharUseRequest request);

    @Mapping(target = "productOfferingId", source = "productOffering.productOfferingId")
    CreatedProductOfferingCharUseResponse toCreatedResponse(ProductOfferingCharUse entity);

    @Mapping(target = "productOfferingCharUseId", ignore = true)
    @Mapping(target = "productOffering", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    void updateEntityFromRequest(UpdateProductOfferingCharUseRequest request, @MappingTarget ProductOfferingCharUse entity);

    @Mapping(target = "productOfferingId", source = "productOffering.productOfferingId")
    UpdatedProductOfferingCharUseResponse toUpdatedResponse(ProductOfferingCharUse entity);

    @Mapping(target = "productOfferingId", source = "productOffering.productOfferingId")
    GetProductOfferingCharUseResponse toGetResponse(ProductOfferingCharUse entity);

    @Mapping(target = "productOfferingId", source = "productOffering.productOfferingId")
    GetAllProductOfferingCharUseResponse toGetAllResponse(ProductOfferingCharUse entity);

    List<GetAllProductOfferingCharUseResponse> toGetAllResponseList(List<ProductOfferingCharUse> entities);
}