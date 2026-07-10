package com.etiya.crm.customerservice.clients;

/**
 * party-service PUT /api/v1/individuals/by-party-role/{partyRoleId} istek
 * govdesi. nationalId ve birthDate KASITLI OLARAK yok - kimlik dogrulamali
 * (KPS) alanlar customer-service tarafinda editlenebilir kabul edilmiyor,
 * bkz. CUSTOMER_EDIT_INTEGRATION.md SS1.
 */
public record UpdateIndividualCommand(
		String firstName,
		String middleName,
		String lastName,
		Long genderId,
		String motherName,
		String fatherName) {
}
