package com.etiya.crm.shared.contracts.individual;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import com.etiya.crm.shared.contracts.constants.ValidationMessageKeys;

/** party-service POST /api/v1/individuals istek govdesi. */
public record CreateIndividualCommand(

		@NotBlank
		@Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]*$", message = "{" + ValidationMessageKeys.NAME_INVALID + "}")
		String firstName,

		@Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]*$", message = "{" + ValidationMessageKeys.NAME_INVALID + "}")
		String middleName,

		@NotBlank
		@Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]*$", message = "{" + ValidationMessageKeys.NAME_INVALID + "}")
		String lastName,

		@NotNull
		LocalDate birthDate,

		@NotNull
		Long genderId,

		@Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]*$", message = "{" + ValidationMessageKeys.NAME_INVALID + "}")
		String motherName,

		@Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]*$", message = "{" + ValidationMessageKeys.NAME_INVALID + "}")
		String fatherName,

		@NotBlank
		@Pattern(regexp = "\\d{11}", message = "{" + ValidationMessageKeys.NATIONAL_ID_INVALID + "}")
		String nationalId) {
}
