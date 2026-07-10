package com.etiya.crm.shared.events.customer;

/** "customer-events" topic'inde tasinan event'lerin "type" degerleri. */
public final class CustomerEventTypes {

	public static final String CUSTOMER_ONBOARDED = "CustomerOnboarded";
	public static final String CUSTOMER_DELETED = "CustomerDeleted";

	private CustomerEventTypes() {
	}
}
