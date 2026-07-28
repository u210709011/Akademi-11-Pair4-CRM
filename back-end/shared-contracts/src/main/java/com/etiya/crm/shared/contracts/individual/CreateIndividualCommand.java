package com.etiya.crm.shared.contracts.individual;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/** party-service POST /api/v1/individuals istek govdesi. */
public record CreateIndividualCommand(

		@NotBlank
		@Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]*$", message = "isim sadece harflerden olusmalidir")
		String firstName,

		@Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]*$", message = "isim sadece harflerden olusmalidir")
		String middleName,

		@NotBlank
		@Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]*$", message = "isim sadece harflerden olusmalidir")
		String lastName,

		@NotNull
		LocalDate birthDate,

		@NotNull
		Long genderId,

		@Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]*$", message = "isim sadece harflerden olusmalidir")
		String motherName,

		@Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]*$", message = "isim sadece harflerden olusmalidir")
		String fatherName,

		@NotBlank
		@Pattern(regexp = "\\d{11}", message = "nationalId 11 haneli rakamlardan olusmalidir")
		String nationalId) {
}
