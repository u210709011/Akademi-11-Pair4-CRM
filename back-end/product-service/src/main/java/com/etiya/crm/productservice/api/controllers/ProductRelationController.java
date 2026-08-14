package com.etiya.crm.productservice.api.controllers;

import com.etiya.crm.productservice.business.abstracts.ProductRelationService;
import com.etiya.crm.productservice.business.dtos.requests.ProductRelation.CreateProductRelationRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductRelation.UpdateProductRelationRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.CreatedProductRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.GetAllProductRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.GetProductRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.UpdatedProductRelationResponse;
import com.etiya.crm.productservice.constants.SwaggerText;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = SwaggerText.PRODUCT_RELATION_TAG_NAME, description = SwaggerText.PRODUCT_RELATION_TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/product-relations")
public class ProductRelationController {

    private final ProductRelationService productRelationService;

    public ProductRelationController(ProductRelationService productReletionService) {
        this.productRelationService = productReletionService;
    }


    @Operation(summary = SwaggerText.PRODUCT_RELATION_CREATE_SUMMARY)
    @PostMapping
    public ResponseEntity<CreatedProductRelationResponse> create(@Valid @RequestBody CreateProductRelationRequest request){
        CreatedProductRelationResponse response = productRelationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = SwaggerText.PRODUCT_RELATION_UPDATE_SUMMARY)
    @PutMapping("/{productRelationId}")
    public ResponseEntity<UpdatedProductRelationResponse> update(@PathVariable Long productRelationId, @Valid @RequestBody UpdateProductRelationRequest request){
        UpdatedProductRelationResponse response = productRelationService.update(productRelationId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.PRODUCT_RELATION_GET_BY_ID_SUMMARY)
    @GetMapping("/{productRelationId}")
    public ResponseEntity<GetProductRelationResponse> getById(@PathVariable Long productRelationId){
        GetProductRelationResponse response = productRelationService.getById(productRelationId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.PRODUCT_RELATION_GET_ALL_SUMMARY)
    @GetMapping
    public ResponseEntity<List<GetAllProductRelationResponse>> getAll(){
        List<GetAllProductRelationResponse> response = productRelationService.getAll();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.PRODUCT_RELATION_DELETE_SUMMARY)
    @DeleteMapping("/{productRelationId}")
    public ResponseEntity<Void> delete(@PathVariable Long productRelationId){
        productRelationService.delete(productRelationId);
        return ResponseEntity.noContent().build();
    }
}