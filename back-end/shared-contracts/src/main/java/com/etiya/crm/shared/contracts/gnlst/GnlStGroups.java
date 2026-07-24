package com.etiya.crm.shared.contracts.gnlst;

/**
 * lookup-service GNL_ST tablosundaki ent_code_name (grup) degerleri - GET
 * /api/v1/general-statuses/resolve/{entCodeName}/{shrtCode} ve
 * /api/v1/general-statuses?entCodeName=... cagrilarinda kullanilir.
 */
public final class GnlStGroups {

	public static final String CUST_STATUS = "CUST_STATUS";
	public static final String ACCOUNT_STATUS = "ACCOUNT_STATUS";
 *
 * Gercek DBeaver verisinden: ent_code_name genelde durumu tasinan is tablosunun kendi adi
 * (orn. PROD, CUST, CUST_ACCT) - TYPE_VALUE.table_name ile ayni isimlendirme mantigi.
 */
public final class GnlStGroups {

	public static final String CUST_ORDER = "CUST_ORD";
	public static final String CUST_ACCT_PROD_INVL = "CUST_ACCT_PROD_INVL";
	public static final String PRODUCT = "PROD";
	public static final String PRODUCT_CHAR_VAL = "PROD_CHAR_VAL";
	public static final String RESOURCE_SPEC = "RSRC_SPEC";
	public static final String INDIVIDUAL = "IND";
	public static final String CUSTOMER_ACCOUNT = "CUST_ACCT";
	public static final String PRODUCT_SPEC = "PROD_SPEC";
	public static final String PRODUCT_SPEC_SERVICE_SPEC = "PROD_SPEC_SRVC_SPEC";
	public static final String PARTY = "PARTY";
	public static final String PARTY_ROLE = "PARTY_ROLE";
	public static final String PRODUCT_SPEC_RESOURCE_SPEC = "PROD_SPEC_RSRC_SPEC";
	public static final String SERVICE_SPEC = "SRVC_SPEC";
	public static final String PRODUCT_CATALOG = "PROD_CATAL";
	public static final String PRODUCT_OFFER = "PROD_OFR";
	public static final String CUSTOMER = "CUST";
	public static final String PRODUCT_CATALOG_OFFER = "PROD_CATAL_PROD_OFR";
	public static final String CAMPAIGN = "CMPG";
	public static final String CONTACT_MEDIUM = "CNTC_MEDIUM";

	private GnlStGroups() {
	}
}
