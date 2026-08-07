package com.etiya.crm.orderservice.business.dtos.requests;

import java.util.List;

import com.etiya.crm.orderservice.constants.MessageKeys;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

// Offer Selection'da "Next" tiklandiginda (validate-basket basarili olduktan sonra) cagrilir.
// CustOrd/BsnInter'i WAIT durumunda olusturur - charVals/adres henuz yok, onlar Configuration
// adiminda saveConfiguration ile eklenir.
public record CreateOrderRequest(

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}") Long custId,
    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}") Long custAcctId,
    @NotEmpty(message = "{" + MessageKeys.LIST_EMPTY + "}") @Valid List<BasketItemRequest> items
) {

}
