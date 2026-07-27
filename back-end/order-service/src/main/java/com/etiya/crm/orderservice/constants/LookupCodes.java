package com.etiya.crm.orderservice.constants;

/** Lookup grup'lari ve servis-ici tablolardaki iyi-bilinen kod (CODE) degerleri. */
public final class LookupCodes {

	/**
	 * TYPE_VALUE tablosunda CUST_ORD'u temsil eden deger. lookup-service'in seed'inde
	 * (V5__seed_general_lookup_data.sql) bu satir henuz eklenmedi - eklenene kadar
	 * bu kodla yapilan cozumleme 404/EntityNotFoundException doner.
	 */
	public static final String DATA_TYPE_CUST_ORD = "CUST_ORD";

	/** BSN_INTER_SPEC.shrt_code - yeni satis siparisi sureci (bkz. V3__seed_bsn_inter_spec.sql). */
	public static final String BSN_INTER_SPEC_NEW_SALE = "NEW_SALE";

	private LookupCodes() {
	}
}
