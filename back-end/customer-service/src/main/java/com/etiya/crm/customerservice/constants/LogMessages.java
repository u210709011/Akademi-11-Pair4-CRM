package com.etiya.crm.customerservice.constants;

/** SLF4J log sablonlari; log.xxx(...) cagrilarinda literal metin kullanilmaz. */
public final class LogMessages {

	public static final String ONBOARDING_CONTACT_FAILED =
			"contact-address-service call failed during onboarding, compensating customer {}";

	public static final String ONBOARDING_FAILED_COMPENSATING_PARTY =
			"onboarding failed after party-service call, compensating party {}";

	public static final String ONBOARDING_CONTACT_COMPENSATION_FAILED =
			"contact-info-service compensation (deleteByCustomerId) failed for custId={} - manual cleanup may be required";

	public static final String ONBOARDING_PARTY_COMPENSATION_FAILED =
			"party-service compensation (deleteParty) failed for partyId={} - manual cleanup may be required";

	public static final String FAKE_KPS_VERIFICATION = "Fake KPS verification for nationalId={} (always succeeds)";

	public static final String PARTY_EVENT_CUSTOMER_NOT_FOUND =
			"No customer found for partyRoleId={}, skipping CUSTOMER_SEARCH_VIEW sync";

	public static final String UNKNOWN_PARTY_EVENT_TYPE = "Unhandled party event type={}, ignoring";

	public static final String CONTACT_MEDIUM_EVENT_CUSTOMER_NOT_FOUND =
			"No CUSTOMER_SEARCH_VIEW row found for custId={}, skipping gsm sync";

	public static final String UNKNOWN_CONTACT_MEDIUM_EVENT_TYPE = "Unhandled contact medium event type={}, ignoring";

	public static final String LOOKUP_SERVICE_CALL_FAILED =
			"lookup-service call failed (expected to be transient: network/auth), will retry via RetryableTopic: {}";

	public static final String PARTY_EVENT_DLT =
			"party-events DLT'ye dustu (tum retry denemeleri tukendi): eventId={}, type={}, exception={}: {}";

	public static final String CONTACT_MEDIUM_EVENT_DLT =
			"contact-medium-events DLT'ye dustu (tum retry denemeleri tukendi): eventId={}, type={}, exception={}: {}";

	public static final String LOOKUP_EXISTS_IN_GROUP_FAILED =
			"lookup-service existsInGroup check failed for id={}, entCodeName={}, treating as not-found: {}";

	public static final String UNEXPECTED_ERROR = "Unexpected error";

	public static final String CACHE_GET_FAILED = "Cache '{}' okunamadi (key={}), DB'ye dusuluyor: {}";
	public static final String CACHE_PUT_FAILED = "Cache '{}' yazilamadi (key={}): {}";
	public static final String CACHE_EVICT_FAILED = "Cache '{}' temizlenemedi (key={}): {}";
	public static final String CACHE_CLEAR_FAILED = "Cache '{}' tumuyle temizlenemedi: {}";

	public static final String SAGA_STARTED = "Customer deletion saga started: custId={}";

	public static final String SAGA_STEP_RESULT_RECEIVED =
			"Saga step result received: custId={}, step={}, success={}, reason={}";

	public static final String SAGA_NOT_FOUND =
			"No CustomerDeletionSaga found for custId={}, ignoring step result (step={})";

	public static final String SAGA_ALREADY_RESOLVED =
			"CustomerDeletionSaga for custId={} already resolved (status={}), ignoring late step result (step={})";

	public static final String SAGA_COMPLETED = "Customer deletion saga completed successfully: custId={}";

	public static final String SAGA_COMPENSATING =
			"Customer deletion saga step failed (step={}, reason={}), triggering compensation for custId={}";

	public static final String SAGA_COMPENSATED =
			"Customer deletion saga compensated (customer reactivated locally, compensate command broadcast): custId={}";

	public static final String SAGA_STEP_RESULT_EVENT_DLT =
			"customer-deletion-saga-events DLT'ye dustu (tum retry denemeleri tukendi): eventId={}, custId={}, step={}, exception={}: {}";

	private LogMessages() {
	}
}
