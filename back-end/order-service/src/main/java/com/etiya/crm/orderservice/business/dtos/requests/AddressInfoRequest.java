package com.etiya.crm.orderservice.business.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

//for create new service address FR-015
//limits contact-info-service'teki addr tablosu (street_name/house_name/addr_desc) ile birebir
public record AddressInfoRequest(

    @NotNull(message = "cityId is required") Long cityId,
    @NotBlank(message = "streetName is required") @Size(max = 200, message = "streetName must be at most 200 characters") String streetName,
    @NotBlank(message = "buildingName is required") @Size(max = 100, message = "buildingName must be at most 100 characters") String buildingName,
    @NotBlank(message = "addressDesc is required") @Size(max = 200, message = "addressDesc must be at most 200 characters") String addressDesc
) {

}
