package com.etiya.crm.productservice.api.controllers;

import com.etiya.crm.productservice.business.abstracts.ProductService;
import com.etiya.crm.productservice.business.dtos.requests.Product.CreateProductRequest;
import com.etiya.crm.productservice.business.dtos.requests.Product.UpdateProductRequest;
import com.etiya.crm.productservice.business.dtos.responses.Product.CreatedProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.GetAllProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.GetProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.UpdatedProductResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<CreatedProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        CreatedProductResponse response = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<UpdatedProductResponse> update(@PathVariable Long productId, @Valid @RequestBody UpdateProductRequest request) {
        UpdatedProductResponse response = productService.update(productId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<GetProductResponse> getById(@PathVariable Long productId) {
        GetProductResponse response = productService.getById(productId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<GetAllProductResponse>> getAll() {
        List<GetAllProductResponse> response = productService.getAll();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(@PathVariable Long productId) {
        productService.delete(productId);
        return ResponseEntity.noContent().build();
    }
}
