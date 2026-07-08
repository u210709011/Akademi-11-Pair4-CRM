package com.etiya.crm.customerservice.business.dtos.requests;

import java.time.LocalDate;

import com.etiya.crm.customerservice.constants.MessageKeys;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/** party-service'e (IND) gidecek kisi bilgisi. ACC-002/003/005/006/007. */
public record IndividualInfo(

		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String firstName,

		String middleName,

		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String lastName,

		@NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
		LocalDate birthDate,

		@NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		Long genderId,

		String motherName,

		String fatherName,

		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Pattern(regexp = "^[0-9]{11}$", message = "{" + MessageKeys.NATIONAL_ID_INVALID + "}")
		String nationalId) {
}
