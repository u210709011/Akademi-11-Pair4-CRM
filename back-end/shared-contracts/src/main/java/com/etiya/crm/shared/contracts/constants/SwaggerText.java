package com.etiya.crm.shared.contracts.constants;

/**
 * Swagger/OpenAPI ({@code @Schema}) icin literal metinler burada tutulur - annotation'lar kisa,
 * okunabilir tek satirlik referanslara indirgenir (ör. {@code @Schema(description =
 * SwaggerText.CITY_ID_DESCRIPTION)}). Java annotation attribute'lari compile-time constant
 * gerektirdigi icin bu duz bir Java sinifidir - messages*.properties/MessageKeys (runtime,
 * Accept-Language'e gore cozulen kullanici hata mesajlari) ile karistirilmamalidir. Sadece
 * {@code description} degerleri buraya tasinir; {@code example} degerleri kisa/temsili oldugu
 * icin literal birakilir.
 */
public final class SwaggerText {

	// Shared (birden fazla DTO'da birebir ayni metin - ayni lookup-service grubu/alani)
	public static final String CITY_ID_DESCRIPTION = "lookup-service CITY grubundaki deger id'si.";
	public static final String STREET_NAME_DESCRIPTION = "Cadde/sokak";
	public static final String HOUSE_NAME_DESCRIPTION = "Bina/kat/daire no";
	public static final String ADDRESS_DESC_DESCRIPTION = "Serbest metin aciklama";
	public static final String ADDRESS_PRIMARY_DESCRIPTION =
			"true ise diger adreslerin primary'si otomatik false yapilir (tek primary kurali).";
	public static final String OWNER_ROW_ID_DESCRIPTION = "Adresin sahibinin id'si (ornegin custId).";
	public static final String DATA_TYPE_ID_LOOKUP_DESCRIPTION = "lookup-service DATA_TYPE grubundaki deger id'si.";
	public static final String CNTC_MEDIUM_TYPE_ID_DESCRIPTION =
			"lookup-service CNTC_MEDIUM_TYPE grubundaki deger id'si (EMAIL=4001, MOBILE_PHONE=4002, "
					+ "HOME_PHONE=4003, FAX=4004).";
	public static final String CONTACT_DATA_DESCRIPTION = "Iletisim verisi (e-posta, telefon no, vb.)";
	public static final String SPEC_ST_ID_DESCRIPTION = "GNL_ST grubundaki durum id'si (gnl_st_id).";

	// address.UpdateAddressRequest / address.CreateAddressRequest
	public static final String UPDATE_ADDRESS_REQUEST_DESCRIPTION = "PUT /api/v1/addresses/{id} istek govdesi.";
	public static final String CREATE_ADDRESS_REQUEST_DESCRIPTION = "POST /api/v1/addresses istek govdesi.";
	public static final String CREATE_ADDRESS_DATA_TYPE_ID_DESCRIPTION =
			"lookup-service TYPE_VALUE tablosundaki polimorfik tip etiketi (musteri icin CUST=12, dinamik cozulur).";

	// address.AddressResponse
	public static final String ADDRESS_RESPONSE_DESCRIPTION = "Adres kaydi.";
	public static final String ADDRESS_ID_DESCRIPTION = "Adresin kendi id'si (guncelleme/silme icin kullanilir).";
	public static final String ADDRESS_RESPONSE_PRIMARY_DESCRIPTION =
			"Musterinin birden fazla adresi olabilir, en fazla biri primary=true olur.";

	// contactmedium.AddressCommand
	public static final String ADDRESS_COMMAND_DESCRIPTION = "Composite create icindeki tek adres.";
	public static final String ADDRESS_COMMAND_PRIMARY_DESCRIPTION =
			"Onboarding'de ilk adres icin true, digerleri icin false gelir.";

	// contactmedium.ContactMediumCommand
	public static final String CONTACT_MEDIUM_COMMAND_DESCRIPTION = "Composite create icindeki tek contact medium.";

