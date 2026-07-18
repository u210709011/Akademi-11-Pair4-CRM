package com.etiya.crm.shared.contracts.individual;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * party-service PUT /api/v1/individuals/by-party-role/{partyRoleId} istek
 * govdesi. nationalId ve birthDate KASITLI OLARAK yok - KPS ile dogrulanan
 * kimlik alanlari customer-service tarafindan editlenebilir kabul edilmiyor.
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

		String fatherName) {
}
