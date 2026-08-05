package com.etiya.crm.orderservice.business.dtos.responses;

import java.math.BigDecimal;
import java.util.List;

//summary of order after submit request
public record OrderItemSummaryResponse(

    Long custOrdItemId,
    Long prodId,
    Long prodOfrId,
    String ofrName,
    String prodName,
    Long cmpgId,
    String cmpgName,
    BigDecimal price,
    List<ProdCharValResponse> charVals
) {

}
