package com.etiya.crm.partyservice.constants;

/** SLF4J log sablonlari; log.xxx(...) cagrilarinda literal metin kullanilmaz. */
public final class LogMessages {

	public static final String CUSTOMER_DELETED_PARTY_ROLE_NOT_FOUND =
			"CustomerDeleted event'i icin PartyRole bulunamadi: partyRoleId={}";

	public static final String CUSTOMER_EVENT_ALREADY_PROCESSED =
			"customer-events mesaji zaten islenmis, atlaniyor: partyRoleId={}";

	public static final String CUSTOMER_EVENT_DLT =
			"customer-events DLT'ye dustu (tum retry denemeleri tukendi): eventId={}, type={}, exception={}: {}";

	public static final String CUSTOMER_DELETED_PARTY_ROLE_REACTIVATE_NOT_FOUND =
			"Compensation icin PartyRole bulunamadi: partyRoleId={}";

	public static final String SAGA_STEP_RESULT_PUBLISH_FAILED =
			"Saga step result event yayinlanamadi (custId={}, step={}, success={}) - orchestrator bu adimi timeout'a kadar bekleyecek";

	public static final String UNEXPECTED_ERROR = "Unexpected error";

	private LogMessages() {
	}
}
