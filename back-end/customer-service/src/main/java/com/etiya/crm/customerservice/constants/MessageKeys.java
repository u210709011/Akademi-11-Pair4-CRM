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
	public static final String BILLING_ACCOUNT_ADDRESS_REQUIRED = "validation.billing-account.address-required";

	public static final String CUSTOMER_NOT_FOUND = "error.customer.not-found";
	public static final String ADDRESS_NOT_FOUND = "error.customer.address-not-found";
	public static final String DUPLICATE_NATIONAL_ID = "error.customer.duplicate-national-id";
	public static final String ONBOARDING_FAILED = "error.customer.onboarding-failed";
	public static final String IDENTITY_VERIFICATION_FAILED = "error.identity.verification-failed";

	public static final String PRIMARY_ADDRESS_CANNOT_BE_DELETED = "error.address.primary-cannot-be-deleted";
	public static final String ADDRESS_LINKED_TO_BILLING_ACCOUNT = "error.address.linked-to-billing-account";
	public static final String BILLING_ACCOUNT_NOT_FOUND = "error.customer.billing-account-not-found";
	public static final String BILLING_ACCOUNT_ACTIVE_CANNOT_BE_DELETED = "error.billing-account.active-cannot-be-deleted";
	public static final String BILLING_ACCOUNT_HAS_ACTIVE_PRODUCTS = "error.billing-account.has-active-products";
	public static final String CUSTOMER_HAS_ACTIVE_BILLING_ACCOUNT = "error.customer.has-active-billing-account";

	private MessageKeys() {
	}
}
