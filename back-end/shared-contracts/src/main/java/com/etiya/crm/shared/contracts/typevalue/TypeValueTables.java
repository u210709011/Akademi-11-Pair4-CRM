package com.etiya.crm.shared.contracts.typevalue;

/**
 * lookup-service TYPE_VALUE tablosundaki table_name degerleri - GET /api/v1/type-values/by-table/{tableName}
 * ile bir is tablosunun polimorfik tip etiketini (field_name) cozmek icin kullanilir.
 */
public final class TypeValueTables {

	public static final String PARTY = "PARTY";
	public static final String CUSTOMER = "CUST";
	public static final String CUSTOMER_ACCOUNT = "CUST_ACCT";
	public static final String PRODUCT = "PROD";

	/**
	 * TODO: lookup-service'te henuz seed edilmedi - dogru field_name numarasi netlesince
	 * V5__seed_general_lookup_data.sql'e eklenecek. Bu sabiti simdiden ekliyoruz ki
	 * order-service entegrasyonu ayni ismi kullansin.
	 */
	public static final String ORDER = "ORDER";

	private TypeValueTables() {
	}
}
