package com.etiya.crm.shared.contracts.messaging;

/** DLT handler'larinin ortak Micrometer counter adi ve tag anahtarlari. */
public final class DltMetrics {

	public static final String DLT_EVENTS_COUNTER = "kafka.dlt.events";
	public static final String TAG_EVENT_TYPE = "eventType";
	public static final String TAG_LISTENER = "listener";

	private DltMetrics() {
	}
}
