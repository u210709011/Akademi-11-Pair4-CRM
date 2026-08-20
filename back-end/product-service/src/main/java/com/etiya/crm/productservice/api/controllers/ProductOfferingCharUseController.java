package com.etiya.crm.productservice.api.controllers;

import com.etiya.crm.productservice.business.abstracts.ProductOfferingCharUseService;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingCharUse.CreateProductOfferingCharUseRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingCharUse.UpdateProductOfferingCharUseRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingCharUse.*;
import com.etiya.crm.productservice.constants.SwaggerText;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = SwaggerText.PRODUCT_OFFERING_CHAR_USE_TAG_NAME, description = SwaggerText.PRODUCT_OFFERING_CHAR_USE_TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/product-offering-char-uses")
public class ProductOfferingCharUseController {

    private final ProductOfferingCharUseService productOfferingCharUseService;

    public ProductOfferingCharUseController(ProductOfferingCharUseService productOfferingCharUseService) {
        this.productOfferingCharUseService = productOfferingCharUseService;
    }

    @Operation(summary = SwaggerText.PRODUCT_OFFERING_CHAR_USE_CREATE_SUMMARY)
    @PostMapping
    public ResponseEntity<CreatedProductOfferingCharUseResponse> create(@Valid @RequestBody CreateProductOfferingCharUseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productOfferingCharUseService.create(request));
    }

    @Operation(summary = SwaggerText.PRODUCT_OFFERING_CHAR_USE_UPDATE_SUMMARY)
    @PutMapping("/{id}")
    public ResponseEntity<UpdatedProductOfferingCharUseResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateProductOfferingCharUseRequest request) {
        return ResponseEntity.ok(productOfferingCharUseService.update(id, request));
    }

    @Operation(summary = SwaggerText.PRODUCT_OFFERING_CHAR_USE_GET_BY_ID_SUMMARY)
    @GetMapping("/{id}")
    public ResponseEntity<GetProductOfferingCharUseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productOfferingCharUseService.getById(id));
    }

    @Operation(summary = SwaggerText.PRODUCT_OFFERING_CHAR_USE_GET_ALL_SUMMARY)
    @GetMapping
    public ResponseEntity<List<GetAllProductOfferingCharUseResponse>> getAll() {
        return ResponseEntity.ok(productOfferingCharUseService.getAll());
    }

    @Operation(summary = SwaggerText.PRODUCT_OFFERING_CHAR_USE_GET_BY_OFFERING_SUMMARY)
    @GetMapping("/by-offering/{productOfferingId}")
    public ResponseEntity<List<GetAllProductOfferingCharUseResponse>> getByProductOfferingId(@PathVariable Long productOfferingId) {
        return ResponseEntity.ok(productOfferingCharUseService.getByProductOfferingId(productOfferingId));
    }

    @Operation(summary = SwaggerText.PRODUCT_OFFERING_CHAR_USE_DELETE_SUMMARY)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productOfferingCharUseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}