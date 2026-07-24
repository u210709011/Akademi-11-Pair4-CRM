package com.etiya.crm.shared.contracts.gnlst;

/**
 * lookup-service GNL_ST tablosundaki ent_code_name (grup) degerleri - GET
 * /api/v1/general-statuses/resolve/{entCodeName}/{shrtCode} ve
 * /api/v1/general-statuses?entCodeName=... cagrilarinda kullanilir.
 */
public final class GnlStGroups {

	public static final String CUST_STATUS = "CUST_STATUS";
	public static final String ACCOUNT_STATUS = "ACCOUNT_STATUS";

	private GnlStGroups() {
	}
}
