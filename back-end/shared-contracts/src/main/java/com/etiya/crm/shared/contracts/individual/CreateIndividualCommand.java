package com.etiya.crm.shared.contracts.individual;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/** party-service POST /api/v1/individuals istek govdesi. */
public record CreateIndividualCommand(

		@NotBlank
		String firstName,

		String middleName,

		@NotBlank
		String lastName,

		@NotNull
		LocalDate birthDate,

		@NotNull
		Long genderId,

		String motherName,

		String fatherName,

		@NotBlank
		@Pattern(regexp = "\\d{11}", message = "nationalId 11 haneli rakamlardan olusmalidir")
		String nationalId) {
}