	// contactmedium.ContactMediumResponse
	public static final String CONTACT_MEDIUM_RESPONSE_DESCRIPTION = "Contact medium kaydi (e-posta, telefon, vb.).";
	public static final String CONTACT_MEDIUM_RESPONSE_ID_DESCRIPTION =
			"Kaydin kendi id'si (guncelleme/silme icin kullanilir).";
	public static final String CONTACT_MEDIUM_RESPONSE_ROW_ID_DESCRIPTION = "Kaydin sahibinin id'si (ornegin custId).";

	// contactmedium.CreateContactCommand
	public static final String CREATE_CONTACT_COMMAND_DESCRIPTION =
			"Onboarding composite create: bir musterinin tum adreslerini ve contact "
					+ "medium'larini tek transaction'da yazar. Tekil ekleme icin bkz. CreateAddressRequest / CreateContactMediumRequest.";
	public static final String CREATE_CONTACT_COMMAND_CUST_ID_DESCRIPTION =
			"Adreslerin/contact medium'larin ait olacagi customer id.";
	public static final String CREATE_CONTACT_COMMAND_DATA_TYPE_ID_DESCRIPTION =
			"lookup-service TYPE_VALUE tablosundaki CUST etiketi (dinamik cozulur).";
	public static final String CREATE_CONTACT_COMMAND_ADDRESSES_DESCRIPTION =
			"1-5 adet adres; ilk eleman genelde primary olarak isaretlenir.";
	public static final String CREATE_CONTACT_COMMAND_CONTACT_MEDIUMS_DESCRIPTION =
			"Contact medium listesi (email, mobilePhone zorunlu; homePhone, fax opsiyonel).";

	// contactmedium.CreateContactMediumRequest
	public static final String CREATE_CONTACT_MEDIUM_REQUEST_DESCRIPTION = "POST /api/v1/contact-mediums/single istek govdesi.";
	public static final String CREATE_CONTACT_MEDIUM_ROW_ID_DESCRIPTION =
			"Contact medium'un sahibinin id'si (ornegin custId).";
	public static final String CREATE_CONTACT_MEDIUM_DATA_TYPE_ID_DESCRIPTION =
			"lookup-service DATA_TYPE grubundaki deger id'si (musteri icin 102).";

	// contactmedium.UpdateContactMediumRequest
	public static final String UPDATE_CONTACT_MEDIUM_REQUEST_DESCRIPTION = "PUT /api/v1/contact-mediums/{id} istek govdesi.";

	// error.ErrorResponse
	public static final String ERROR_RESPONSE_DESCRIPTION = "Standart hata govdesi.";
	public static final String ERROR_RESPONSE_ERROR_DESCRIPTION = "HTTP status'un reason phrase'i.";
	public static final String ERROR_RESPONSE_PATH_DESCRIPTION = "Hatanin olustugu istek path'i.";
	public static final String ERROR_RESPONSE_VALIDATION_ERRORS_DESCRIPTION =
			"Validasyon hatasi ise alan adi -> hata mesaji eslemesi; aksi halde null.";

	// gnlchar.CreateGnlCharRequest / gnlchar.UpdateGnlCharRequest
	public static final String GNL_CHAR_CREATE_REQUEST_DESCRIPTION = "lookup-service POST /api/v1/characteristics istek govdesi.";
	public static final String GNL_CHAR_UPDATE_REQUEST_DESCRIPTION = "lookup-service PUT /api/v1/characteristics/{id} istek govdesi.";
	public static final String GNL_CHAR_NAME_DESCRIPTION = "Karakteristik adi.";
	public static final String GNL_CHAR_DESCR_DESCRIPTION = "Aciklama.";
	public static final String GNL_CHAR_PRVDR_CLS_DESCRIPTION = "Deger saglayici sinif (opsiyonel).";
	public static final String GNL_CHAR_SHRT_CODE_DESCRIPTION = "Kisa kod.";
	public static final String GNL_CHAR_ACTIVE_DESCRIPTION = "Aktif mi.";

