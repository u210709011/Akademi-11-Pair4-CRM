package com.etiya.crm.orderservice.business.dtos.responses;

import java.math.BigDecimal;
import java.util.List;

// main submit order response, fr-016 order detail page
public record OrderSummaryResponse(

    Long custOrdId,
    Long ordStId,
    List<OrderItemSummaryResponse> items,
    List<ProdCharValResponse> charVals,
    AddressSummaryResponse serviceAddress,
    BigDecimal totalAmount
) {

}
