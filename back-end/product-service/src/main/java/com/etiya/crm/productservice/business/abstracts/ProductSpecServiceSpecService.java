package com.etiya.crm.productservice.business.abstracts;

import com.etiya.crm.productservice.business.dtos.requests.ProductSpecServiceSpec.CreateProductSpecServiceSpecRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecServiceSpec.UpdateProductSpecServiceSpecRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.CreatedProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.GetAllProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.GetProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.UpdatedProductSpecServiceSpecResponse;

import java.util.List;

public interface ProductSpecServiceSpecService {
    CreatedProductSpecServiceSpecResponse create(CreateProductSpecServiceSpecRequest request);

    UpdatedProductSpecServiceSpecResponse update(Long productSpecServiceSpecId, UpdateProductSpecServiceSpecRequest request);

    GetProductSpecServiceSpecResponse getById(Long productSpecServiceSpecId);

    List<GetAllProductSpecServiceSpecResponse> getAll();

    void delete(Long productSpecServiceSpecId);
}
