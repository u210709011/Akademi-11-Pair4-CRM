package com.etiya.crm.customerservice.constants;

/**
 * Swagger/OpenAPI ({@code @Tag}, {@code @Operation}, {@code @Parameter}) icin uzun/coklu-satir
 * literal metinler burada tutulur - annotation'lar kisa, okunabilir tek satirlik referanslara
 * indirgenir (ör. {@code @Operation(summary = SwaggerText.ADD_ADDRESS_SUMMARY, ...)}).
 * Java annotation attribute'lari compile-time constant gerektirdigi icin bu duz bir Java
 * sinifidir - messages*.properties/MessageKeys (runtime, Accept-Language'e gore cozulen
 * kullanici hata mesajlari) ile karistirilmamalidir.
 */
public final class SwaggerText {

	// Shared (birden fazla controller'da birebir ayni metin)
	public static final String CUSTOMER_TAG_NAME = "Customers";
	public static final String PAGE_PARAM_DESCRIPTION = "Sayfa numarasi (0'dan baslar)";
	public static final String SIZE_PARAM_DESCRIPTION = "Sayfa basina kayit sayisi";

	// CustomerController
	public static final String CUSTOMER_TAG_DESCRIPTION = "Musteri onboarding, arama ve yasam dongusu yonetimi";

	public static final String VERIFY_IDENTITY_SUMMARY = "Kimlik dogrulama (KPS) + TC no tekillik kontrolu";
	public static final String VERIFY_IDENTITY_DESCRIPTION =
			"DB'ye hicbir sey yazmaz, sadece dogrular. Basarisiz olursa 422 (kimlik dogrulanamadi) "
					+ "veya 409 (TC no zaten kayitli) doner.";

	public static final String ONBOARD_CUSTOMER_SUMMARY = "Yeni musteri olustur (onboarding)";
	public static final String ONBOARD_CUSTOMER_DESCRIPTION =
			"Tek istekte parti (kisi+rol), musteri (+ otomatik hesap) ve iletisim bilgilerini olusturur. "
					+ "Bir adim basarisiz olursa onceki adimlar otomatik geri alinir.";

	public static final String GET_CUSTOMER_BY_ID_SUMMARY = "Musteriyi id ile getir";

	public static final String SEARCH_CUSTOMER_SUMMARY = "Musteri ara";
	public static final String SEARCH_CUSTOMER_DESCRIPTION =
			"firstName/lastName ikisi birlikte verilirse AND ile tek bir grup olusturur; bu grup ile "
					+ "tcNo/acctNo/custId birbirine ve isim grubuna her zaman OR ile baglanir (ör. hem "
					+ "ad-soyad hem tcNo verilirse, ada-soyada UYAN VEYA o tcNo'ya sahip musteriler doner). "
					+ "Hicbir parametre verilmezse tum aktif musteriler doner; soft-delete edilmis "
					+ "musteriler sonuca dahil olmaz. Varsayilan sayfa boyutu 10'dur.";
	public static final String SEARCH_FIRST_NAME_PARAM_DESCRIPTION = "Ad (kismi/prefix eslesme)";
	public static final String SEARCH_LAST_NAME_PARAM_DESCRIPTION = "Soyad (kismi/prefix eslesme)";
	public static final String SEARCH_TC_NO_PARAM_DESCRIPTION = "T.C. Kimlik No (tam eslesme)";
	public static final String SEARCH_ACCT_NO_PARAM_DESCRIPTION = "Hesap no (tam eslesme), sadece rakam.";
	public static final String SEARCH_CUST_ID_PARAM_DESCRIPTION =
			"Musteri no (tam eslesme), CUST-{custId} onekindeki sayisal kisim.";
	public static final String SEARCH_GSM_PARAM_DESCRIPTION =
			"GSM no (tam eslesme), basinda ulke kodu/sifir olmadan rakamlar.";
	public static final String SEARCH_SORT_BY_PARAM_DESCRIPTION =
			"Siralama alani - custId/firstName/middleName/lastName/tcNo/role disinda "
					+ "bir deger verilirse ya da hic verilmezse siralama uygulanmaz.";
	public static final String SEARCH_SORT_DIR_PARAM_DESCRIPTION = "Siralama yonu";

	public static final String SOFT_DELETE_CUSTOMER_SUMMARY = "Musteriyi sil (soft-delete)";
	public static final String SOFT_DELETE_CUSTOMER_DESCRIPTION =
			"Fiziksel silme yapilmaz; musteri ve hesaplari pasife alinir ve arama sonuclarindan cikar.";

	// CustomerAddressController
	public static final String CUSTOMER_ADDRESS_TAG_DESCRIPTION = "Musteri adres yonetimi";

	public static final String GET_ADDRESSES_SUMMARY = "Musterinin tum adreslerini listele";
	public static final String GET_ADDRESSES_DESCRIPTION = "Musterinin en fazla 5 adresini dondurur.";

	public static final String ADD_ADDRESS_SUMMARY = "Musteriye yeni adres ekle";
	public static final String ADD_ADDRESS_DESCRIPTION =
			"Musteri basina en fazla 5 adres eklenebilir; 6. eklemede 409 doner.";

	public static final String UPDATE_ADDRESS_SUMMARY = "Var olan bir adresi guncelle (primary yapma dahil)";
	public static final String UPDATE_ADDRESS_DESCRIPTION =
			"addressId'nin bu custId'ye ait oldugu dogrulanir; baska musterinin adresi 404 doner.";
	public static final String UPDATE_ADDRESS_ADDRESS_ID_PARAM_DESCRIPTION =
			"Guncellenecek adresin id'si (GET .../addresses cevabindaki 'id')";

