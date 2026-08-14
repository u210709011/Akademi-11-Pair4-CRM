package com.etiya.crm.productservice.api.controllers;

import com.etiya.crm.productservice.business.abstracts.ProductCatalogOfferingService;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalogOffering.CreateProductCatalogOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalogOffering.UpdateProductCatalogOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.CreatedProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.GetAllProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.GetProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.UpdatedProductCatalogOfferingResponse;
import com.etiya.crm.productservice.constants.SwaggerText;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = SwaggerText.PRODUCT_CATALOG_OFFERING_TAG_NAME, description = SwaggerText.PRODUCT_CATALOG_OFFERING_TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/product-catalog-offerings")
public class ProductCatalogOfferingController {
    private final ProductCatalogOfferingService productCatalogOfferingService;

    public ProductCatalogOfferingController(ProductCatalogOfferingService productCatalogOfferingService) {
        this.productCatalogOfferingService = productCatalogOfferingService;
    }

    @Operation(summary = SwaggerText.PRODUCT_CATALOG_OFFERING_CREATE_SUMMARY)
    @PostMapping
    public ResponseEntity<CreatedProductCatalogOfferingResponse> create(@Valid @RequestBody CreateProductCatalogOfferingRequest request) {
        CreatedProductCatalogOfferingResponse response = productCatalogOfferingService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = SwaggerText.PRODUCT_CATALOG_OFFERING_UPDATE_SUMMARY)
    @PutMapping("/{productCatalogOfferingId}")
    public ResponseEntity<UpdatedProductCatalogOfferingResponse> update(@PathVariable Long productCatalogOfferingId, @Valid @RequestBody UpdateProductCatalogOfferingRequest request) {
        UpdatedProductCatalogOfferingResponse response = productCatalogOfferingService.update(productCatalogOfferingId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.PRODUCT_CATALOG_OFFERING_GET_BY_ID_SUMMARY)
    @GetMapping("/{productCatalogOfferingId}")
    public ResponseEntity<GetProductCatalogOfferingResponse> getById(@PathVariable Long productCatalogOfferingId) {
        GetProductCatalogOfferingResponse response = productCatalogOfferingService.getById(productCatalogOfferingId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.PRODUCT_CATALOG_OFFERING_GET_ALL_SUMMARY)
    @GetMapping
    public ResponseEntity<List<GetAllProductCatalogOfferingResponse>> getAll() {
        List<GetAllProductCatalogOfferingResponse> response = productCatalogOfferingService.getAll();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.PRODUCT_CATALOG_OFFERING_GET_BY_CATALOG_SUMMARY)
    @GetMapping("/by-catalog/{productCatalogId}")
    public ResponseEntity<List<GetAllProductCatalogOfferingResponse>> getByCatalogId(@PathVariable Long productCatalogId) {
        List<GetAllProductCatalogOfferingResponse> response = productCatalogOfferingService.getByCatalogId(productCatalogId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.PRODUCT_CATALOG_OFFERING_DELETE_SUMMARY)
    @DeleteMapping("/{productCatalogOfferingId}")
    public ResponseEntity<Void> delete(@PathVariable Long productCatalogOfferingId) {
        productCatalogOfferingService.delete(productCatalogOfferingId);
        return ResponseEntity.noContent().build();
    }
}
