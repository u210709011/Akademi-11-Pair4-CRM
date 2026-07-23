package com.etiya.crm.shared.events;

/**
 * Debezium outbox EventRouter, her servisin outbox tablosundaki
 * aggregate_type degerini "<aggregate_type>-events" topic adina cevirir
 * (bkz. infra/debezium/*-connector.json, transforms.outbox.route.topic.replacement).
 * Bu yuzden asagidaki topic ve aggregate_type sabitleri birbirine bagli:
 * aggregate_type degistirilirse topic adi da degisir.
 */
public final class KafkaTopics {

	/** party-service'in yayinladigi, customer-service'in dinledigi topic. */
	public static final String PARTY_EVENTS = "party-events";

	/** customer-service'in yayinladigi, party-service'in (ve ilerde contact-info-service'in) dinledigi topic. */
	public static final String CUSTOMER_EVENTS = "customer-events";

	/** contact-info-service'in yayinladigi, customer-service'in (CUSTOMER_SEARCH_VIEW.gsm) dinledigi topic. */
	public static final String CONTACT_MEDIUM_EVENTS = "contact-medium-events";

	/** party-service outbox.aggregate_type degeri. */
	public static final String PARTY_AGGREGATE_TYPE = "party";

	/** customer-service outbox.aggregate_type degeri. */
	public static final String CUSTOMER_AGGREGATE_TYPE = "customer";

	/** contact-info-service outbox.aggregate_type degeri. */
	public static final String CONTACT_MEDIUM_AGGREGATE_TYPE = "contact-medium";

	private KafkaTopics() {
	}
}
