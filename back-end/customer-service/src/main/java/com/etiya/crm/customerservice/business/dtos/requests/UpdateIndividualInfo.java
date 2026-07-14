package com.etiya.crm.customerservice.business.dtos.requests;

import com.etiya.crm.customerservice.constants.MessageKeys;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Kisisel bilgi editleme istegi. IndividualInfo'nun kasitli olarak bir alt
 * kumesidir: nationalId (TC no) ve birthDate KPS ile dogrulanan kimlik
 * alanlari oldugu icin bu uc noktadan editlenemez.
 */
public record UpdateIndividualInfo(

		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String firstName,

		String middleName,

		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String lastName,

		@NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		Long genderId,

		String motherName,

		String fatherName) {
}
