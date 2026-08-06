package com.etiya.crm.orderservice.business.dtos.requests;

import java.util.List;

import com.etiya.crm.orderservice.constants.MessageKeys;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


//single item in the cart, which offer belong to which campaign and characteristics of that item
public record BasketItemRequest(

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}") Long prodOfrId,
    Long cmpgId,
    @Valid List<ProdCharValRequest> charVals
) {

}
