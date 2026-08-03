package com.etiya.crm.customerservice.messaging;

/**
 * PartyEventListener/ContactMediumEventListener'in lookup-service'e yaptigi
 * cagri basarisiz oldugunda (agi/Keycloak/lookup-service kendisi gecici olarak
 * erisilemez) firlatilir. Amac: DLT'ye dusen "Listener failed" log'unun
 * altindaki ham Feign/RestClient exception'i, "bu muhtemelen gecici bir altyapi
 * sorunu, kod hatasi degil" diye acikca etiketlemek - bkz. LogMessages.LOOKUP_SERVICE_CALL_FAILED.
 * Business exception DEGILDIR (GlobalExceptionHandler tarafindan islenmez) -
 * hicbir zaman bir HTTP cevabina donusmez, sadece Kafka retry/DLT akisinda gorulur.
 */
public class LookupServiceUnavailableException extends RuntimeException {

	public LookupServiceUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}
}
