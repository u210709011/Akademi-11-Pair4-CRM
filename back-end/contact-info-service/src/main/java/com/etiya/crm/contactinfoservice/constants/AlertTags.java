package com.etiya.crm.contactinfoservice.constants;

/** Kafka consumer group id'si ve DltAlertNotifier.alert() cagrilarinda kullanilan sabitler. */
public final class AlertTags {

	public static final String SERVICE_NAME = "contact-info-service";
	public static final String CUSTOMER_EVENT_LISTENER = "ContactInfoServiceCustomerEventListener";
	public static final String CUSTOMER_DELETED_EVENT_TYPE = "CustomerDeletedEvent";

	private AlertTags() {
	}
}
