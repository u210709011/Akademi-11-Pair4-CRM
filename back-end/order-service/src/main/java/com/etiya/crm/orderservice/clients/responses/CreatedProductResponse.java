package com.etiya.crm.orderservice.clients.responses;

public record CreatedProductResponse(

    Long productId,
    Long parentProductId,
    Long productOfferingId,
    Long productSpecId,
    String name,
    String descr,
    Long campaignId,
    Long statusId
) {

}
