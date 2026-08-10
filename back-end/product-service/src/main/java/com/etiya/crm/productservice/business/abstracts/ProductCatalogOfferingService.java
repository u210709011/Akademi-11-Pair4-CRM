package com.etiya.crm.productservice.business.abstracts;

import com.etiya.crm.productservice.business.dtos.requests.ProductCatalogOffering.CreateProductCatalogOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalogOffering.UpdateProductCatalogOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.CreatedProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.GetAllProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.GetProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.UpdatedProductCatalogOfferingResponse;

import java.util.List;

public interface ProductCatalogOfferingService {
    CreatedProductCatalogOfferingResponse create(CreateProductCatalogOfferingRequest request);
    UpdatedProductCatalogOfferingResponse update(Long productCatalogOfferingId, UpdateProductCatalogOfferingRequest request);
    GetProductCatalogOfferingResponse getById(Long productCatalogOfferingId);
    List<GetAllProductCatalogOfferingResponse> getAll();
    List<GetAllProductCatalogOfferingResponse> getByCatalogId(Long productCatalogId);
    void delete(Long productCatalogOfferingId);
}
