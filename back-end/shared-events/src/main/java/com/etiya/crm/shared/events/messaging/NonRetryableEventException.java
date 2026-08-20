package com.etiya.crm.shared.events.messaging;

/**
 * Kafka event handler'larinda kalici (retry ile cozulmeyecek) hatalari isaretlemek
 * icin kullanilir - orn. event payload'inda eksik/gecersiz zorunlu veri, bilinmeyen
 * bir domain durumu. Her servisin @RetryableTopic'inde "exclude" listesine eklenmesi
 * gerekir; aksi halde normal Exception gibi 4 kez retry edilip DLT'ye oyle duser.
 * Gecici altyapi hatalari (orn. lookup-service'e erisilemiyor) icin BU DEGIL,
 * ilgili servisteki retryable exception (orn. customer-service'teki
 * LookupServiceUnavailableException) kullanilmali.
 */
public class NonRetryableEventException extends RuntimeException {

	public NonRetryableEventException(String message) {
		super(message);
	}

	public NonRetryableEventException(String message, Throwable cause) {
		super(message, cause);
	}
}
