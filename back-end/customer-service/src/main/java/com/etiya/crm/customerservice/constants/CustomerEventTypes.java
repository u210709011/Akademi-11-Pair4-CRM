package com.etiya.crm.customerservice.constants;

/** OUTBOX.type degerleri; customer-events topic'ine bu isimlerle yayinlanir. */
public final class CustomerEventTypes {

	public static final String CUSTOMER_ONBOARDED = "CustomerOnboarded";
	public static final String CUSTOMER_DELETED = "CustomerDeleted";

	private CustomerEventTypes() {
	}
}
