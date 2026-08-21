package com.etiya.crm.shared.events.customer;

/** "customer-events" topic'inde tasinan event'lerin "type" degerleri. */
public final class CustomerEventTypes {

	public static final String CUSTOMER_ONBOARDED = "CustomerOnboarded";
	public static final String CUSTOMER_DELETED = "CustomerDeleted";

	/** Saga compensating command tipi - party/contact-info-service bunu gorunce reactivate eder. */
	public static final String CUSTOMER_DELETION_COMPENSATE = "CustomerDeletionCompensate";

	private CustomerEventTypes() {
	}
}
