package com.etiya.crm.orderservice.business.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// fr-015, it will be forwarded to the ProductClient when prod is created
public record ProdCharValRequest(

    @NotNull(message = "charId is required") Long charId,
    Long charValId,
    @Size(max = 255, message = "val must be at most 255 characters") String val

) {

}
