package com.etiya.crm.orderservice.constants;

/** SLF4J log sablonlari; log.xxx(...) cagrilarinda literal metin kullanilmaz. */
public final class LogMessages {

	public static final String UNEXPECTED_ERROR = "Unexpected error";

	public static final String NO_JWT_FOR_DOWNSTREAM_CALL =
			"Downstream Feign cagrisi icin SecurityContext'te JWT yok - istek reddediliyor";

	public static final String LOOKUP_EXISTS_IN_GROUP_FAILED =
			"lookup-service existsInGroup check failed for id={}, entCodeName={}, treating as not-found: {}";

	private LogMessages() {
	}
}
