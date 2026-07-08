package com.etiya.crm.customerservice.clients;

import java.time.LocalDate;

/** party-service POST /api/individuals istek govdesi. */
public record CreateIndividualCommand(
		String firstName,
		String middleName,
		String lastName,
		LocalDate birthDate,
		Long genderId,
		String motherName,
		String fatherName,
		String nationalId) {
}
