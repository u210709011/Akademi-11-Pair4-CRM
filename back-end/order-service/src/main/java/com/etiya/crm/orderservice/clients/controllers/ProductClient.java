package com.etiya.crm.orderservice.clients.controllers;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.etiya.crm.orderservice.clients.responses.CampaignResponse;
import com.etiya.crm.orderservice.clients.responses.ProductOfferingResponse;

@FeignClient(name = "product-service")
public interface ProductClient {

    // sepete eklenen prodOfrId'nin gercekten var olup olmadigini dogrulamak ve
    // ofrName/fiyat snapshot'ini almak icin (bkz. createOrder/addItem).
    @GetMapping("/api/v1/product-procutOfferings/{productOfferingId}")
    ProductOfferingResponse getById(@PathVariable("productOfferingId") Long productOfferingId);

    // sepete eklenen cmpgId'nin gercekten var olup olmadigini dogrulamak ve
    // cmpgName snapshot'ini almak icin (bkz. createOrder/addItem).
    @GetMapping("/api/v1/product-campaigns/{campaignId}")
    CampaignResponse getCampaignById(@PathVariable("campaignId") Long campaignId);

}