	public static final String DELETE_ADDRESS_SUMMARY = "Adresi sil";
	public static final String DELETE_ADDRESS_DESCRIPTION =
			"Birincil adres silinemez (409). Bir fatura hesabina bagli adres de silinemez (409) - "
					+ "once ilgili hesabin adresi degistirilmelidir. Baska musterinin adresi 404 doner.";
	public static final String DELETE_ADDRESS_ADDRESS_ID_PARAM_DESCRIPTION =
			"Silinecek adresin id'si (GET .../addresses cevabindaki 'id')";

	// CustomerContactController
	public static final String CUSTOMER_CONTACT_TAG_DESCRIPTION = "Musteri iletisim bilgisi yonetimi";

	public static final String GET_CONTACT_SUMMARY = "Contact bilgisini getir";
	public static final String GET_CONTACT_DESCRIPTION = "Musteri basina tek contact bilgisi doner.";

	public static final String UPDATE_CONTACT_SUMMARY = "Contact bilgisini guncelle";
	public static final String UPDATE_CONTACT_DESCRIPTION =
			"email/mobilePhone her zaman zorunlu; homePhone/fax bos gonderilirse mevcut kayit "
					+ "varsa dokunulmaz, yoksa olusturulmaz.";

	// CustomerAccountController
	public static final String CUSTOMER_ACCOUNT_TAG_DESCRIPTION = "Musteri fatura hesabi yonetimi";

	public static final String GET_ACCOUNTS_SUMMARY = "Musterinin hesaplarini listele";
	public static final String GET_ACCOUNTS_DESCRIPTION =
			"Onboarding'de otomatik acilan varsayilan hesap ve sonradan eklenen billing "
					+ "account'lari listeler. Varsayilan sayfa boyutu 5.";

	public static final String CREATE_BILLING_ACCOUNT_SUMMARY = "Musteriye yeni billing account ekle";
	public static final String CREATE_BILLING_ACCOUNT_DESCRIPTION =
			"addressId (var olan adres) veya newAddress (yeni adres) alanlarindan tam olarak biri doldurulmalidir.";

	public static final String UPDATE_BILLING_ACCOUNT_SUMMARY = "Billing account guncelle";
	public static final String UPDATE_BILLING_ACCOUNT_DESCRIPTION =
			"Sadece hesap adi/aciklamasi/adresi guncellenebilir - accountNo ve accountTpId "
					+ "degistirilemez. addressId veya newAddress alanlarindan tam olarak biri "
					+ "doldurulmalidir. Baska musterinin hesabi 404 doner.";
	public static final String UPDATE_BILLING_ACCOUNT_ACCOUNT_ID_PARAM_DESCRIPTION = "Guncellenecek hesabin id'si";

	public static final String UPDATE_BILLING_ACCOUNT_STATUS_SUMMARY =
			"Billing account aktiflik durumunu degistir (ACTIVE<->PASSIVE)";
	public static final String UPDATE_BILLING_ACCOUNT_STATUS_DESCRIPTION =
			"Soft-delete degildir. Varsayilan hesabin durumu degistirilemez; silinmis (DEL) hesap "
					+ "icin 404 doner. Baska musterinin hesabi 404 doner.";
	public static final String UPDATE_BILLING_ACCOUNT_STATUS_ACCOUNT_ID_PARAM_DESCRIPTION =
			"Durumu degistirilecek hesabin id'si";

	public static final String DELETE_BILLING_ACCOUNT_SUMMARY = "Billing account sil (soft-delete)";
	public static final String DELETE_BILLING_ACCOUNT_DESCRIPTION =
			"Aktif hesap silinemez (409). Pasif hesaba bagli aktif urun varsa da silinemez (409). "
					+ "Baska musterinin hesabi 404 doner.";
	public static final String DELETE_BILLING_ACCOUNT_ACCOUNT_ID_PARAM_DESCRIPTION = "Silinecek hesabin id'si";

	public static final String EXISTS_ACCOUNT_BY_ADDRESS_SUMMARY =
			"Bir adresin herhangi bir hesapta kullanilip kullanilmadigini kontrol et";
	public static final String EXISTS_ACCOUNT_BY_ADDRESS_DESCRIPTION =
			"Bir adresin herhangi bir billing hesabinda kullanilip kullanilmadigini dondurur (custId gerekmez).";

	public static final String GET_ACCOUNTS_BY_ADDRESS_SUMMARY =
			"Bir adrese bagli TUM billing account'lari (sayi + liste) getir";
	public static final String GET_ACCOUNTS_BY_ADDRESS_DESCRIPTION =
			"Bir adrese bagli tum billing account'larin sayisini ve listesini dondurur.";

	// CustomerIndividualController
	public static final String CUSTOMER_INDIVIDUAL_TAG_DESCRIPTION = "Musteri kisisel bilgi yonetimi";

	public static final String GET_INDIVIDUAL_SUMMARY = "Kisisel bilgiyi getir";
	public static final String GET_INDIVIDUAL_DESCRIPTION = "birthDate/nationalId de doner (salt-okunur).";

	public static final String UPDATE_INDIVIDUAL_SUMMARY = "Kisisel bilgiyi guncelle";
	public static final String UPDATE_INDIVIDUAL_DESCRIPTION =
			"nationalId/birthDate de guncellenebilir; nationalId baska bir musteriyle cakisirsa 409 doner.";

	private SwaggerText() {
	}
}
