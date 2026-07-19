package com.etiya.crm.customerservice.constants;

/** SLF4J log sablonlari; log.xxx(...) cagrilarinda literal metin kullanilmaz. */
public final class LogMessages {

	public static final String ONBOARDING_CONTACT_FAILED =
			"contact-address-service call failed during onboarding, compensating customer {}";

	public static final String ONBOARDING_FAILED_COMPENSATING_PARTY =
			"onboarding failed after party-service call, compensating party {}";

	public static final String FAKE_KPS_VERIFICATION = "Fake KPS verification for nationalId={} (always succeeds)";

	public static final String PARTY_EVENT_CUSTOMER_NOT_FOUND =
			"No customer found for partyRoleId={}, skipping CUSTOMER_SEARCH_VIEW sync";

	public static final String UNKNOWN_PARTY_EVENT_TYPE = "Unhandled party event type={}, ignoring";

	public static final String CONTACT_MEDIUM_EVENT_CUSTOMER_NOT_FOUND =
			"No CUSTOMER_SEARCH_VIEW row found for custId={}, skipping gsm sync";

	public static final String UNKNOWN_CONTACT_MEDIUM_EVENT_TYPE = "Unhandled contact medium event type={}, ignoring";

	public static final String LOOKUP_SERVICE_CALL_FAILED =
			"lookup-service call failed (expected to be transient: network/auth), will retry via RetryableTopic: {}";

	private LogMessages() {
	}
}
