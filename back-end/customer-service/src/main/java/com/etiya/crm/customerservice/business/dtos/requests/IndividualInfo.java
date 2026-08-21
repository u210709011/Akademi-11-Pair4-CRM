package com.etiya.crm.customerservice.business.dtos.requests;

import java.time.LocalDate;

import com.etiya.crm.customerservice.constants.MessageKeys;
import com.etiya.crm.customerservice.constants.SwaggerText;
import com.etiya.crm.shared.contracts.gnltp.GnlTpGroups;
import com.etiya.crm.shared.contracts.validation.ExistsInLookupGroup;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** party-service'e (IND) gidecek kisi bilgisi. ACC-002/003/005/006/007. */
@Schema(description = SwaggerText.INDIVIDUAL_INFO_SCHEMA_DESCRIPTION)
public record IndividualInfo(

		@Schema(description = SwaggerText.INDIVIDUAL_INFO_FIRST_NAME_DESCRIPTION, example = "Ahmet")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Size(max = 50, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
		@Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]*$", message = "{" + MessageKeys.NAME_INVALID + "}")
		String firstName,

		@Schema(description = SwaggerText.INDIVIDUAL_INFO_MIDDLE_NAME_DESCRIPTION, example = "Can")
		@Size(max = 50, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
		@Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]*$", message = "{" + MessageKeys.NAME_INVALID + "}")
		String middleName,

		@Schema(description = SwaggerText.INDIVIDUAL_INFO_LAST_NAME_DESCRIPTION, example = "Yilmaz")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Size(max = 50, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
		@Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]*$", message = "{" + MessageKeys.NAME_INVALID + "}")
		String lastName,

		@Schema(description = SwaggerText.INDIVIDUAL_INFO_BIRTH_DATE_DESCRIPTION, example = "15/06/1990")
		@NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
		LocalDate birthDate,

		@Schema(description = SwaggerText.INDIVIDUAL_INFO_GENDER_ID_DESCRIPTION, example = "1")
		@NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@ExistsInLookupGroup(group = GnlTpGroups.GENDER, message = "{" + MessageKeys.GENDER_INVALID + "}")
		Long genderId,

		@Schema(description = SwaggerText.INDIVIDUAL_INFO_MOTHER_NAME_DESCRIPTION, example = "Ayse")
		@Size(max = 50, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
		@Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]*$", message = "{" + MessageKeys.NAME_INVALID + "}")
		String motherName,

		@Schema(description = SwaggerText.INDIVIDUAL_INFO_FATHER_NAME_DESCRIPTION, example = "Mehmet")
		@Size(max = 50, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
		@Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]*$", message = "{" + MessageKeys.NAME_INVALID + "}")
		String fatherName,

		@Schema(description = SwaggerText.INDIVIDUAL_INFO_NATIONAL_ID_DESCRIPTION,
				example = "10000000146", pattern = "^[0-9]{11}$")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Pattern(regexp = "^[0-9]{11}$", message = "{" + MessageKeys.NATIONAL_ID_INVALID + "}")
		String nationalId) {
}
