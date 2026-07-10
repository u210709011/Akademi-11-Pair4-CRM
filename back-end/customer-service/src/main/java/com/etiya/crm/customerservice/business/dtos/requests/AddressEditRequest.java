package com.etiya.crm.customerservice.business.dtos.requests;

import com.etiya.crm.customerservice.constants.MessageKeys;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Adres ekleme/guncelleme istegi. AddressInfo'dan (onboarding) farki:
 * primary alani burada acikca istemciden gelir - onboarding'deki "ilk adres
 * primary" kurali sadece ilk kayit anina ozeldir.
 */
public record AddressEditRequest(

		@NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		Long cityId,

		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String streetName,

		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String buildingName,

		String addressDesc,

		boolean primary) {
}
