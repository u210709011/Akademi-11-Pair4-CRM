package com.etiya.crm.orderservice.business.dtos.requests;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


//single item in the cart, which offer belong to which campaign and characteristics of that item
public record BasketItemRequest(

    @NotNull Long prodOfrId,
    Long cmpgId,
    @Valid List<ProdCharValRequest> charVals
) {

}
