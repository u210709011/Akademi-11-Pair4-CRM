package com.etiya.crm.productservice.api.controllers;

import com.etiya.crm.productservice.business.abstracts.ProductSpecService;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpec.CreateProductSpecRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpec.UpdateProductSpecRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.CreatedProductSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.GetAllProductSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.GetProductSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.UpdatedProductSpecResponse;
import com.etiya.crm.productservice.entities.concretes.ProductSpec;
import com.etiya.crm.productservice.constants.SwaggerText;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = SwaggerText.PRODUCT_SPEC_TAG_NAME, description = SwaggerText.PRODUCT_SPEC_TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/product-specs")
public class ProductSpecController {

    private final ProductSpecService productSpecService;

    public ProductSpecController(ProductSpecService productSpecService) {
        this.productSpecService = productSpecService;
    }

    @Operation(summary = SwaggerText.PRODUCT_SPEC_CREATE_SUMMARY)
    @PostMapping
    public ResponseEntity<CreatedProductSpecResponse> create(@Valid @RequestBody CreateProductSpecRequest request){
        System.out.println("GELEN DESCR: " + request.getDescr());
        CreatedProductSpecResponse response = productSpecService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = SwaggerText.PRODUCT_SPEC_UPDATE_SUMMARY)
    @PutMapping("/{productSpecId}")
    public ResponseEntity<UpdatedProductSpecResponse> update(@PathVariable Long productSpecId, @Valid @RequestBody UpdateProductSpecRequest request){
        UpdatedProductSpecResponse response = productSpecService.update(productSpecId, request);
        return ResponseEntity.ok(response); // -> ResponseEntity.status(HttpStatus.OK).body(response) yazmanın aynısı
    }

    @Operation(summary = SwaggerText.PRODUCT_SPEC_GET_BY_ID_SUMMARY)
    @GetMapping("/{productSpecId}")
    public ResponseEntity<GetProductSpecResponse> getById(@PathVariable Long productSpecId){
        GetProductSpecResponse response = productSpecService.getById(productSpecId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.PRODUCT_SPEC_GET_ALL_SUMMARY)
    @GetMapping
    public ResponseEntity<List<GetAllProductSpecResponse>> getAll(){
        List<GetAllProductSpecResponse> response = productSpecService.getAll();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.PRODUCT_SPEC_DELETE_SUMMARY)
    @DeleteMapping("/{productSpecId}")
    public ResponseEntity<Void> delete(@PathVariable Long productSpecId){
        productSpecService.delete(productSpecId);
        return ResponseEntity.noContent().build(); // işlem başarılı ama dönecek veri yok
    }
}
