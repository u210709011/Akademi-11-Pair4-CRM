package com.etiya.crm.orderservice.business.dtos.responses;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

//summary of order after submit request( eye icon )
public record OrderItemSummaryResponse(

    Long custOrdItemId,
    Long prodId,
    Long prodOfrId,
    Long prodSpecId,
    String ofrName,
    String prodName,
    Long cmpgId,
    String cmpgName,
    BigDecimal price,
    Instant serviceStartDate,
    List<ProdCharValResponse> charVals
) {

}
