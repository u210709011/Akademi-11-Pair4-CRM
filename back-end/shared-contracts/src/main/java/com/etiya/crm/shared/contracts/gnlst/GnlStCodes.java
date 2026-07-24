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
 *
 * ACTIVE/PASSIVE/DELETED neredeyse her grupta ortak tekrar eder (bkz. gercek DBeaver verisi);
 * grup-ozel kodlar (orn. CUST_ORD'un WAIT/MIDLWARE/FINISHED/REJECTED'i) ayrica listelenir.
 */
public final class GnlStCodes {

	/** Neredeyse her grupta var (PROD, CUST, CUST_ACCT, PARTY, PARTY_ROLE, RSRC_SPEC, SRVC_SPEC, PROD_SPEC, PROD_CATAL, PROD_OFR, CNTC_MEDIUM, CMPG, IND, PROD_CHAR_VAL, PROD_SPEC_RSRC_SPEC, PROD_SPEC_SRVC_SPEC, PROD_CATAL_PROD_OFR...). */
	public static final String ACTIVE = "ACTV";
	public static final String PASSIVE = "PASS";
	public static final String DELETED = "DEL";

	/** Sadece GnlStGroups.CUSTOMER (CUST) altinda. */
	public static final String CANCELLED = "CNCL";

	/** Sadece GnlStGroups.PARTY altinda (id=61, "Genel"). */
	public static final String GENERAL = "GNL";

	// GnlStGroups.CUST_ORDER altinda
	public static final String WAITING = "WAIT";
	public static final String PROCESSING = "MIDLWARE";
	public static final String FINISHED = "FINISHED";
	public static final String REJECTED = "REJECTED";

	// GnlStGroups.CUST_ACCT_PROD_INVL altinda (bkz. not: bu tablo yeni tasarimda kaldirildi,
	// ama eski referans veride hala var - kullanilmiyor olabilir)
	public static final String PENDING = "PNDG";
	public static final String SUSPENDED = "SPND";

	// GnlStGroups.PRODUCT altinda (yukaridakilere ek olarak)
	public static final String QUOTE_CANCELLED = "QUOTE_DEL";

	private GnlStCodes() {
	}
}
