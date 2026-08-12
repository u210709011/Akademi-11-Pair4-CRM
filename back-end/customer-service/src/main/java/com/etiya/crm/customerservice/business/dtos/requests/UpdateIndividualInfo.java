package com.etiya.crm.customerservice.business.dtos.requests;

import java.time.LocalDate;

import com.etiya.crm.customerservice.business.validation.ExistsInLookupGroup;
import com.etiya.crm.customerservice.constants.MessageKeys;
import com.etiya.crm.shared.contracts.gnltp.GnlTpGroups;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Kisisel bilgi editleme istegi. FR-004 geregi Nationality ID ve Birth Date
 * de bu uc noktadan editlenebilir; Save'de nationalId'nin baska bir
 * musteriyle cakismadigi party-service tarafinda tekrar kontrol edilir.
 */
@Schema(description = "PUT /api/v1/customers/{custId}/individual istek govdesi.")
public record UpdateIndividualInfo(

		@Schema(description = "Ad", example = "Ahmet")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Size(max = 50, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
		@Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]*$", message = "{" + MessageKeys.NAME_INVALID + "}")
		String firstName,

		@Schema(description = "Ikinci ad (opsiyonel)", example = "Can")
		@Size(max = 50, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
		@Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]*$", message = "{" + MessageKeys.NAME_INVALID + "}")
		String middleName,

		@Schema(description = "Soyad", example = "Yilmazoglu")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Size(max = 50, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
		@Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]*$", message = "{" + MessageKeys.NAME_INVALID + "}")
		String lastName,

		@Schema(description = "lookup-service GENDER grubundaki deger id'si (1=MALE, 2=FEMALE).", example = "1")
		@NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@ExistsInLookupGroup(group = GnlTpGroups.GENDER, message = "{" + MessageKeys.GENDER_INVALID + "}")
		Long genderId,

		@Schema(description = "Anne adi (opsiyonel)", example = "Ayse")
		@Size(max = 50, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
		@Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]*$", message = "{" + MessageKeys.NAME_INVALID + "}")
		String motherName,

		@Schema(description = "Baba adi (opsiyonel)", example = "Mehmet")
		@Size(max = 50, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
		@Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]*$", message = "{" + MessageKeys.NAME_INVALID + "}")
		String fatherName,

		@Schema(description = "Dogum tarihi, dd/MM/yyyy formatinda. 01/01/1900 oncesi ya da bugunden sonrasi gecersiz.",
				example = "15/06/1990")
		@NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
		LocalDate birthDate,

		@Schema(description = "T.C. Kimlik No - 11 haneli rakam. Sistemde tekil olmali (baska musteride varsa 409 doner).",
				example = "10000000146", pattern = "^[0-9]{11}$")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Pattern(regexp = "^[0-9]{11}$", message = "{" + MessageKeys.NATIONAL_ID_INVALID + "}")
		String nationalId) {
}
