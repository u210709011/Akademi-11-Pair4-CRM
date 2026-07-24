package com.etiya.crm.shared.contracts.lookup;

/**
 * Eski duz lookup grup'lari icindeki iyi-bilinen kod (CODE) degerleri.
 *
 * @deprecated {@link LookupGroups} ile ayni sebep - lookup-service artik bu modeli kullanmiyor.
 * party-service/customer-service/contact-info-service yeni semaya (GNL_TP/GNL_ST/TYPE_VALUE)
 * gecince kaldirilmali.
 */
@Deprecated(forRemoval = true)
public final class LookupCodes {

	public static final String STATUS_ACTIVE = "ACTIVE";

	public static final String CONTACT_MEDIUM_EMAIL = "EML";
	public static final String CONTACT_MEDIUM_MOBILE_PHONE = "GSM";
	public static final String CONTACT_MEDIUM_HOME_PHONE = "PSTN";
	public static final String CONTACT_MEDIUM_FAX = "FAX";

	/** type_value.table_name grubunda musteriyi temsil eden deger (bkz. lookup-service V5 seed). */
	public static final String TABLE_NAME_CUSTOMER = "CUST";

	/** PARTY_ROLE_TYPE grubunda musteri rolunu temsil eden deger. */
	public static final String PARTY_ROLE_CUSTOMER = "CUSTOMER";

	public static final String ACCOUNT_TYPE_CUST_ACCT = "CUST_ACCT";
	public static final String ACCOUNT_TYPE_BILL_ACCT = "BILL_ACCT";
	public static final String ACCOUNT_STATUS_ACTIVE = "ACTIVE";
	public static final String ACCOUNT_STATUS_PASSIVE = "PASSIVE";

	private LookupCodes() {
	}
}
