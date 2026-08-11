package com.etiya.crm.orderservice.clients.controllers;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.etiya.crm.orderservice.clients.requests.CreateProductCharacteristicValueRequest;
import com.etiya.crm.orderservice.clients.requests.CreateProductRequest;
import com.etiya.crm.orderservice.clients.responses.CampaignOfferingResponse;
import com.etiya.crm.orderservice.clients.responses.CampaignResponse;
import com.etiya.crm.orderservice.clients.responses.CreatedProductResponse;
import com.etiya.crm.orderservice.clients.responses.ProductCatalogOfferingResponse;
import com.etiya.crm.orderservice.clients.responses.ProductOfferingRelationResponse;
import com.etiya.crm.orderservice.clients.responses.ProductOfferingResponse;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductClient {

    // sepete eklenen prodOfrId'nin gercekten var olup olmadigini dogrulamak ve
    // ofrName/fiyat snapshot'ini almak icin (bkz. createOrder/addItem).
    @GetMapping("/api/v1/product-offerings/{productOfferingId}")
    ProductOfferingResponse getById(@PathVariable("productOfferingId") Long productOfferingId);

    // sepete eklenen cmpgId'nin gercekten var olup olmadigini dogrulamak ve
    // cmpgName snapshot'ini almak icin (bkz. createOrder/addItem).
    @GetMapping("/api/v1/product-campaigns/{campaignId}")
    CampaignResponse getCampaignById(@PathVariable("campaignId") Long campaignId);

    // FR-021: finishOrder'da her CustOrdItem icin gercek Product instance'i (subscription
    // provisioning) olusturmak icin.
    @PostMapping("/api/v1/products")
    CreatedProductResponse createProduct(@RequestBody CreateProductRequest request);

    // FR-014 ACC-012/BR-04: sepetteki tekliflerin birbiriyle cakisip cakismadigini (EXCL)
    // kontrol etmek icin - product-service'te filtreli bir endpoint olmadigindan tum
    // iliskiler bir kerede cekilip BasketValidationRules'ta filtrelenir.
    @GetMapping("/api/v1/product-offering-relations")
    List<ProductOfferingRelationResponse> getOfferingRelations();

    // BR-05 (genisletilmis): bir teklifin katalog kategorisini (Internet/Mobile/TV) cozmek icin -
    // hesapta o kategoriden farkli bir teklif zaten aktifse yenisi eklenemez.
    @GetMapping("/api/v1/product-catalog-offerings")
    List<ProductCatalogOfferingResponse> getCatalogOfferings();

    // cmpgId secilen bir item'in fiyatini indirimli hesaplamak icin: campaign'in hangi
    // offering'lere uygulandigini ve indirimli fiyatini (discountedPrice) doner.
    @GetMapping("/api/v1/campaign-offerings/by-campaign/{campaignId}")
    List<CampaignOfferingResponse> getCampaignOfferingsByCampaignId(@PathVariable("campaignId") Long campaignId);

    // FR-021: finishOrder'da provizyon edilen Product'a, Configuration'da secilen
    // karakteristikleri (CustOrdCharVal) islemek icin.
    @PostMapping("/api/v1/product-characteristic-values")
    void createProductCharacteristicValue(@RequestBody CreateProductCharacteristicValueRequest request);

}
