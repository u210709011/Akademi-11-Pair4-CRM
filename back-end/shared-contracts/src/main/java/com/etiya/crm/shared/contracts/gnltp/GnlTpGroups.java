package com.etiya.crm.shared.contracts.gnltp;

/**
 * lookup-service GNL_TP tablosundaki ent_code_name (grup) degerleri - GET
 * /api/v1/general-types/resolve/{entCodeName}/{shrtCode} ve
 * /api/v1/general-types?entCodeName=... cagrilarinda kullanilir.
 */
public final class GnlTpGroups {

	public static final String CONTACT_MEDIUM = "CNTC_MEDIUM";
	public static final String ACCOUNT_TYPE = "ACCOUNT_TYPE";

	/** Gercek veride "PARTY_TYPE" degil "CAM_PARTY_TYPE" (bkz. DBeaver: id=163 Kurumsal/ORG, id=164 Bireysel/INDV). */
	public static final String PARTY_TYPE = "CAM_PARTY_TYPE";

	public static final String PARTY_ROLE_TYPE = "PARTY_ROLE_TYPE";
	public static final String GENDER = "GENDER";
	public static final String CITY = "CITY";
	public static final String CUSTOMER_TYPE = "CUSTOMER_TYPE";

	/** Genel/varsayilan PARTY sinifi (gercek veri: id=21, "Genel"/"GNL"). Amaci netlesmedi. */
	public static final String PARTY = "PARTY";

	public static final String PROD_SPEC_RSRC_SPEC = "PROD_SPEC_RSRC_SPEC";
	public static final String PROD_SPEC_SRVC_SPEC = "PROD_SPEC_SRVC_SPEC";
	public static final String PROD_REL = "PROD_REL";

	private GnlTpGroups() {
	}
}
