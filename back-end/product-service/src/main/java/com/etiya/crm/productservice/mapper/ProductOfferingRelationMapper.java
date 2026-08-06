package com.etiya.crm.productservice.mapper;

import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingRelation.CreateProductOfferingRelationRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingRelation.UpdateProductOfferingRelationRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.CreatedProductOfferingRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.GetAllProductOfferingRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.GetProductOfferingRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.UpdatedProductOfferingRelationResponse;
import com.etiya.crm.productservice.entities.concretes.ProductOfferingRelation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductOfferingRelationMapper {
    // ---------- CREATE ----------

    @Mapping(target = "productOfferingRelationId", ignore = true)
    @Mapping(target = "productOffering1", ignore = true)
    @Mapping(target = "productOffering2", ignore = true)
    @Mapping(target = "relationTypeId", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    ProductOfferingRelation toEntity(CreateProductOfferingRelationRequest request);

    @Mapping(target = "productOfferingId1", source = "productOffering1.productOfferingId")
    @Mapping(target = "productOfferingId2", source = "productOffering2.productOfferingId")
    CreatedProductOfferingRelationResponse toCreatedResponse(ProductOfferingRelation productOfferingRelation);

    // ---------- UPDATE ----------

    @Mapping(target = "productOfferingRelationId", ignore = true)
    @Mapping(target = "productOffering1", ignore = true)
    @Mapping(target = "productOffering2", ignore = true)
    @Mapping(target = "relationTypeId", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    void updateEntityFromRequest(UpdateProductOfferingRelationRequest request, @MappingTarget ProductOfferingRelation productOfferingRelation);

    @Mapping(target = "productOfferingId1", source = "productOffering1.productOfferingId")
    @Mapping(target = "productOfferingId2", source = "productOffering2.productOfferingId")
    UpdatedProductOfferingRelationResponse toUpdatedResponse(ProductOfferingRelation productOfferingRelation);

    // ---------- GET ----------

    @Mapping(target = "productOfferingId1", source = "productOffering1.productOfferingId")
    @Mapping(target = "productOfferingId2", source = "productOffering2.productOfferingId")
    GetProductOfferingRelationResponse toGetResponse(ProductOfferingRelation productOfferingRelation);

    @Mapping(target = "productOfferingId1", source = "productOffering1.productOfferingId")
    @Mapping(target = "productOfferingId2", source = "productOffering2.productOfferingId")
    GetAllProductOfferingRelationResponse toGetAllResponse(ProductOfferingRelation productOfferingRelation);

    List<GetAllProductOfferingRelationResponse> toGetAllResponseList(List<ProductOfferingRelation> productOfferingRelations);
}