package com.etiya.crm.orderservice.business.dtos.responses;

import java.math.BigDecimal;
import java.time.Instant;

// musteri sipariş gecmisi (order list) ekranindaki tek bir satir - item bazli degil, siparis bazlidir
public record OrderListItemResponse(

    Long custOrdId,
    Long ordStId,
    int itemCount,
    BigDecimal totalAmount,
    Instant cdate
) {

}
