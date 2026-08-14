package com.etiya.crm.productservice.api.controllers;

import com.etiya.crm.productservice.business.abstracts.ProductSpecServiceSpecService;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecServiceSpec.CreateProductSpecServiceSpecRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecServiceSpec.UpdateProductSpecServiceSpecRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.CreatedProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.GetAllProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.GetProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.UpdatedProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.constants.SwaggerText;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = SwaggerText.PRODUCT_SPEC_SERVICE_SPEC_TAG_NAME, description = SwaggerText.PRODUCT_SPEC_SERVICE_SPEC_TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/product-spec-service-specs")
public class ProductSpecServiceSpecController {
    private final ProductSpecServiceSpecService productSpecServiceSpecService;

    public ProductSpecServiceSpecController(ProductSpecServiceSpecService productSpecServiceSpecService) {
        this.productSpecServiceSpecService = productSpecServiceSpecService;
    }

    @Operation(summary = SwaggerText.PRODUCT_SPEC_SERVICE_SPEC_CREATE_SUMMARY)
    @PostMapping
    public ResponseEntity<CreatedProductSpecServiceSpecResponse> create(
            @Valid @RequestBody CreateProductSpecServiceSpecRequest request) {
        CreatedProductSpecServiceSpecResponse response = productSpecServiceSpecService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = SwaggerText.RELATION_UPDATE_SUMMARY)
    @PutMapping("/{productSpecServiceSpecId}")
    public ResponseEntity<UpdatedProductSpecServiceSpecResponse> update(
            @PathVariable Long productSpecServiceSpecId,
            @Valid @RequestBody UpdateProductSpecServiceSpecRequest request) {
        UpdatedProductSpecServiceSpecResponse response = productSpecServiceSpecService.update(productSpecServiceSpecId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.RELATION_GET_BY_ID_SUMMARY)
    @GetMapping("/{productSpecServiceSpecId}")
    public ResponseEntity<GetProductSpecServiceSpecResponse> getById(@PathVariable Long productSpecServiceSpecId) {
        GetProductSpecServiceSpecResponse response = productSpecServiceSpecService.getById(productSpecServiceSpecId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.RELATION_GET_ALL_SUMMARY)
    @GetMapping
    public ResponseEntity<List<GetAllProductSpecServiceSpecResponse>> getAll() {
        List<GetAllProductSpecServiceSpecResponse> response = productSpecServiceSpecService.getAll();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.RELATION_DELETE_SUMMARY)
    @DeleteMapping("/{productSpecServiceSpecId}")
    public ResponseEntity<Void> delete(@PathVariable Long productSpecServiceSpecId) {
        productSpecServiceSpecService.delete(productSpecServiceSpecId);
        return ResponseEntity.noContent().build();
    }

}
