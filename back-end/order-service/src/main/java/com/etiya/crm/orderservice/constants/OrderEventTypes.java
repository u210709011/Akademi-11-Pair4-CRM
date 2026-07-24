package com.etiya.crm.orderservice.constants;

/**
 * Debezium'un outbox.aggregate_type degerinden ureteceigi topic adi "order-events"
 * olacak (bkz. infra/debezium/order-outbox-connector.json, route.topic.replacement).
 */
public final class OrderEventTypes {

	public static final String AGGREGATE_TYPE = "order";

	public static final String ORDER_SUBMITTED = "ORDER_SUBMITTED";

	private OrderEventTypes() {
	}
}
