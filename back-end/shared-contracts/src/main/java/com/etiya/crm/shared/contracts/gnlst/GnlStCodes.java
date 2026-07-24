package com.etiya.crm.shared.contracts.gnlst;

/**
 * lookup-service GNL_ST tablosundaki shrt_code (deger) degerleri - her biri {@link GnlStGroups}'taki
 * bir grubun altinda yasar, GET /api/v1/general-statuses/resolve/{entCodeName}/{shrtCode} icin kullanilir.
 */
public final class GnlStCodes {

	public static final String ACTIVE = "ACTIVE";
	public static final String PASSIVE = "PASSIVE";

	/** Sadece GnlStGroups.CUST_STATUS altinda var. */
	public static final String DELETED = "DELETED";

	private GnlStCodes() {
	}
}
