package com.etiya.crm.productservice.api.controllers;

import com.etiya.crm.productservice.business.abstracts.ProductSpecResourceSpecService;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecResourceSpec.CreateProductSpecResourceSpecRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecResourceSpec.UpdateProductSpecResourceSpecRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.CreatedProductSpecResourceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.GetAllProductSpecResourceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.GetProductSpecResourceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.UpdatedProductSpecResourceSpecResponse;
import com.etiya.crm.productservice.constants.SwaggerText;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = SwaggerText.PRODUCT_SPEC_RESOURCE_SPEC_TAG_NAME, description = SwaggerText.PRODUCT_SPEC_RESOURCE_SPEC_TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/product-spec-resource-specs")
public class ProductSpecResourceSpecController {
    private final ProductSpecResourceSpecService productSpecResourceSpecService;

    public ProductSpecResourceSpecController(ProductSpecResourceSpecService productSpecResourceSpecService) {
        this.productSpecResourceSpecService = productSpecResourceSpecService;
    }

    @Operation(summary = SwaggerText.PRODUCT_SPEC_RESOURCE_SPEC_CREATE_SUMMARY)
    @PostMapping
    public ResponseEntity<CreatedProductSpecResourceSpecResponse> create(
            @Valid @RequestBody CreateProductSpecResourceSpecRequest request) {
        CreatedProductSpecResourceSpecResponse response = productSpecResourceSpecService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = SwaggerText.RELATION_UPDATE_SUMMARY)
    @PutMapping("/{productSpecResourceSpecId}")
    public ResponseEntity<UpdatedProductSpecResourceSpecResponse> update(
            @PathVariable Long productSpecResourceSpecId,
            @Valid @RequestBody UpdateProductSpecResourceSpecRequest request) {
        UpdatedProductSpecResourceSpecResponse response = productSpecResourceSpecService.update(productSpecResourceSpecId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.RELATION_GET_BY_ID_SUMMARY)
    @GetMapping("/{productSpecResourceSpecId}")
    public ResponseEntity<GetProductSpecResourceSpecResponse> getById(@PathVariable Long productSpecResourceSpecId) {
        GetProductSpecResourceSpecResponse response = productSpecResourceSpecService.getById(productSpecResourceSpecId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.RELATION_GET_ALL_SUMMARY)
    @GetMapping
    public ResponseEntity<List<GetAllProductSpecResourceSpecResponse>> getAll() {
        List<GetAllProductSpecResourceSpecResponse> response = productSpecResourceSpecService.getAll();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.RELATION_DELETE_SUMMARY)
    @DeleteMapping("/{productSpecResourceSpecId}")
    public ResponseEntity<Void> delete(@PathVariable Long productSpecResourceSpecId) {
        productSpecResourceSpecService.delete(productSpecResourceSpecId);
        return ResponseEntity.noContent().build();
    }
}
