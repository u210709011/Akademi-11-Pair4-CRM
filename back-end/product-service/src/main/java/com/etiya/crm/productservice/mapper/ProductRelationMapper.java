package com.etiya.crm.productservice.mapper;

import com.etiya.crm.productservice.business.dtos.requests.ProductRelation.CreateProductRelationRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductRelation.UpdateProductRelationRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.CreatedProductRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.GetAllProductRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.GetProductRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.UpdatedProductRelationResponse;
import com.etiya.crm.productservice.entities.concretes.ProductRelation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductRelationMapper {
    // ---------- CREATE ----------

    /**
     * Request'ten entity uretir. Iki iliski de (product1, product2) Product'tir,
     * ignore edilir - Manager ikisini de productRepository'den bulup set eder.
     */
    @Mapping(target = "productRelationId", ignore = true)
    @Mapping(target = "relationTypeId", ignore = true)
    @Mapping(target = "product1", ignore = true)
    @Mapping(target = "product2", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    ProductRelation toEntity(CreateProductRelationRequest request);

    /** Entity -> response. Iki Product iliskisinden de ID cekiliyor. */
    @Mapping(target = "productId1", source = "product1.productId")
    @Mapping(target = "productId2", source = "product2.productId")
    CreatedProductRelationResponse toCreatedResponse(ProductRelation productRelation);

    // ---------- UPDATE ----------

    @Mapping(target = "productRelationId", ignore = true)
    @Mapping(target = "relationTypeId", ignore = true)
    @Mapping(target = "product1", ignore = true)
    @Mapping(target = "product2", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    void updateEntityFromRequest(UpdateProductRelationRequest request, @MappingTarget ProductRelation productRelation);

    @Mapping(target = "productId1", source = "product1.productId")
    @Mapping(target = "productId2", source = "product2.productId")
    UpdatedProductRelationResponse toUpdatedResponse(ProductRelation productRelation);

    // ---------- GET ----------

    @Mapping(target = "productId1", source = "product1.productId")
    @Mapping(target = "productId2", source = "product2.productId")
    GetProductRelationResponse toGetResponse(ProductRelation productRelation);

    @Mapping(target = "productId1", source = "product1.productId")
    @Mapping(target = "productId2", source = "product2.productId")
    GetAllProductRelationResponse toGetAllResponse(ProductRelation productRelation);

    List<GetAllProductRelationResponse> toGetAllResponseList(List<ProductRelation> productRelations);
}