	// gnlchar.GnlCharResponse
	public static final String GNL_CHAR_RESPONSE_DESCRIPTION = "Karakteristik tanimi.";

	// gnlcharval.CreateGnlCharValRequest / gnlcharval.UpdateGnlCharValRequest
	public static final String GNL_CHAR_VAL_CREATE_REQUEST_DESCRIPTION =
			"lookup-service POST /api/v1/characteristic-values istek govdesi.";
	public static final String GNL_CHAR_VAL_UPDATE_REQUEST_DESCRIPTION =
			"lookup-service PUT /api/v1/characteristic-values/{id} istek govdesi.";
	public static final String GNL_CHAR_VAL_CHAR_ID_DESCRIPTION = "Ait oldugu GNL_CHAR id'si.";
	public static final String GNL_CHAR_VAL_DFLT_DESCRIPTION = "Varsayilan deger mi.";
	public static final String GNL_CHAR_VAL_VAL_DESCRIPTION = "Deger metni.";
	public static final String GNL_CHAR_VAL_SHRT_CODE_DESCRIPTION = "Kisa kod.";
	public static final String GNL_CHAR_VAL_SDATE_DESCRIPTION = "Gecerlilik baslangic tarihi.";
	public static final String GNL_CHAR_VAL_EDATE_DESCRIPTION = "Gecerlilik bitis tarihi (opsiyonel).";
	public static final String GNL_CHAR_VAL_ACTIVE_DESCRIPTION = "Aktif mi.";

	// gnlcharval.GnlCharValResponse
	public static final String GNL_CHAR_VAL_RESPONSE_DESCRIPTION = "Karakteristik degeri.";

	// gnlst.CreateGnlStRequest / gnlst.UpdateGnlStRequest
	public static final String GNL_ST_CREATE_REQUEST_DESCRIPTION = "lookup-service POST /api/v1/general-statuses istek govdesi.";
	public static final String GNL_ST_UPDATE_REQUEST_DESCRIPTION = "lookup-service PUT /api/v1/general-statuses/{id} istek govdesi.";
	public static final String GNL_ST_NAME_DESCRIPTION = "Bu degerin gosterim adi.";
	public static final String GNL_ST_DESCR_DESCRIPTION = "Aciklama.";
	public static final String GNL_ST_SHRT_CODE_DESCRIPTION =
			"Bu degerin kendi kisa kodu (ent_code_name ile birlikte benzersiz olmali).";
	public static final String GNL_ST_ACTIVE_DESCRIPTION = "Aktif mi.";
	public static final String GNL_ST_ENT_CODE_NAME_DESCRIPTION = "Grup anahtari - bu degerin ait oldugu grup.";
	public static final String GNL_ST_ENT_NAME_DESCRIPTION = "Pratikte entCodeName ile ayni deger girilir.";

	// gnlst.GnlStResponse
	public static final String GNL_ST_RESPONSE_DESCRIPTION = "Genel durum grubu.";

	// gnltp.CreateGnlTpRequest / gnltp.UpdateGnlTpRequest
	public static final String GNL_TP_CREATE_REQUEST_DESCRIPTION = "lookup-service POST /api/v1/general-types istek govdesi.";
	public static final String GNL_TP_UPDATE_REQUEST_DESCRIPTION = "lookup-service PUT /api/v1/general-types/{id} istek govdesi.";
	public static final String GNL_TP_NAME_DESCRIPTION = "Bu degerin gosterim adi.";
	public static final String GNL_TP_DESCR_DESCRIPTION = "Aciklama.";
	public static final String GNL_TP_SHRT_CODE_DESCRIPTION =
			"Bu degerin kendi kisa kodu (ent_code_name ile birlikte benzersiz olmali).";
	public static final String GNL_TP_ENT_CODE_NAME_DESCRIPTION = "Grup anahtari - bu degerin ait oldugu grup.";
	public static final String GNL_TP_ENT_NAME_DESCRIPTION = "Pratikte entCodeName ile ayni deger girilir.";
	public static final String GNL_TP_ACTIVE_DESCRIPTION = "Aktif mi.";

