package com.etiya.crm.customerservice.clients.commands;

/** contact-address-service ADDR satiri. primary, ilk adres icin true gelir. */
public record AddressCommand(
		Long cityId,
		String streetName,
		String buildingName,
		String addressDesc,
		boolean primary) {
}
