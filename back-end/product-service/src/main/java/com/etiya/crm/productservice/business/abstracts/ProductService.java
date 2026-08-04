package com.etiya.crm.productservice.business.abstracts;

import com.etiya.crm.productservice.business.dtos.requests.Product.CreateProductRequest;
import com.etiya.crm.productservice.business.dtos.requests.Product.UpdateProductRequest;
import com.etiya.crm.productservice.business.dtos.responses.Product.CreatedProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.GetAllProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.GetProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.UpdatedProductResponse;

import java.util.List;

public interface ProductService {
    CreatedProductResponse create(CreateProductRequest request);

    UpdatedProductResponse update(Long productId, UpdateProductRequest request);

    GetProductResponse getById(Long productId);

    List<GetAllProductResponse> getAll();

    void delete(Long productId);
}
