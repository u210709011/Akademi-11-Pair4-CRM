package com.etiya.crm.productservice.api.controllers;

import com.etiya.crm.productservice.business.abstracts.ProductCharacteristicValueService;
import com.etiya.crm.productservice.business.dtos.requests.ProductCharacteristicValue.CreateProductCharacteristicValueRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCharacteristicValue.UpdateProductCharacteristicValueRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue.CreatedProductCharacteristicValueResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue.GetAllProductCharacteristicValueResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue.GetProductCharacteristicValueResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue.UpdatedProductCharacteristicValueResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-characteristic-values")
public class ProductCharacteristicValueController {
    private final ProductCharacteristicValueService productCharacteristicValueService;

    public ProductCharacteristicValueController(ProductCharacteristicValueService productCharacteristicValueService) {
        this.productCharacteristicValueService = productCharacteristicValueService;
    }

    @PostMapping
    public ResponseEntity<CreatedProductCharacteristicValueResponse> create(@Valid @RequestBody CreateProductCharacteristicValueRequest request) {
        CreatedProductCharacteristicValueResponse response = productCharacteristicValueService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{productCharacteristicValueId}")
    public ResponseEntity<UpdatedProductCharacteristicValueResponse> update(@PathVariable Long productCharacteristicValueId, @Valid @RequestBody UpdateProductCharacteristicValueRequest request) {
        UpdatedProductCharacteristicValueResponse response = productCharacteristicValueService.update(productCharacteristicValueId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productCharacteristicValueId}")
    public ResponseEntity<GetProductCharacteristicValueResponse> getById(@PathVariable Long productCharacteristicValueId) {
        GetProductCharacteristicValueResponse response = productCharacteristicValueService.getById(productCharacteristicValueId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<GetAllProductCharacteristicValueResponse>> getAll() {
        List<GetAllProductCharacteristicValueResponse> response = productCharacteristicValueService.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<List<GetAllProductCharacteristicValueResponse>> getByProductId(@PathVariable Long productId) {
        List<GetAllProductCharacteristicValueResponse> response = productCharacteristicValueService.getByProductId(productId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productCharacteristicValueId}")
    public ResponseEntity<Void> delete(@PathVariable Long productCharacteristicValueId) {
        productCharacteristicValueService.delete(productCharacteristicValueId);
        return ResponseEntity.noContent().build();
    }
}
