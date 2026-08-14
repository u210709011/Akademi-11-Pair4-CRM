package com.etiya.crm.lookupservice.constants;

/**
 * Swagger/OpenAPI @Tag, @Operation ve @Parameter aciklamalarinda kullanilan literal
 * metinlerin sabitleri. Java'da annotation attribute degerleri compile-time constant
 * olmak zorunda oldugundan bu class kullanilir; messages*.properties/MessageKeys
 * mekanizmasi (runtime, Accept-Language'a gore localize edilen kullanici hata mesajlari
 * icindir) burada uygulanmaz.
 */
public final class SwaggerText {

	// GnlCharController
	public static final String GNL_CHAR_TAG_NAME = "Characteristics (GNL_CHAR)";
	public static final String GNL_CHAR_TAG_DESCRIPTION = "Karakteristik tanimlari CRUD";
	public static final String GNL_CHAR_GET_ALL_SUMMARY = "Tum karakteristikleri listele";
	public static final String GNL_CHAR_GET_BY_ID_SUMMARY = "Karakteristigi id ile getir";
	public static final String GNL_CHAR_ADD_SUMMARY = "Yeni karakteristik ekle";
	public static final String GNL_CHAR_UPDATE_SUMMARY = "Karakteristigi guncelle";
	public static final String GNL_CHAR_UPDATE_DESCRIPTION = "shrtCode degistirilemez.";
	public static final String GNL_CHAR_DELETE_SUMMARY = "Karakteristigi sil (soft-delete)";

	// GnlCharValController
	public static final String GNL_CHAR_VAL_TAG_NAME = "Characteristic Values (GNL_CHAR_VAL)";
	public static final String GNL_CHAR_VAL_TAG_DESCRIPTION = "Karakteristik degerleri CRUD";
	public static final String GNL_CHAR_VAL_GET_ALL_SUMMARY = "Tum karakteristik degerlerini listele";
	public static final String GNL_CHAR_VAL_GET_BY_ID_SUMMARY = "Karakteristik degerini id ile getir";
	public static final String GNL_CHAR_VAL_ADD_SUMMARY = "Yeni karakteristik degeri ekle";
	public static final String GNL_CHAR_VAL_ADD_DESCRIPTION = "charId var olan bir GNL_CHAR'a ait olmali.";
	public static final String GNL_CHAR_VAL_UPDATE_SUMMARY = "Karakteristik degerini guncelle";
	public static final String GNL_CHAR_VAL_UPDATE_DESCRIPTION = "charId degistirilemez.";
	public static final String GNL_CHAR_VAL_DELETE_SUMMARY = "Karakteristik degerini sil (soft-delete)";

	// GnlStController
	public static final String GNL_ST_TAG_NAME = "General Statuses (GNL_ST)";
	public static final String GNL_ST_TAG_DESCRIPTION = "Genel durum grubu tanimlari CRUD";
	public static final String GNL_ST_GET_ALL_SUMMARY = "Durum degerlerini listele";
	public static final String GNL_ST_GET_ALL_DESCRIPTION = "entCodeName verilirse sadece o gruba ait degerler doner (orn. CUST_STATUS); "
			+ "verilmezse TUMU doner.";
	public static final String GNL_ST_GET_BY_ID_SUMMARY = "Durum degerini id ile getir";
	public static final String GNL_ST_RESOLVE_SUMMARY = "Durum degerini grup+kisa kod ile coz";
	public static final String GNL_ST_RESOLVE_DESCRIPTION = "Kod bazli erisim. Ornek: GET /api/v1/general-statuses/resolve/CUST_STATUS/ACTIVE";
	public static final String GNL_ST_ADD_SUMMARY = "Yeni durum grubu ekle";
	public static final String GNL_ST_UPDATE_SUMMARY = "Durum grubunu guncelle";
	public static final String GNL_ST_DELETE_SUMMARY = "Durum grubunu sil (soft-delete)";

	// GnlTpController
	public static final String GNL_TP_TAG_NAME = "General Types (GNL_TP)";
	public static final String GNL_TP_TAG_DESCRIPTION = "Genel tip grubu tanimlari CRUD";
	public static final String GNL_TP_GET_ALL_SUMMARY = "Tip degerlerini listele";
	public static final String GNL_TP_GET_ALL_DESCRIPTION = "entCodeName verilirse sadece o gruba ait degerler doner (orn. CNTC_MEDIUM); "
			+ "verilmezse TUMU doner.";
	public static final String GNL_TP_GET_BY_ID_SUMMARY = "Tip degerini id ile getir";
	public static final String GNL_TP_RESOLVE_SUMMARY = "Tip degerini grup+kisa kod ile coz";
	public static final String GNL_TP_RESOLVE_DESCRIPTION = "Kod bazli erisim. Ornek: GET /api/v1/general-types/resolve/CNTC_MEDIUM/GSM";
	public static final String GNL_TP_ADD_SUMMARY = "Yeni tip grubu ekle";
	public static final String GNL_TP_UPDATE_SUMMARY = "Tip grubunu guncelle";
	public static final String GNL_TP_DELETE_SUMMARY = "Tip grubunu sil (soft-delete)";

