package com.etiya.crm.orderservice.business.dtos.requests;

import java.util.List;

import com.etiya.crm.orderservice.constants.MessageKeys;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

// FR-017: Offer Selection ekraninda "Next" tiklandiginda, Product Configuration'a
// gecmeden once sepetin dogrulanmasi icin kullanilir. Adres/charVals henuz bu adimda
// toplanmadigindan (onlar Product Configuration'da girilir) CreateOrderRequest'ten
// farkli, daha dar bir kontrat.
public record ValidateBasketRequest(

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}") Long custId,
    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}") Long custAcctId,
    @NotEmpty(message = "{" + MessageKeys.LIST_EMPTY + "}") @Valid List<BasketItemRequest> items
) {

}
