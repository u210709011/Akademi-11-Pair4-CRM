package com.etiya.crm.customerservice.business.dtos.requests;

import com.etiya.crm.customerservice.constants.MessageKeys;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Kisisel bilgi editleme istegi. IndividualInfo'nun kasitli olarak bir alt
 * kumesidir: nationalId (TC no) ve birthDate KPS ile dogrulanan kimlik
 * alanlari oldugu icin bu uc noktadan editlenemez.
 */
@Schema(description = "PUT /api/v1/customers/{custId}/individual istek govdesi. "
		+ "nationalId ve birthDate KASITLI OLARAK yok - KPS ile dogrulanan kimlik alanlari bu ekrandan editlenemez.")
public record UpdateIndividualInfo(

		@Schema(description = "Ad", example = "Ahmet")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String firstName,

		@Schema(description = "Ikinci ad (opsiyonel)", example = "Can")
		String middleName,

		@Schema(description = "Soyad", example = "Yilmazoglu")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String lastName,

		@Schema(description = "lookup-service GENDER grubundaki deger id'si (1=MALE, 2=FEMALE).", example = "1")
		@NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		Long genderId,

		@Schema(description = "Anne adi (opsiyonel)", example = "Ayse")
		String motherName,

		@Schema(description = "Baba adi (opsiyonel)", example = "Mehmet")
		String fatherName) {
}
