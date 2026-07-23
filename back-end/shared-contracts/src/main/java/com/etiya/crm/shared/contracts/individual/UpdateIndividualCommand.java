package com.etiya.crm.shared.contracts.individual;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * party-service PUT /api/v1/individuals/by-party-role/{partyRoleId} istek
 * govdesi. nationalId ve birthDate FR-004 geregi artik editlenebilir:
 * party-service guncelleme sirasinda nationalId'nin baska bir bireyle
 * cakismadigini kontrol eder (bkz. IndividualBusinessRules).
 */
public record UpdateIndividualCommand(

		@NotBlank
		String firstName,

		String middleName,

		@NotBlank
		String lastName,

		@NotNull
		Long genderId,

		String motherName,

		String fatherName,

		@NotNull
		LocalDate birthDate,

		@NotBlank
		@Pattern(regexp = "^[0-9]{11}$")
		String nationalId) {
}
