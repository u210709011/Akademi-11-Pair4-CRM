package com.etiya.crm.orderservice.business.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

//for create new service address FR-015
public record AddressInfoRequest(

    @NotNull Long cityId,
    @NotBlank String streetName,
    @NotBlank String buildingName,
    @NotBlank String addressDesc
) {

}
