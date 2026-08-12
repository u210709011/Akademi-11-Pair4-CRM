package com.etiya.crm.productservice.api.controllers;

import com.etiya.crm.productservice.business.abstracts.ProductOfferingService;
import com.etiya.crm.productservice.business.dtos.requests.ProductOffering.CreateProductOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductOffering.UpdateProductOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.CreatedProductOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.GetAllProductOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.GetProductOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.UpdatedProductOfferingResponse;
import com.etiya.crm.productservice.constants.SwaggerText;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = SwaggerText.PRODUCT_OFFERING_TAG_NAME, description = SwaggerText.PRODUCT_OFFERING_TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/product-offerings")
public class ProductOfferingController {

    private final ProductOfferingService productOfferingService;

    public ProductOfferingController(ProductOfferingService productOfferingService) {
        this.productOfferingService = productOfferingService;
    }

    @Operation(summary = SwaggerText.PRODUCT_OFFERING_CREATE_SUMMARY)
    @PostMapping
    public ResponseEntity<CreatedProductOfferingResponse> create(@Valid @RequestBody CreateProductOfferingRequest request){
        CreatedProductOfferingResponse response = productOfferingService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = SwaggerText.PRODUCT_OFFERING_UPDATE_SUMMARY)
    @PutMapping("/{productOfferingId}")
    public ResponseEntity<UpdatedProductOfferingResponse> update(@PathVariable Long productOfferingId, @Valid @RequestBody UpdateProductOfferingRequest request){
        UpdatedProductOfferingResponse response = productOfferingService.update(productOfferingId, request);
        return ResponseEntity.ok(response); // -> ResponseEntity.status(HttpStatus.OK).body(response) yazmanın aynısı
    }

    @Operation(summary = SwaggerText.PRODUCT_OFFERING_GET_BY_ID_SUMMARY)
    @GetMapping("/{productOfferingId}")
    public ResponseEntity<GetProductOfferingResponse> getById(@PathVariable Long productOfferingId){
        GetProductOfferingResponse response = productOfferingService.getById(productOfferingId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.PRODUCT_OFFERING_GET_ALL_SUMMARY)
    @GetMapping
    public ResponseEntity<List<GetAllProductOfferingResponse>> getAll(){
        List<GetAllProductOfferingResponse> response = productOfferingService.getAll();
        return ResponseEntity.ok(response);
    }



    @Operation(summary = SwaggerText.PRODUCT_OFFERING_DELETE_SUMMARY)
    @DeleteMapping("/{productOfferingId}")
    public ResponseEntity<Void> delete(@PathVariable Long productOfferingId){
        productOfferingService.delete(productOfferingId);
        return ResponseEntity.noContent().build(); // işlem başarılı ama dönecek veri yok
    }
}
