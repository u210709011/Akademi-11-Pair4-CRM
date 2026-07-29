package com.etiya.crm.lookupservice.constants;

/**
 * messages/messages.properties icindeki key'lerin sabitleri. Exception'larda literal
 * metin kullanilmaz; hepsi buradaki key'ler uzerinden MessageSource'tan cozulur.
 */
public final class MessageKeys {

	public static final String ENTITY_NOT_FOUND = "error.entity.not-found";
	public static final String GNL_CHAR_VAL_INVALID_DATE_RANGE = "error.gnl-char-val.invalid-date-range";
	public static final String DATA_INTEGRITY_VIOLATION = "error.data-integrity-violation";
	public static final String VALIDATION_FAILED = "error.validation.failed";

	private MessageKeys() {
	}
}
