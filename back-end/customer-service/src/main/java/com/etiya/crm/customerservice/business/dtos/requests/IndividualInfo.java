package com.etiya.crm.customerservice.business.dtos.requests;

import java.time.LocalDate;

import com.etiya.crm.customerservice.constants.MessageKeys;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/** party-service'e (IND) gidecek kisi bilgisi. ACC-002/003/005/006/007. */
@Schema(description = "Onboarding sirasinda girilen kisi (individual) bilgisi.")
public record IndividualInfo(

		@Schema(description = "Ad", example = "Ahmet")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String firstName,

		@Schema(description = "Ikinci ad (opsiyonel)", example = "Can")
		String middleName,

		@Schema(description = "Soyad", example = "Yilmaz")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		String lastName,

		@Schema(description = "Dogum tarihi, dd/MM/yyyy formatinda. 01/01/1900 oncesi ya da bugunden sonrasi gecersiz.",
				example = "15/06/1990", pattern = "dd/MM/yyyy")
		@NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
		LocalDate birthDate,

		@Schema(description = "lookup-service GENDER grubundaki deger id'si (1=MALE, 2=FEMALE).", example = "1")
		@NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		Long genderId,

		@Schema(description = "Anne adi (opsiyonel)", example = "Ayse")
		String motherName,

		@Schema(description = "Baba adi (opsiyonel)", example = "Mehmet")
		String fatherName,

		@Schema(description = "T.C. Kimlik No - 11 haneli rakam. Sistemde tekil olmali (ayni nationalId ile ikinci kayit 409 doner).",
				example = "10000000146", pattern = "^[0-9]{11}$")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Pattern(regexp = "^[0-9]{11}$", message = "{" + MessageKeys.NATIONAL_ID_INVALID + "}")
		String nationalId) {
}
