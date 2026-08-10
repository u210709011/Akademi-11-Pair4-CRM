package com.etiya.crm.productservice.business.abstracts;

import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingRelation.CreateProductOfferingRelationRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingRelation.UpdateProductOfferingRelationRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.CreatedProductOfferingRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.GetAllProductOfferingRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.GetProductOfferingRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.UpdatedProductOfferingRelationResponse;

import java.util.List;

public interface ProductOfferingRelationService {

    CreatedProductOfferingRelationResponse create(CreateProductOfferingRelationRequest request);

    UpdatedProductOfferingRelationResponse update(Long productOfferingRelationId, UpdateProductOfferingRelationRequest request);

    GetProductOfferingRelationResponse getById(Long productOfferingRelationId);

    List<GetAllProductOfferingRelationResponse> getAll();

    List<GetAllProductOfferingRelationResponse> getByProductOfferingId(Long productOfferingId);

    void delete(Long productOfferingRelationId);
}