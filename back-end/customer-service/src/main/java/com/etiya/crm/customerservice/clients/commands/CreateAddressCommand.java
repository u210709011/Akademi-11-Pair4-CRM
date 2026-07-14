package com.etiya.crm.customerservice.clients.commands;

/**
 * contact-info-service POST /api/v1/addresses istek govdesi. Alan adlari
 * (ozellikle houseName) contact-info-service.CreateAddressRequest ile birebir
 * aynı olmak zorunda - JSON serialize/deserialize ismen eslesir.
 */
public record CreateAddressCommand(
		Long rowId,
		Long dataTypeId,
		Long cityId,
		String streetName,
		String houseName,
		String addrDesc,
		boolean primary) {
}