	// gnltp.GnlTpResponse
	public static final String GNL_TP_RESPONSE_DESCRIPTION = "Genel tip grubu.";

	// rsrcspec.CreateRsrcSpecRequest / rsrcspec.UpdateRsrcSpecRequest
	public static final String RSRC_SPEC_CREATE_REQUEST_DESCRIPTION = "lookup-service POST /api/v1/resource-specs istek govdesi.";
	public static final String RSRC_SPEC_UPDATE_REQUEST_DESCRIPTION = "lookup-service PUT /api/v1/resource-specs/{id} istek govdesi.";
	public static final String RSRC_SPEC_NAME_DESCRIPTION = "Kaynak spec adi.";
	public static final String RSRC_SPEC_DESCR_DESCRIPTION = "Aciklama.";
	public static final String RSRC_SPEC_RSRC_CODE_DESCRIPTION = "Kisa kod.";

	// rsrcspec.RsrcSpecResponse
	public static final String RSRC_SPEC_RESPONSE_DESCRIPTION = "Kaynak spesifikasyonu.";

	// srvcspec.CreateSrvcSpecRequest / srvcspec.UpdateSrvcSpecRequest
	public static final String SRVC_SPEC_CREATE_REQUEST_DESCRIPTION = "lookup-service POST /api/v1/service-specs istek govdesi.";
	public static final String SRVC_SPEC_UPDATE_REQUEST_DESCRIPTION = "lookup-service PUT /api/v1/service-specs/{id} istek govdesi.";
	public static final String SRVC_SPEC_NAME_DESCRIPTION = "Servis spec adi.";
	public static final String SRVC_SPEC_DESCR_DESCRIPTION = "Aciklama.";
	public static final String SRVC_SPEC_SRVC_CODE_DESCRIPTION = "Kisa kod.";

	// srvcspec.SrvcSpecResponse
	public static final String SRVC_SPEC_RESPONSE_DESCRIPTION = "Servis spesifikasyonu.";

	// typevalue.CreateTypeValueRequest
	public static final String CREATE_TYPE_VALUE_REQUEST_DESCRIPTION = "lookup-service POST /api/v1/type-values istek govdesi.";
	public static final String TYPE_VALUE_TABLE_NAME_DESCRIPTION = "Polimorfik sahiplik etiketi verilen gercek is tablosunun adi.";
	public static final String TYPE_VALUE_FIELD_NAME_DESCRIPTION = "Bu tabloya atanan tip etiketi numarasi.";
	public static final String CREATE_TYPE_VALUE_DESCRIPTION_FIELD_DESCRIPTION = "Aciklama.";
	public static final String CREATE_TYPE_VALUE_VALUE_DESCRIPTION = "Kisa kod (opsiyonel).";
	public static final String TYPE_VALUE_USING_MODULE_NAME_DESCRIPTION = "Bu degeri kullanan servis/modul (bilgi amacli).";

	// typevalue.UpdateTypeValueRequest
	public static final String UPDATE_TYPE_VALUE_REQUEST_DESCRIPTION = "lookup-service PUT /api/v1/type-values/{id} istek govdesi.";
	public static final String UPDATE_TYPE_VALUE_DESCRIPTION_FIELD_DESCRIPTION = "Gosterim metni.";
	public static final String UPDATE_TYPE_VALUE_VALUE_DESCRIPTION = "Kisa kod.";

	// typevalue.TypeValueResponse
	public static final String TYPE_VALUE_RESPONSE_DESCRIPTION = "Bir is tablosuna atanmis polimorfik tip etiketi.";

	private SwaggerText() {
	}
}
