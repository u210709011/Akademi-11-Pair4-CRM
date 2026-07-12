package com.etiya.crm.customerservice.clients.responses;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * party-service GET/PUT /api/v1/individuals/by-party-role/{partyRoleId}
 * yanit govdesi. birthDate/nationalId salt-okunur alanlardir (edit formunda
 * gosterilir ama PUT govdesinde gonderilmez) - bkz. UpdateIndividualCommand.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IndividualResponse(
		String firstName,
		String middleName,
		String lastName,
		LocalDate birthDate,
		Long genderId,
		String motherName,
		String fatherName,
		String nationalId) {
}
