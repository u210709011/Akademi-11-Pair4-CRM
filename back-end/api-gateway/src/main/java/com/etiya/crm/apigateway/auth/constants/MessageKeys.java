package com.etiya.crm.apigateway.auth.constants;

/**
 * messages/messages.properties icindeki key'lerin sabitleri. Exception'larda literal
 * metin kullanilmaz; hepsi buradaki key'ler uzerinden MessageSource'tan cozulur.
 */
public final class MessageKeys {

	public static final String INVALID_CREDENTIALS = "error.auth.invalid-credentials";
	public static final String LOGOUT_FAILED = "error.auth.logout-failed";
	public static final String ACCOUNT_LOCKED = "error.auth.account-locked";
	public static final String VALIDATION_FAILED = "error.validation.failed";

	// FR-001: login alanlari icin format dogrulama mesajlari
	public static final String FIELD_MAX_LENGTH = "validation.field.max-length";
	public static final String NO_LEADING_TRAILING_WHITESPACE = "validation.field.no-leading-trailing-whitespace";
	public static final String INVALID_CLIENT_ID = "validation.client-id.invalid";

	private MessageKeys() {
	}
}
