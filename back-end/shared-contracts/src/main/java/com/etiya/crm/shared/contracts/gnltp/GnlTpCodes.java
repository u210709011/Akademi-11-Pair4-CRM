package com.etiya.crm.shared.contracts.gnltp;

/**
 * lookup-service GNL_TP tablosundaki shrt_code (deger) degerleri - her biri {@link GnlTpGroups}'taki
 * bir grubun altinda yasar, GET /api/v1/general-types/resolve/{entCodeName}/{shrtCode} icin kullanilir.
 * Prefix sadece iki grup arasinda gercek bir isim carpismasi varsa eklenir (bkz. CUSTOMER_ACCOUNT/
 * CUSTOMER_ROLE) - onun disinda hangi gruba ait oldugu yandaki yorumdan zaten belli.
 */
public final class GnlTpCodes {

	// GnlTpGroups.CONTACT_MEDIUM altinda
	public static final String LANDLINE = "PSTN";
	public static final String FAX = "FAX";
	public static final String MOBILE = "GSM";
	public static final String EMAIL = "EML";

	// GnlTpGroups.ACCOUNT_TYPE altinda
	public static final String CUSTOMER_ACCOUNT = "CUST_ACCT";
	public static final String BILLING_ACCOUNT = "BILL_ACCT";

	// GnlTpGroups.PARTY_TYPE (gercek ent_code_name: CAM_PARTY_TYPE) altinda
	public static final String INDIVIDUAL = "INDV";
	public static final String CORPORATE = "ORG";

	// GnlTpGroups.PARTY_ROLE_TYPE altinda - HENUZ DBeaver ile dogrulanmadi, tahmini
	public static final String CUSTOMER_ROLE = "CUSTOMER";
	public static final String PARTNER = "PARTNER";

	// GnlTpGroups.GENDER altinda - HENUZ DBeaver ile dogrulanmadi, tahmini
	public static final String MALE = "MALE";
	public static final String FEMALE = "FEMALE";

	// GnlTpGroups.CITY altinda - HENUZ DBeaver ile dogrulanmadi, tahmini
	public static final String ANKARA = "ANKARA";

	// GnlTpGroups.CUSTOMER_TYPE altinda - HENUZ DBeaver ile dogrulanmadi, tahmini
	public static final String YOUNG = "YOUNG";
	public static final String RETIRED = "RETIRED";

	// GnlTpGroups.PROD_SPEC_RSRC_SPEC / PROD_SPEC_SRVC_SPEC altinda
	public static final String REALIZED = "REALIZED";

	// GnlTpGroups.PROD_REL altinda
	public static final String PARENT_PRODUCT = "PRNTPROD";

	// GnlTpGroups.PARTY altinda (genel/varsayilan siniflandirma)
	public static final String GENERAL = "GNL";

	private GnlTpCodes() {
	}
}
