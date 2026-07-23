package com.etiya.crm.shared.contracts.lookup;

/** lookup-service'teki deger gruplarinin kodlari. */
public final class LookupGroups {

	public static final String CUSTOMER_TYPE = "CUST_TP";
	public static final String ACCOUNT_TYPE = "ACCOUNT_TYPE";
	public static final String ACCOUNT_STATUS = "ACCOUNT_STATUS";
	public static final String GENDER = "GENDER";
	public static final String CITY = "CITY";
	public static final String CONTACT_MEDIUM_TYPE = "CNTC_MEDIUM";

	/** contact-info-service polimorfik ADDR/CNTC_MEDIUM.DATA_TP_ID icin. */
	public static final String DATA_TYPE = "DATA_TYPE";

	/** party-service'in PartyRole.partyRoleTypeId'si icin (musteri arama sonucundaki "Rol" kolonu). */
	public static final String PARTY_ROLE_TYPE = "PARTY_ROLE_TYPE";

	private LookupGroups() {
	}
}
