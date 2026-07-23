package com.etiya.crm.orderservice.business.dtos.requests;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

//frontend uses this request in the submit button
//addressId or newAddress must be full, it wil be check later in BasketValidationRules
public record SubmitOrderRequest(

    @NotNull(message = "custId is required") Long custId,
    @NotNull(message = "custAcctId is required") Long custAcctId,
    @NotEmpty(message = "items must contain at least one item") @Valid List<BasketItemRequest> items,
    Long addressId,
    @Valid AddressInfoRequest newAddress
) {

    


}
