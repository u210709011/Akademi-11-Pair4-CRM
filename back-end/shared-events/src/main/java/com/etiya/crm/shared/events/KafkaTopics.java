package com.etiya.crm.shared.events;


/** Servisler arası Kafka topic ve aggregate adlarını merkezi tutar. */
public final class KafkaTopics {

	/** party-service'in yayinladigi, customer-service'in dinledigi topic. */
	public static final String PARTY_EVENTS = "party-events";

	/** customer-service'in yayinladigi, party-service'in ve contact-info-service'i dinledigi topic. */
	public static final String CUSTOMER_EVENTS = "customer-events";

	/** contact-info-service'in yayinladigi, customer-service'in dinledigi topic. */
	public static final String CONTACT_MEDIUM_EVENTS = "contact-medium-events";

	/** order-service'in yayinladigi topic */
	public static final String ORDER_EVENTS = "order-events";

	/** party-service outbox.aggregate_type degeri. */
	public static final String PARTY_AGGREGATE_TYPE = "party";

	/** customer-service outbox.aggregate_type degeri. */
	public static final String CUSTOMER_AGGREGATE_TYPE = "customer";

	/** contact-info-service outbox.aggregate_type degeri. */
	public static final String CONTACT_MEDIUM_AGGREGATE_TYPE = "contact-medium";

	/** order-service outbox.aggregate_type degeri. */
	public static final String ORDER_AGGREGATE_TYPE = "order";

	private KafkaTopics() {
	}
}
