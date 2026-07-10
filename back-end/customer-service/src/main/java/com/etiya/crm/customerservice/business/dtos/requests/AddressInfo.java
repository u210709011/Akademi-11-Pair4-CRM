package com.etiya.crm.customerservice.business.dtos.requests;

import com.etiya.crm.customerservice.constants.MessageKeys;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * contact-address-service'e (ADDR) gidecek adres bilgisi. ACC-015.
 * "primary" alani UI'da yok; ilk eklenen adres server-side primary sayilir
 * (bkz. CustomerBusinessRules).
 */
public record AddressInfo(

		@NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		Long cityId,

		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String streetName,

		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String buildingName, // House/Flat Number bu alana yazilir (ADDR.BLDG_NAME)

		String addressDesc) {
}
