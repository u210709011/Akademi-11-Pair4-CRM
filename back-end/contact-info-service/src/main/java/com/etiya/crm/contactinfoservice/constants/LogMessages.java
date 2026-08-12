package com.etiya.crm.contactinfoservice.constants;

/** SLF4J log sablonlari; log.xxx(...) cagrilarinda literal metin kullanilmaz. */
public final class LogMessages {

	public static final String CUSTOMER_EVENT_PAYLOAD_PARSE_FAILED = "customer-events payload parse edilemedi: {}";

	public static final String CUSTOMER_EVENT_ALREADY_PROCESSED =
			"customer-events mesaji zaten islenmis, atlaniyor: custId={}";

	public static final String DOWNSTREAM_ERROR_BODY_PARSE_FAILED = "Downstream Feign hata govdesi coz(ul)emedi: {}";

	public static final String UNEXPECTED_ERROR = "Unexpected error";

	private LogMessages() {
	}
}
