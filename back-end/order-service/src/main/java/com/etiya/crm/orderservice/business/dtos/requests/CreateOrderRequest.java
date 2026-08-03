package com.etiya.crm.orderservice.business.dtos.requests;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

// Offer Selection'da "Next" tiklandiginda (validate-basket basarili olduktan sonra) cagrilir.
// CustOrd/BsnInter'i WAIT durumunda olusturur - charVals/adres henuz yok, onlar Configuration
// adiminda saveConfiguration ile eklenir.
public record CreateOrderRequest(

    @NotNull(message = "custId is required") Long custId,
    @NotNull(message = "custAcctId is required") Long custAcctId,
    @NotEmpty(message = "items must contain at least one item") @Valid List<BasketItemRequest> items
) {

}
