package com.etiya.crm.contactinfoservice.constants;

/** Swagger/OpenAPI @Tag/@Operation/@Parameter metinleri; annotasyonlarda literal metin kullanilmaz. */
public final class SwaggerText {

	// Common (birden fazla controller'da aynen kullanilan metinler)
	public static final String DATA_TYPE_ID_PARAM_DESCRIPTION =
			"Kaydin sahibinin tipini belirten TYPE_VALUE id'si (ornegin musteri icin 12=CUST). rowId ile birlikte sahibi tanimlar.";

	// AddressController
	public static final String ADDRESS_TAG_NAME = "Addresses";

	public static final String ADDRESS_TAG_DESCRIPTION =
			"Adres (ADDR) CRUD - polimorfik, rowId+dataTypeId ile sahiplenilir";

	public static final String GET_ALL_ADDRESSES_SUMMARY = "Adresleri listele";

	public static final String GET_ALL_ADDRESSES_DESCRIPTION =
			"rowId VE dataTypeId ikisi de verilirse sadece o sahibe ait adresler doner "
					+ "(ornegin bir musterinin tum adresleri); ikisi de verilmezse TUM adresler doner.";

	public static final String ADDRESS_ROW_ID_PARAM_DESCRIPTION =
			"Adresin sahibinin id'si (ornegin custId). dataTypeId ile birlikte kullanilir.";

	public static final String GET_ADDRESS_BY_ID_SUMMARY = "Adresi id ile getir";

	public static final String CREATE_ADDRESS_SUMMARY = "Yeni adres ekle";

	public static final String CREATE_ADDRESS_DESCRIPTION =
			"primary=true gonderilirse ayni rowId+dataTypeId'ye ait diger adreslerin "
					+ "primary'si otomatik false yapilir (tek primary kurali).";

	public static final String UPDATE_ADDRESS_SUMMARY = "Var olan adresi guncelle (primary yapma dahil)";

	public static final String DELETE_ADDRESS_SUMMARY = "Adresi sil (soft-delete)";

	// ContactMediumController
	public static final String CONTACT_MEDIUM_TAG_NAME = "Contact Mediums";

	public static final String CONTACT_MEDIUM_TAG_DESCRIPTION =
			"Iletisim (CNTC_MEDIUM) CRUD + onboarding composite create/delete";

	public static final String CREATE_CONTACT_SUMMARY =
			"[Onboarding] Musterinin tum adres+contact medium'larini tek transaction'da olustur";

	public static final String CREATE_CONTACT_DESCRIPTION =
			"Musteri onboarding akisinda kullanilan composite uc nokta - custId, dataTypeId, adres "
					+ "listesi ve contact medium listesi alip hepsini tek transaction'da olusturur. "
					+ "Tekil bir kayit eklemek icin POST /api/v1/addresses veya POST /api/v1/contact-mediums/single kullanin.";

	public static final String DELETE_BY_CUSTOMER_ID_SUMMARY =
			"[Onboarding compensation] Musterinin tum adres+contact medium'larini deaktive et";

	public static final String DELETE_BY_CUSTOMER_ID_DESCRIPTION =
			"Onboarding basarisiz olup geri alinirken musterinin tum adres ve contact medium kayitlarini deaktive eder.";

	public static final String GET_ALL_CONTACT_MEDIUMS_SUMMARY = "Contact medium'lari listele";

	public static final String GET_ALL_CONTACT_MEDIUMS_DESCRIPTION =
			"rowId VE dataTypeId ikisi de verilirse sadece o sahibe ait kayitlar doner; "
					+ "ikisi de verilmezse TUM kayitlar doner.";

	public static final String CONTACT_MEDIUM_ROW_ID_PARAM_DESCRIPTION =
			"Kaydin sahibinin id'si (ornegin custId). dataTypeId ile birlikte kullanilir.";

	public static final String GET_CONTACT_MEDIUM_BY_ID_SUMMARY = "Contact medium'u id ile getir";

	public static final String ADD_CONTACT_MEDIUM_SUMMARY = "Yeni tekil contact medium ekle";

	public static final String ADD_CONTACT_MEDIUM_DESCRIPTION = "Tek bir contact medium kaydi ekler.";

	public static final String UPDATE_CONTACT_MEDIUM_SUMMARY = "Var olan contact medium'u guncelle";

	public static final String DELETE_CONTACT_MEDIUM_SUMMARY = "Contact medium'u sil (soft-delete)";

	// OpenApiConfig
	public static final String OPENAPI_TITLE = "Contact Info Service API";
	public static final String OPENAPI_DESCRIPTION =
			"Adres (ADDR) ve iletisim (CNTC_MEDIUM) kayitlarinin CRUD'u. Her iki tablo da "
					+ "polimorfiktir: bir kayit rowId+dataTypeId ciftiyle \"kime ait oldugunu\" belirtir "
					+ "(bugun icin tek DATA_TYPE=CUST/102 - musteri). Bu servis rowId'nin bir customer, "
					+ "party ya da baska bir sey oldugunu bilmez/bilmemelidir; customer'a ozel kurallar "
					+ "(max 5 adres, tek primary secimi haric - o burada uygulanir) customer-service'te "
					+ "uygulanir.";
	public static final String OPENAPI_GATEWAY_SERVER_DESCRIPTION = "API Gateway uzerinden (onerilen)";
	public static final String OPENAPI_DIRECT_SERVER_DESCRIPTION = "Bu servisin kendi adresi (gateway'i atlar)";

	private SwaggerText() {
	}
}
