package com.etiya.crm.productservice.api.controllers;

import com.etiya.crm.productservice.business.abstracts.ProductOfferingRelationService;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingRelation.CreateProductOfferingRelationRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingRelation.UpdateProductOfferingRelationRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.CreatedProductOfferingRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.GetAllProductOfferingRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.GetProductOfferingRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.UpdatedProductOfferingRelationResponse;
import com.etiya.crm.productservice.constants.SwaggerText;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = SwaggerText.PRODUCT_OFFERING_RELATION_TAG_NAME, description = SwaggerText.PRODUCT_OFFERING_RELATION_TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/product-offering-relations")
public class ProductOfferingRelationController {

    private final ProductOfferingRelationService productOfferingRelationService;

    public ProductOfferingRelationController(ProductOfferingRelationService productOfferingRelationService) {
        this.productOfferingRelationService = productOfferingRelationService;
    }

    @Operation(summary = SwaggerText.PRODUCT_OFFERING_RELATION_CREATE_SUMMARY)
    @PostMapping
    public ResponseEntity<CreatedProductOfferingRelationResponse> create(@Valid @RequestBody CreateProductOfferingRelationRequest request) {
        CreatedProductOfferingRelationResponse response = productOfferingRelationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = SwaggerText.PRODUCT_OFFERING_RELATION_UPDATE_SUMMARY)
    @PutMapping("/{productOfferingRelationId}")
    public ResponseEntity<UpdatedProductOfferingRelationResponse> update(@PathVariable Long productOfferingRelationId, @Valid @RequestBody UpdateProductOfferingRelationRequest request) {
        UpdatedProductOfferingRelationResponse response = productOfferingRelationService.update(productOfferingRelationId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.PRODUCT_OFFERING_RELATION_GET_BY_ID_SUMMARY)
    @GetMapping("/{productOfferingRelationId}")
    public ResponseEntity<GetProductOfferingRelationResponse> getById(@PathVariable Long productOfferingRelationId) {
        GetProductOfferingRelationResponse response = productOfferingRelationService.getById(productOfferingRelationId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.PRODUCT_OFFERING_RELATION_GET_ALL_SUMMARY)
    @GetMapping
    public ResponseEntity<List<GetAllProductOfferingRelationResponse>> getAll() {
        List<GetAllProductOfferingRelationResponse> response = productOfferingRelationService.getAll();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.PRODUCT_OFFERING_RELATION_GET_BY_OFFERING_SUMMARY)
    @GetMapping("/by-offering/{productOfferingId}")
    public ResponseEntity<List<GetAllProductOfferingRelationResponse>> getByProductOfferingId(@PathVariable Long productOfferingId) {
        List<GetAllProductOfferingRelationResponse> response = productOfferingRelationService.getByProductOfferingId(productOfferingId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.PRODUCT_OFFERING_RELATION_DELETE_SUMMARY)
    @DeleteMapping("/{productOfferingRelationId}")
    public ResponseEntity<Void> delete(@PathVariable Long productOfferingRelationId) {
        productOfferingRelationService.delete(productOfferingRelationId);
        return ResponseEntity.noContent().build();
    }
}