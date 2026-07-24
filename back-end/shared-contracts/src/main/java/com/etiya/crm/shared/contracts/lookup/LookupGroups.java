package com.etiya.crm.shared.contracts.lookup;

/**
 * lookup-service'teki eski duz "group_code" modelinin kodlari.
 *
 * @deprecated lookup-service artik bu grup/group_code modelini kullanmiyor - GNL_TP/GNL_ST/
 * TYPE_VALUE semasina gecti (bkz. lookup-service V4/V5 migration'lari). Bu sinif sadece
 * party-service/customer-service/contact-info-service henuz yeni semaya gecmedigi icin duruyor;
 * o servisler guncellenince kaldirilmali. Yeni kod bunu kullanmamali.
 */
@Deprecated(forRemoval = true)
public final class LookupGroups {

	public static final String CUSTOMER_TYPE = "CUST_TP";

	/** gnl_tp / ent_code_name=ACCOUNT_TYPE (shrt_code: CUST_ACCT/BILL_ACCT). */
	public static final String ACCOUNT_TYPE = "ACCOUNT_TYPE";

	/**
	 * gnl_st / ent_code_name=CUST_ACCT (shrt_code: ACTV/PASS/DEL). "ACCOUNT_STATUS" diye bir
	 * grup YOK - lookup-service statuleri tablo adina gore gruplar, tiplerden (ACCOUNT_TYPE)
	 * farkli bir isimlendirme. Bu tutarsizlik lookup-service'in gercegi, birine uydurulmaz.
	 */
	public static final String CUST_ACCT_STATUS = "CUST_ACCT";

	public static final String GENDER = "GENDER";
	public static final String CITY = "CITY";
	public static final String CONTACT_MEDIUM_TYPE = "CNTC_MEDIUM";

	/** contact-info-service polimorfik ADDR/CNTC_MEDIUM.DATA_TP_ID icin. */
	public static final String DATA_TYPE = "DATA_TYPE";

	/** party-service'in PartyRole.partyRoleTypeId'si icin (musteri arama sonucundaki "Rol" kolonu). */
	public static final String PARTY_ROLE_TYPE = "PARTY_ROLE_TYPE";

	private LookupGroups() {
	}
}
