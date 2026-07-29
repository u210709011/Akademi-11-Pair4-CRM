package com.etiya.crm.productservice.business.abstracts;

import com.etiya.crm.productservice.business.dtos.requests.ProductOffering.CreateProductOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductOffering.UpdateProductOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.CreatedProductOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.GetAllProductOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.GetProductOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.UpdatedProductOfferingResponse;

import java.util.List;

public interface ProductOfferingService {
    CreatedProductOfferingResponse create(CreateProductOfferingRequest request);
    UpdatedProductOfferingResponse update(Long productOfferingId, UpdateProductOfferingRequest request);
    GetProductOfferingResponse getById(Long productOfferingId);
    List<GetAllProductOfferingResponse> getAll();
    void delete(Long productOfferingId);
}