package com.etiya.crm.productservice.api.controllers;

import com.etiya.crm.productservice.business.abstracts.ProductRelationService;
import com.etiya.crm.productservice.business.dtos.requests.ProductRelation.CreateProductRelationRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductRelation.UpdateProductRelationRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.CreatedProductRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.GetAllProductRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.GetProductRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.UpdatedProductRelationResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-relations")
public class ProductRelationController {

    private final ProductRelationService productReletionService;

    public ProductRelationController(ProductRelationService productReletionService) {
        this.productReletionService = productReletionService;
    }


    @PostMapping
    public ResponseEntity<CreatedProductRelationResponse> create(@Valid @RequestBody CreateProductRelationRequest request){
        CreatedProductRelationResponse response = productReletionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{productRelationId}")
    public ResponseEntity<UpdatedProductRelationResponse> update(@PathVariable Long productRelationId, @Valid @RequestBody UpdateProductRelationRequest request){
        UpdatedProductRelationResponse response = productReletionService.update(productRelationId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productRelationId}")
    public ResponseEntity<GetProductRelationResponse> getById(@PathVariable Long productRelationId){
        GetProductRelationResponse response = productReletionService.getById(productRelationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<GetAllProductRelationResponse>> getAll(){
        List<GetAllProductRelationResponse> response = productReletionService.getAll();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productRelationId}")
    public ResponseEntity<Void> delete(@PathVariable Long productRelationId){
        productReletionService.delete(productRelationId);
        return ResponseEntity.noContent().build();
    }
}