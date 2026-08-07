package com.etiya.crm.orderservice.clients.responses;

import java.math.BigDecimal;

public record ProductOfferingResponse(

    Long productOfferingId,
    Long productSpecId,
    String name,
    String descr,
    Long parentOfferingId,
    Long statusId,
    BigDecimal totalPrice
) {

}
