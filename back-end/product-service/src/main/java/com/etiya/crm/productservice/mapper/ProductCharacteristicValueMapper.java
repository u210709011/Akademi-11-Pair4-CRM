package com.etiya.crm.productservice.mapper;

import com.etiya.crm.productservice.business.dtos.requests.ProductCharacteristicValue.CreateProductCharacteristicValueRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCharacteristicValue.UpdateProductCharacteristicValueRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue.CreatedProductCharacteristicValueResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue.GetAllProductCharacteristicValueResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue.GetProductCharacteristicValueResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue.UpdatedProductCharacteristicValueResponse;
import com.etiya.crm.productservice.entities.concretes.ProductCharacteristicValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductCharacteristicValueMapper {
    @Mapping(target = "productCharacteristicValueId", ignore = true)
    @Mapping(target = "statusId", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    ProductCharacteristicValue toEntity(CreateProductCharacteristicValueRequest request);


    @Mapping(target = "productId", source = "product.productId")
    CreatedProductCharacteristicValueResponse toCreatedResponse(ProductCharacteristicValue productCharacteristicValue);

    @Mapping(target = "productCharacteristicValueId", ignore = true)
    @Mapping(target = "statusId", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    void updateEntityFromRequest(UpdateProductCharacteristicValueRequest request, @MappingTarget ProductCharacteristicValue productCharacteristicValue);

    @Mapping(target = "productId", source = "product.productId")
    UpdatedProductCharacteristicValueResponse toUpdatedResponse(ProductCharacteristicValue productCharacteristicValue);

    @Mapping(target = "productId", source = "product.productId")
    GetProductCharacteristicValueResponse toGetResponse(ProductCharacteristicValue productCharacteristicValue);

    @Mapping(target = "productId", source = "product.productId")
    GetAllProductCharacteristicValueResponse toGetAllResponse(ProductCharacteristicValue productCharacteristicValue);

    List<GetAllProductCharacteristicValueResponse> toGetAllResponseList(List<ProductCharacteristicValue> productCharacteristicValues);
}
