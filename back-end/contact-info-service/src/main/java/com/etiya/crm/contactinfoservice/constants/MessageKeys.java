package com.etiya.crm.contactinfoservice.constants;

/**
 * messages/messages.properties icindeki key'lerin sabitleri. Exception'larda literal
 * metin kullanilmaz; hepsi buradaki key'ler uzerinden MessageSource'tan cozulur.
 */
public final class MessageKeys {

	public static final String ADDRESS_NOT_FOUND = "error.address.not-found";
	public static final String ADDRESS_MAX_EXCEEDED = "error.address.max-exceeded";
	public static final String PRIMARY_ADDRESS_CANNOT_BE_DELETED = "error.address.primary-cannot-be-deleted";
	public static final String ADDRESS_LINKED_TO_BILLING_ACCOUNT = "error.address.linked-to-billing-account";
	public static final String CONTACT_MEDIUM_NOT_FOUND = "error.contact-medium.not-found";
	public static final String CONTACT_MEDIUM_INVALID_EMAIL_FORMAT = "error.contact-medium.invalid-email-format";
	public static final String CONTACT_MEDIUM_INVALID_PHONE_FORMAT = "error.contact-medium.invalid-phone-format";
	public static final String VALIDATION_FAILED = "error.validation.failed";
	public static final String UNEXPECTED_ERROR = "error.unexpected";

	private MessageKeys() {
	}
}
