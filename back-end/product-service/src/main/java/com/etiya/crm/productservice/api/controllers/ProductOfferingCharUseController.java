package com.etiya.crm.productservice.api.controllers;

import com.etiya.crm.productservice.business.abstracts.ProductOfferingCharUseService;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingCharUse.CreateProductOfferingCharUseRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingCharUse.UpdateProductOfferingCharUseRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingCharUse.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-offering-char-uses")
public class ProductOfferingCharUseController {

    private final ProductOfferingCharUseService productOfferingCharUseService;

    public ProductOfferingCharUseController(ProductOfferingCharUseService productOfferingCharUseService) {
        this.productOfferingCharUseService = productOfferingCharUseService;
    }

    @PostMapping
    public ResponseEntity<CreatedProductOfferingCharUseResponse> create(@Valid @RequestBody CreateProductOfferingCharUseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productOfferingCharUseService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdatedProductOfferingCharUseResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateProductOfferingCharUseRequest request) {
        return ResponseEntity.ok(productOfferingCharUseService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetProductOfferingCharUseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productOfferingCharUseService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<GetAllProductOfferingCharUseResponse>> getAll() {
        return ResponseEntity.ok(productOfferingCharUseService.getAll());
    }

    @GetMapping("/by-offering/{productOfferingId}")
    public ResponseEntity<List<GetAllProductOfferingCharUseResponse>> getByProductOfferingId(@PathVariable Long productOfferingId) {
        return ResponseEntity.ok(productOfferingCharUseService.getByProductOfferingId(productOfferingId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productOfferingCharUseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}