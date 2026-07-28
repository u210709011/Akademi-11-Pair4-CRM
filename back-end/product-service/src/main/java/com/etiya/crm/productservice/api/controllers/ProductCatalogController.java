package com.etiya.crm.productservice.api.controllers;

import com.etiya.crm.productservice.business.abstracts.ProductCatalogService;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalog.CreateProductCatalogRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalog.UpdateProductCatalogRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.CreatedProductCatalogResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.GetAllProductCatalogResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.GetProductCatalogResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.UpdatedProductCatalogResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-catalogs")
public class ProductCatalogController {

    private final ProductCatalogService productCatalogService;

    public ProductCatalogController(ProductCatalogService productCatalogService) {
        this.productCatalogService = productCatalogService;
    }

    @PostMapping
    public ResponseEntity<CreatedProductCatalogResponse> create(@Valid @RequestBody CreateProductCatalogRequest request){
        CreatedProductCatalogResponse response = productCatalogService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{productCatalogId}")
    public ResponseEntity<UpdatedProductCatalogResponse> update(@PathVariable Long productCatalogId, @Valid @RequestBody UpdateProductCatalogRequest request){
        UpdatedProductCatalogResponse response = productCatalogService.update(productCatalogId, request);
        return ResponseEntity.ok(response); // -> ResponseEntity.status(HttpStatus.OK).body(response) yazmanın aynısı
    }

    @GetMapping("/{productCatalogId}")
    public ResponseEntity<GetProductCatalogResponse> getById(@PathVariable Long productCatalogId){
        GetProductCatalogResponse response = productCatalogService.getById(productCatalogId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<GetAllProductCatalogResponse>> getAll(){
        List<GetAllProductCatalogResponse> response = productCatalogService.getAll();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productCatalogId}")
    public ResponseEntity<Void> delete(@PathVariable Long productCatalogId){
        productCatalogService.delete(productCatalogId);
        return ResponseEntity.noContent().build(); // işlem başarılı ama dönecek veri yok
    }
}
