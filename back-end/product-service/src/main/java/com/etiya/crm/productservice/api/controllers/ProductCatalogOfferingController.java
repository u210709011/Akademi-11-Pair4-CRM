package com.etiya.crm.productservice.api.controllers;

import com.etiya.crm.productservice.business.abstracts.ProductCatalogOfferingService;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalogOffering.CreateProductCatalogOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalogOffering.UpdateProductCatalogOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.CreatedProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.GetAllProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.GetProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.UpdatedProductCatalogOfferingResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-catalog-offerings")
public class ProductCatalogOfferingController {
    private final ProductCatalogOfferingService productCatalogOfferingService;

    public ProductCatalogOfferingController(ProductCatalogOfferingService productCatalogOfferingService) {
        this.productCatalogOfferingService = productCatalogOfferingService;
    }

    @PostMapping
    public ResponseEntity<CreatedProductCatalogOfferingResponse> create(@Valid @RequestBody CreateProductCatalogOfferingRequest request) {
        CreatedProductCatalogOfferingResponse response = productCatalogOfferingService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{productCatalogOfferingId}")
    public ResponseEntity<UpdatedProductCatalogOfferingResponse> update(@PathVariable Long productCatalogOfferingId, @Valid @RequestBody UpdateProductCatalogOfferingRequest request) {
        UpdatedProductCatalogOfferingResponse response = productCatalogOfferingService.update(productCatalogOfferingId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productCatalogOfferingId}")
    public ResponseEntity<GetProductCatalogOfferingResponse> getById(@PathVariable Long productCatalogOfferingId) {
        GetProductCatalogOfferingResponse response = productCatalogOfferingService.getById(productCatalogOfferingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<GetAllProductCatalogOfferingResponse>> getAll() {
        List<GetAllProductCatalogOfferingResponse> response = productCatalogOfferingService.getAll();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productCatalogOfferingId}")
    public ResponseEntity<Void> delete(@PathVariable Long productCatalogOfferingId) {
        productCatalogOfferingService.delete(productCatalogOfferingId);
        return ResponseEntity.noContent().build();
    }
}
