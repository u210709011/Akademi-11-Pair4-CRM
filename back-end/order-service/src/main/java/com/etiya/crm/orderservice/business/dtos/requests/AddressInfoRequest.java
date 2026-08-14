package com.etiya.crm.orderservice.business.dtos.requests;

import com.etiya.crm.orderservice.constants.MessageKeys;
import com.etiya.crm.shared.contracts.gnltp.GnlTpGroups;
import com.etiya.crm.shared.contracts.validation.ExistsInLookupGroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

//for create new service address FR-015
//limits contact-info-service'teki addr tablosu (street_name/house_name/addr_desc) ile birebir
public record AddressInfoRequest(

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    @ExistsInLookupGroup(group = GnlTpGroups.CITY, message = "{" + MessageKeys.CITY_INVALID + "}") Long cityId,
    @NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    @Size(max = 200, message = "{" + MessageKeys.FIELD_TOO_LONG + "}") String streetName,
    @NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    @Size(max = 100, message = "{" + MessageKeys.FIELD_TOO_LONG + "}") String buildingName,
    @NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    @Size(max = 200, message = "{" + MessageKeys.FIELD_TOO_LONG + "}") String addressDesc
) {

}
