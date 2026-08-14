package com.etiya.crm.customerservice.constants;

/**
 * messages/messages.properties icindeki key'lerin sabitleri. Ne DTO
 * validasyon annotasyonlarinda ne de exception'larda literal metin
 * kullanilmaz; hepsi buradaki key'ler uzerinden MessageSource'tan cozulur.
 */
public final class MessageKeys {

	public static final String FIELD_REQUIRED = "validation.field.required";
	public static final String FIELD_MAX_LENGTH = "validation.field.max-length";
	public static final String NATIONAL_ID_INVALID = "validation.national-id.invalid";
	public static final String NAME_INVALID = "validation.name.invalid";
	public static final String BIRTH_DATE_INVALID = "validation.birth-date.invalid";
	public static final String EMAIL_INVALID = "validation.email.invalid";
	public static final String PHONE_INVALID = "validation.phone.invalid";
	public static final String FAX_INVALID = "validation.fax.invalid";
	public static final String ADDRESS_MAX_EXCEEDED = "validation.address.max-exceeded";
	public static final String ADDRESS_MIN_REQUIRED = "validation.address.min-required";
	public static final String BILLING_ACCOUNT_ADDRESS_REQUIRED = "validation.billing-account.address-required";
	public static final String BILLING_ACCOUNT_ADDRESS_CONFLICT = "validation.billing-account.address-conflict";
	public static final String BILLING_ACCOUNT_STATUS_INVALID = "validation.billing-account.status-invalid";

	public static final String CUSTOMER_NOT_FOUND = "error.customer.not-found";
	public static final String ADDRESS_NOT_FOUND = "error.customer.address-not-found";
	public static final String DUPLICATE_NATIONAL_ID = "error.customer.duplicate-national-id";
	public static final String ONBOARDING_FAILED = "error.customer.onboarding-failed";
	public static final String IDENTITY_VERIFICATION_FAILED = "error.identity.verification-failed";
	public static final String DOWNSTREAM_CALL_FAILED = "error.downstream.call-failed";
	public static final String DOWNSTREAM_UNAVAILABLE = "error.downstream.unavailable";
	public static final String UNEXPECTED_ERROR = "error.unexpected";

	public static final String PRIMARY_ADDRESS_CANNOT_BE_DELETED = "error.address.primary-cannot-be-deleted";
	public static final String ADDRESS_LINKED_TO_BILLING_ACCOUNT = "error.address.linked-to-billing-account";
	public static final String BILLING_ACCOUNT_NOT_FOUND = "error.customer.billing-account-not-found";
	public static final String BILLING_ACCOUNT_ACTIVE_CANNOT_BE_DELETED = "error.billing-account.active-cannot-be-deleted";
	public static final String BILLING_ACCOUNT_HAS_ACTIVE_PRODUCTS = "error.billing-account.has-active-products";
	public static final String CUSTOMER_HAS_ACTIVE_BILLING_ACCOUNT = "error.customer.has-active-billing-account";
	public static final String DEFAULT_ACCOUNT_CANNOT_BE_DELETED = "error.customer-account.default-cannot-be-deleted";
	public static final String DEFAULT_ACCOUNT_CANNOT_BE_CHANGED = "error.customer-account.default-cannot-be-changed";
	public static final String ACCOUNT_NUMBER_COLLISION = "error.customer-account.number-collision";
	public static final String PARAMETER_TYPE_MISMATCH = "validation.parameter.type-mismatch";
	public static final String INVALID_REQUEST_PARAMETER = "validation.parameter.invalid";
	public static final String MISSING_REQUEST_PARAMETER = "validation.parameter.missing";
	public static final String METHOD_NOT_SUPPORTED = "error.http.method-not-allowed";
	public static final String ROUTE_NOT_FOUND = "error.http.route-not-found";
	public static final String CITY_INVALID = "validation.city.invalid";
	public static final String GENDER_INVALID = "validation.gender.invalid";

	// FR-002: /customers/search filtre alanlarina ozel mesajlar - doc'ta her alan icin
	// ayri, mevcut genel mesajlardan (NATIONAL_ID_INVALID, PHONE_INVALID, NAME_INVALID)
	// FARKLI bir metin isteniyor, o yuzden onlari degil bunlari kullanirlar.
	public static final String SEARCH_NATIONAL_ID_INVALID = "validation.search.national-id.invalid";
	public static final String SEARCH_GSM_INVALID = "validation.search.gsm.invalid";
	public static final String SEARCH_FIRST_NAME_INVALID = "validation.search.first-name.invalid";
	public static final String SEARCH_LAST_NAME_INVALID = "validation.search.last-name.invalid";
	public static final String SEARCH_ACCOUNT_NUMBER_INVALID = "validation.search.account-number.invalid";
	public static final String SEARCH_FILTER_REQUIRED = "validation.search.filter-required";

	private MessageKeys() {
	}
}
