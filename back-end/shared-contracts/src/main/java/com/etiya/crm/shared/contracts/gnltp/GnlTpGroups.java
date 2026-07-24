package com.etiya.crm.shared.contracts.gnltp;

/**
 * lookup-service GNL_TP tablosundaki ent_code_name (grup) degerleri - GET
 * /api/v1/general-types/resolve/{entCodeName}/{shrtCode} ve
 * /api/v1/general-types?entCodeName=... cagrilarinda kullanilir.
 */
public final class GnlTpGroups {

	public static final String CONTACT_MEDIUM = "CNTC_MEDIUM";
	public static final String ACCOUNT_TYPE = "ACCOUNT_TYPE";
	public static final String PARTY_TYPE = "PARTY_TYPE";
	public static final String PARTY_ROLE_TYPE = "PARTY_ROLE_TYPE";
	public static final String GENDER = "GENDER";
	public static final String CITY = "CITY";
	public static final String CUSTOMER_TYPE = "CUSTOMER_TYPE";

	private GnlTpGroups() {
	}
}
