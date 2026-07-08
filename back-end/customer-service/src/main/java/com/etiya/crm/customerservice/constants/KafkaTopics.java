package com.etiya.crm.customerservice.constants;

public final class KafkaTopics {

	/** party-service'in yayinladigi, bu servisin dinledigi topic. */
	public static final String PARTY_EVENTS = "party-events";

	/**
	 * Bu servisin OUTBOX.aggregate_type olarak yazdigi deger. Debezium
	 * EventRouter bunu okuyup "customer-events" topic'ine yayinlar
	 * (bkz. infra/debezium/customer-outbox-connector.json).
	 */
	public static final String CUSTOMER_AGGREGATE_TYPE = "customer";

	private KafkaTopics() {
	}
}
