package com.etiya.crm.orderservice.business.dtos.requests;

import com.etiya.crm.orderservice.constants.MessageKeys;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// fr-015, it will be forwarded to the ProductClient when prod is created
public record ProdCharValRequest(

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}") Long charId,
    Long charValId,
    @Size(max = 255, message = "{" + MessageKeys.FIELD_TOO_LONG + "}") String val

) {

}
