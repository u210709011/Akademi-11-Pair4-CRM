package com.etiya.crm.shared.contracts.error;

/** SLF4J log sablonlari; log.xxx(...) cagrilarinda literal metin kullanilmaz. */
public final class LogMessages {

	public static final String DOWNSTREAM_ERROR_BODY_PARSE_FAILED = "Downstream Feign hata govdesi coz(ul)emedi: {}";
	public static final String NO_FALLBACK_UNEXPECTED_CAUSE = "NoFallbackAvailableException beklenmeyen bir cause ile geldi: {}";
	public static final String DOWNSTREAM_CALL_FAILED_LOG = "Downstream cagri basarisiz: {} -> {} {}";
	public static final String DOWNSTREAM_CIRCUIT_OPEN_LOG = "Downstream circuit breaker OPEN: {}";

	private LogMessages() {
	}
}
