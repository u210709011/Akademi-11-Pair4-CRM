package com.etiya.crm.orderservice.business.dtos.requests;

import jakarta.validation.constraints.NotNull;

// fr-015, it will be forwarded to the ProductClient when prod is created
public record ProdCharValRequest(

    @NotNull Long charId,
    Long charValId,
    String val

) {

}
