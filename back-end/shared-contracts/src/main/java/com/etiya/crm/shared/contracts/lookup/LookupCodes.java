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

	public static final String CONTACT_MEDIUM_EMAIL = "EMAIL";
	public static final String CONTACT_MEDIUM_MOBILE_PHONE = "MOBILE_PHONE";
	public static final String CONTACT_MEDIUM_HOME_PHONE = "HOME_PHONE";
	public static final String CONTACT_MEDIUM_FAX = "FAX";

	/** DATA_TYPE grubunda musteriyi temsil eden deger (lookup seed: value_id=102). */
	public static final String DATA_TYPE_CUSTOMER = "CUST";

	/** PARTY_ROLE_TYPE grubunda musteri rolunu temsil eden deger (lookup seed: value_id=2001). */
	public static final String PARTY_ROLE_CUSTOMER = "CUSTOMER";

	private LookupCodes() {
	}
}
