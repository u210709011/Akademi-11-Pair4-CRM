package com.etiya.crm.orderservice.constants;

/**
 * messages/messages.properties icindeki key'lerin sabitleri. Exception'larda literal
 * metin kullanilmaz; hepsi buradaki key'ler uzerinden MessageSource'tan cozulur.
 */
public final class MessageKeys {

	public static final String BSN_INTER_SPEC_NOT_FOUND = "error.bsn-inter-spec.not-found";
	public static final String ORDER_NOT_FOUND = "error.order.not-found";
	public static final String ADDRESS_SELECTION_INVALID = "error.order.address-selection-invalid";
	public static final String ACCOUNT_NOT_BELONG_TO_CUSTOMER = "error.order.account-not-belong-to-customer";
	public static final String DUPLICATE_BASKET_ITEM = "error.order.duplicate-basket-item";
	public static final String DOWNSTREAM_CALL_FAILED = "error.downstream.call-failed";
	public static final String VALIDATION_FAILED = "error.validation.failed";
	public static final String UNEXPECTED_ERROR = "error.unexpected";

	private MessageKeys() {
	}
}
