package com.etiya.crm.orderservice.constants;

/** Swagger/OpenAPI @Tag/@Operation/@Parameter metinleri; annotasyonlarda literal metin kullanilmaz. */
public final class SwaggerText {

	// CustOrdController
	public static final String CUST_ORD_TAG_NAME = "Cust Ord";

	public static final String CUST_ORD_TAG_DESCRIPTION =
			"Offer Selection -> Product Configuration -> Review & Confirm siparis akisi";

	public static final String VALIDATE_BASKET_SUMMARY = "Sepeti dogrular";

	public static final String VALIDATE_BASKET_DESCRIPTION =
			"Offer Selection'da Next'e basilinca, Configuration'a gecmeden once sepeti dogrular.";

	public static final String CREATE_ORDER_SUMMARY = "Siparisi WAIT durumunda acar";

	public static final String CREATE_ORDER_DESCRIPTION =
			"Sepet dogrulandiktan sonra siparisi WAIT durumunda olusturur.";

	public static final String SAVE_CONFIGURATION_SUMMARY = "Karakteristik/adres kaydeder (autosave)";

	public static final String SAVE_CONFIGURATION_DESCRIPTION =
			"Product Configuration ekraninda item bazli karakteristikleri ve servis adresini kaydeder.";

	public static final String FINISH_ORDER_SUMMARY = "Siparisi bitirir";

	public static final String FINISH_ORDER_DESCRIPTION =
			"WAIT -> MIDLWARE gecisi yapar, urunleri product-service'te provizyonlar ve OrderSubmittedEvent yayinlar.";

	public static final String CANCEL_ORDER_SUMMARY = "Siparisi iptal eder";

	public static final String CANCEL_ORDER_DESCRIPTION = "WAIT durumundaki siparisi REJECTED'e cevirir.";

	public static final String GET_ORDER_BY_ID_SUMMARY = "Siparis detayini getirir";

	public static final String GET_ITEMS_BY_ACCOUNT_SUMMARY = "Hesabin satin aldigi urunleri listeler";

	public static final String GET_ITEMS_BY_ACCOUNT_DESCRIPTION = "Bir fatura hesabinin satin aldigi urunleri dondurur.";

	public static final String GET_ORDERS_BY_CUSTOMER_SUMMARY = "Musterinin siparis gecmisini listeler";

	public static final String GET_ACTIVE_OFFERS_SUMMARY = "Hesap icin zaten aktif olan teklifleri listeler";

	public static final String GET_ACTIVE_OFFERS_DESCRIPTION = "Bir hesap icin halihazirda aktif olan teklifleri dondurur.";

	// CustOrdItemController
	public static final String CUST_ORD_ITEM_TAG_NAME = "Cust Ord Item";

	public static final String CUST_ORD_ITEM_TAG_DESCRIPTION =
			"WAIT durumundaki siparisin sepetine item ekleme/cikarma";

	public static final String ADD_ITEM_SUMMARY = "Sepete item ekler";

	public static final String ADD_ITEM_DESCRIPTION = "WAIT durumundaki siparise yeni bir teklif ekler.";

	public static final String REMOVE_ITEM_SUMMARY = "Sepetten item cikarir";

	public static final String REMOVE_ITEM_DESCRIPTION = "WAIT durumundaki siparisten tek bir item'i siler.";

	private SwaggerText() {
	}
}