	// RsrcSpecController
	public static final String RSRC_SPEC_TAG_NAME = "Resource Specs (RSRC_SPEC)";
	public static final String RSRC_SPEC_TAG_DESCRIPTION = "Kaynak spesifikasyonlari CRUD";
	public static final String RSRC_SPEC_GET_ALL_SUMMARY = "Tum kaynak speclerini listele";
	public static final String RSRC_SPEC_GET_BY_ID_SUMMARY = "Kaynak specini id ile getir";
	public static final String RSRC_SPEC_ADD_SUMMARY = "Yeni kaynak speci ekle";
	/** Ayni metin SrvcSpecController#add icin de kullanilir (byte-for-byte ayni). */
	public static final String SPEC_ADD_ST_ID_DESCRIPTION = "stId var olan bir GNL_ST satirina ait olmali.";
	public static final String RSRC_SPEC_UPDATE_SUMMARY = "Kaynak specini guncelle";
	public static final String RSRC_SPEC_DELETE_SUMMARY = "Kaynak specini sil";

	// SrvcSpecController
	public static final String SRVC_SPEC_TAG_NAME = "Service Specs (SRVC_SPEC)";
	public static final String SRVC_SPEC_TAG_DESCRIPTION = "Servis spesifikasyonlari CRUD";
	public static final String SRVC_SPEC_GET_ALL_SUMMARY = "Tum servis speclerini listele";
	public static final String SRVC_SPEC_GET_BY_ID_SUMMARY = "Servis specini id ile getir";
	public static final String SRVC_SPEC_ADD_SUMMARY = "Yeni servis speci ekle";
	public static final String SRVC_SPEC_UPDATE_SUMMARY = "Servis specini guncelle";
	public static final String SRVC_SPEC_DELETE_SUMMARY = "Servis specini sil";

	// TypeValueController
	public static final String TYPE_VALUE_TAG_NAME = "Type Values (TYPE_VALUE)";
	public static final String TYPE_VALUE_TAG_DESCRIPTION = "Is tablolarina (PROD, PARTY, CUST, CUST_ACCT...) atanan polimorfik tip etiketleri CRUD";
	public static final String TYPE_VALUE_GET_ALL_SUMMARY = "Tum degerleri listele";
	public static final String TYPE_VALUE_GET_BY_ID_SUMMARY = "Degeri id ile getir";
	public static final String TYPE_VALUE_GET_BY_TABLE_NAME_SUMMARY = "Bir is tablosunun polimorfik tip etiketini coz";
	public static final String TYPE_VALUE_GET_BY_TABLE_NAME_DESCRIPTION = "Kod bazli erisim. Ornek: GET /api/v1/type-values/by-table/CUST";
	public static final String TYPE_VALUE_ADD_SUMMARY = "Yeni tip etiketi ekle";
	public static final String TYPE_VALUE_ADD_DESCRIPTION = "tableName benzersiz olmali - her is tablosunun tek bir tip etiketi olur.";
	public static final String TYPE_VALUE_UPDATE_SUMMARY = "Degeri guncelle";
	public static final String TYPE_VALUE_UPDATE_DESCRIPTION = "tableName/fieldName degistirilemez (immutable kimlik).";
	public static final String TYPE_VALUE_DELETE_SUMMARY = "Degeri sil (hard delete)";

	// OpenApiConfig
	public static final String OPENAPI_TITLE = "Lookup Service API";
	public static final String OPENAPI_DESCRIPTION =
			"Genel amacli id-deger tablosu (GROUP_CODE + VALUE_ID + CODE + deger). Amaci "
					+ "diger servislerdeki (customer/party/contact-info) kucuk referans tablolarinin "
					+ "(gender, city, status, contact medium type, data type ...) coklanmasini onlemek. "
					+ "Sadece okunur; deger ekleme/degistirme migration ile yapilir (bkz. "
					+ "lookup-service V2__seed_lookup.sql basindaki degismezlik kurallari).";
	public static final String OPENAPI_GATEWAY_SERVER_DESCRIPTION = "API Gateway uzerinden (onerilen)";
	public static final String OPENAPI_DIRECT_SERVER_DESCRIPTION = "Bu servisin kendi adresi (gateway'i atlar)";

	private SwaggerText() {
	}
}
