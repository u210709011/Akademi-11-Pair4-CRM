package com.etiya.crm.partyservice.constants;

/** SLF4J log sablonlari; log.xxx(...) cagrilarinda literal metin kullanilmaz. */
public final class LogMessages {

	public static final String CUSTOMER_DELETED_PARTY_ROLE_NOT_FOUND =
			"CustomerDeleted event'i icin PartyRole bulunamadi: partyRoleId={}";

	public static final String CUSTOMER_EVENT_ALREADY_PROCESSED =
			"customer-events mesaji zaten islenmis, atlaniyor: partyRoleId={}";

	public static final String UNEXPECTED_ERROR = "Unexpected error";

	private LogMessages() {
	}
}
