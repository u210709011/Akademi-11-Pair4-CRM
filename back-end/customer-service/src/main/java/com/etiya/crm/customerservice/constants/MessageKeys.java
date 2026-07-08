package com.etiya.crm.customerservice.constants;

/**
 * messages/messages.properties icindeki key'lerin sabitleri. Ne DTO
 * validasyon annotasyonlarinda ne de exception'larda literal metin
 * kullanilmaz; hepsi buradaki key'ler uzerinden MessageSource'tan cozulur.
 */
public final class MessageKeys {

	public static final String FIELD_REQUIRED = "validation.field.required";
	public static final String NATIONAL_ID_INVALID = "validation.national-id.invalid";
	public static final String BIRTH_DATE_INVALID = "validation.birth-date.invalid";
	public static final String EMAIL_INVALID = "validation.email.invalid";
	public static final String PHONE_INVALID = "validation.phone.invalid";
	public static final String ADDRESS_MAX_EXCEEDED = "validation.address.max-exceeded";
	public static final String ADDRESS_MIN_REQUIRED = "validation.address.min-required";

	public static final String CUSTOMER_NOT_FOUND = "error.customer.not-found";
	public static final String DUPLICATE_NATIONAL_ID = "error.customer.duplicate-national-id";
	public static final String ONBOARDING_FAILED = "error.customer.onboarding-failed";
	public static final String IDENTITY_VERIFICATION_FAILED = "error.identity.verification-failed";

	private MessageKeys() {
	}
}
