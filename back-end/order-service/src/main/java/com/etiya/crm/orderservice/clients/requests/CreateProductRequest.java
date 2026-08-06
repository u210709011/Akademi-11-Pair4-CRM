package com.etiya.crm.orderservice.clients.requests;

// finishOrder'da her CustOrdItem icin product-service'te gercek Product instance'i olusturmak
// icin gonderilen istek govdesi (POST /api/v1/products).
public record CreateProductRequest(

    Long parentProductId,
    Long productOfferingId,
    Long productSpecId,
    String name,
    String descr,
    Long campaignId,
    String statusCode
) {

}
