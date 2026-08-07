package com.etiya.crm.productservice.api.controllers;

import com.etiya.crm.productservice.business.abstracts.ProductSpecServiceSpecService;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecServiceSpec.CreateProductSpecServiceSpecRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecServiceSpec.UpdateProductSpecServiceSpecRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.CreatedProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.GetAllProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.GetProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.UpdatedProductSpecServiceSpecResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-spec-service-specs")
public class ProductSpecServiceSpecController {
    private final ProductSpecServiceSpecService productSpecServiceSpecService;

    public ProductSpecServiceSpecController(ProductSpecServiceSpecService productSpecServiceSpecService) {
        this.productSpecServiceSpecService = productSpecServiceSpecService;
    }

    @PostMapping
    public ResponseEntity<CreatedProductSpecServiceSpecResponse> create(
            @Valid @RequestBody CreateProductSpecServiceSpecRequest request) {
        CreatedProductSpecServiceSpecResponse response = productSpecServiceSpecService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{productSpecServiceSpecId}")
    public ResponseEntity<UpdatedProductSpecServiceSpecResponse> update(
            @PathVariable Long productSpecServiceSpecId,
            @Valid @RequestBody UpdateProductSpecServiceSpecRequest request) {
        UpdatedProductSpecServiceSpecResponse response = productSpecServiceSpecService.update(productSpecServiceSpecId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productSpecServiceSpecId}")
    public ResponseEntity<GetProductSpecServiceSpecResponse> getById(@PathVariable Long productSpecServiceSpecId) {
        GetProductSpecServiceSpecResponse response = productSpecServiceSpecService.getById(productSpecServiceSpecId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<GetAllProductSpecServiceSpecResponse>> getAll() {
        List<GetAllProductSpecServiceSpecResponse> response = productSpecServiceSpecService.getAll();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productSpecServiceSpecId}")
    public ResponseEntity<Void> delete(@PathVariable Long productSpecServiceSpecId) {
        productSpecServiceSpecService.delete(productSpecServiceSpecId);
        return ResponseEntity.noContent().build();
    }

}
