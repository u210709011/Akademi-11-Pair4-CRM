package com.etiya.crm.orderservice.constants;

/**
 * messages/messages.properties icindeki key'lerin sabitleri.
 */
public final class MessageKeys {

	public static final String BSN_INTER_SPEC_NOT_FOUND = "error.bsn-inter-spec.not-found";
	public static final String ORDER_NOT_FOUND = "error.order.not-found";
	public static final String ADDRESS_SELECTION_INVALID = "error.order.address-selection-invalid";
	public static final String ACCOUNT_NOT_BELONG_TO_CUSTOMER = "error.order.account-not-belong-to-customer";
	public static final String DUPLICATE_BASKET_ITEM = "error.order.duplicate-basket-item";
	public static final String ORDER_NOT_EDITABLE = "error.order.not-editable";
	public static final String SERVICE_ADDRESS_MISSING = "error.order.service-address-missing";
	public static final String ORDER_ITEM_NOT_FOUND = "error.order.item-not-found";
	public static final String ADDRESS_NOT_BELONG_TO_CUSTOMER = "error.order.address-not-belong-to-customer";
	public static final String CHARACTERISTIC_VALUE_MISMATCH = "error.order.characteristic-value-mismatch";
	public static final String DOWNSTREAM_CALL_FAILED = "error.downstream.call-failed";
	public static final String DOWNSTREAM_UNAVAILABLE = "error.downstream.unavailable";
	public static final String VALIDATION_FAILED = "error.validation.failed";
	public static final String UNEXPECTED_ERROR = "error.unexpected";

	// bean validation (@NotNull/@NotBlank/@Size/@NotEmpty message="{...}") icin - alan bazli
	public static final String FIELD_REQUIRED = "validation.field.required";
	public static final String FIELD_TOO_LONG = "validation.field.too-long";
	public static final String LIST_EMPTY = "validation.list.empty";

	private MessageKeys() {
	}
}
