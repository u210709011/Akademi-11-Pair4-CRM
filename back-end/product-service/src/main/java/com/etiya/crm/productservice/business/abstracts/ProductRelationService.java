package com.etiya.crm.productservice.business.abstracts;

import com.etiya.crm.productservice.business.dtos.requests.ProductRelation.CreateProductRelationRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductRelation.UpdateProductRelationRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.CreatedProductRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.GetAllProductRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.GetProductRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.UpdatedProductRelationResponse;

import java.util.List;

public interface ProductRelationService {

    CreatedProductRelationResponse create(CreateProductRelationRequest request);

    UpdatedProductRelationResponse update(Long productRelationId, UpdateProductRelationRequest request);

    GetProductRelationResponse getById(Long productRelationId);

    List<GetAllProductRelationResponse> getAll();

    void delete(Long productRelationId);
}
