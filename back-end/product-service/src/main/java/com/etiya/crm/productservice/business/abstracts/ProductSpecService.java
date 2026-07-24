package com.etiya.crm.productservice.business.abstracts;

import com.etiya.crm.productservice.business.dtos.requests.ProductSpec.CreateProductSpecRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpec.UpdateProductSpecRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.CreatedProductSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.GetAllProductSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.GetProductSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.UpdatedProductSpecResponse;

import java.util.List;

public interface ProductSpecService {
    CreatedProductSpecResponse create(CreateProductSpecRequest request);

    UpdatedProductSpecResponse update(Long productSpecId, UpdateProductSpecRequest request);

    GetProductSpecResponse getById(Long productSpecId);

    List<GetAllProductSpecResponse> getAll();

    void delete(Long productSpecId);
}
