package com.etiya.crm.partyservice.constants;

/**
 * messages/messages.properties icindeki key'lerin sabitleri. Exception'larda literal
 * metin kullanilmaz; hepsi buradaki key'ler uzerinden MessageSource'tan cozulur.
 */
public final class MessageKeys {

	public static final String PARTY_ROLE_NOT_FOUND = "error.party-role.not-found";
	public static final String INDIVIDUAL_NOT_FOUND_FOR_PARTY_ROLE = "error.individual.not-found-for-party-role";
	public static final String PARTY_NOT_FOUND = "error.party.not-found";
	public static final String LOOKUP_VALUE_NOT_FOUND = "error.lookup.value-not-found";
	public static final String DUPLICATE_NATIONAL_ID = "error.individual.duplicate-national-id";
	public static final String VALIDATION_FAILED = "error.validation.failed";
	public static final String DOWNSTREAM_CALL_FAILED = "error.downstream.call-failed";
	public static final String UNEXPECTED_ERROR = "error.unexpected";

	private MessageKeys() {
	}
}
