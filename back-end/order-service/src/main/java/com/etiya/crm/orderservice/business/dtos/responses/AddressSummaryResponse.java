package com.etiya.crm.orderservice.business.dtos.responses;

// summary of response after submit
public record AddressSummaryResponse(

    Long addressId,
    Long cityId,
    String streetName,
    String buildingName,
    String addressDesc
) {

}
