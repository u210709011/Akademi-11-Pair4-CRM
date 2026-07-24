package com.etiya.crm.shared.contracts.gnltp;

/**
 * lookup-service GNL_TP tablosundaki shrt_code (deger) degerleri - her biri {@link GnlTpGroups}'taki
 * bir grubun altinda yasar, GET /api/v1/general-types/resolve/{entCodeName}/{shrtCode} icin kullanilir.
 * Prefix sadece iki grup arasinda gercek bir isim carpismasi varsa eklenir - onun disinda hangi
 * gruba ait oldugu yandaki yorumdan zaten belli.
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

	// GnlTpGroups.PARTY_TYPE altinda
	public static final String INDIVIDUAL = "INDIVIDUAL";
	public static final String CORPORATE = "CORPORATE";

	// GnlTpGroups.PARTY_ROLE_TYPE altinda
	public static final String CUSTOMER_ROLE = "CUSTOMER";
	public static final String PARTNER = "PARTNER";

	// GnlTpGroups.GENDER altinda
	public static final String MALE = "MALE";
	public static final String FEMALE = "FEMALE";

	// GnlTpGroups.CITY altinda
	public static final String ANKARA = "ANKARA";

	// GnlTpGroups.CUSTOMER_TYPE altinda
	public static final String YOUNG = "YOUNG";
	public static final String RETIRED = "RETIRED";

	private GnlTpCodes() {
	}
}
