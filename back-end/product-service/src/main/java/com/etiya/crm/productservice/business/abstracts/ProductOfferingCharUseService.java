package com.etiya.crm.productservice.business.abstracts;

import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingCharUse.CreateProductOfferingCharUseRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingCharUse.UpdateProductOfferingCharUseRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingCharUse.*;

import java.util.List;

public interface ProductOfferingCharUseService {
    CreatedProductOfferingCharUseResponse create(CreateProductOfferingCharUseRequest request);
    UpdatedProductOfferingCharUseResponse update(Long id, UpdateProductOfferingCharUseRequest request);
    GetProductOfferingCharUseResponse getById(Long id);
    List<GetAllProductOfferingCharUseResponse> getAll();
    List<GetAllProductOfferingCharUseResponse> getByProductOfferingId(Long productOfferingId);
    void delete(Long id);
}