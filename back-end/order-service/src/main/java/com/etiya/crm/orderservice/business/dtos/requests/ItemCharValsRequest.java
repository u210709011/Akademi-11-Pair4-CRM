package com.etiya.crm.orderservice.business.dtos.requests;

import java.util.List;

import com.etiya.crm.orderservice.constants.MessageKeys;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

// Configuration ekraninda basket item basina bir kart var, her biri kendi karakteristik setini tasir.
public record ItemCharValsRequest(

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}") Long custOrdItemId,
    @Valid List<ProdCharValRequest> charVals
) {

}
