package com.etiya.crm.partyservice.constants;

/** @Tag/@Operation icindeki aciklama metinleri buraya tasindi - annotation'lari okunur tutmak icin. */
public final class SwaggerText {

	// PartyController
	public static final String PARTY_TAG_NAME = "Parties";
	public static final String PARTY_TAG_DESCRIPTION = "Party (kisi/kurum) yonetimi";
	public static final String DELETE_PARTY_SUMMARY = "Party'yi sil (soft-delete)";
	public static final String DELETE_PARTY_DESCRIPTION =
			"Party'ye bagli tum PartyRole'leri ve Individual'i pasiflestirir - fiziksel silme yapilmaz.";

	// IndividualController
	public static final String INDIVIDUAL_TAG_NAME = "Individuals";
	public static final String INDIVIDUAL_TAG_DESCRIPTION = "Gercek kisi (Individual) yonetimi";
	public static final String CREATE_INDIVIDUAL_SUMMARY = "Yeni birey (Individual) olustur";
	public static final String EXISTS_BY_NATIONAL_ID_SUMMARY = "TC kimlik no ile kayitli birey var mi kontrol et";
	public static final String GET_BY_PARTY_ROLE_ID_SUMMARY = "PartyRole id'sine gore birey bilgisini getir";
	public static final String UPDATE_BY_PARTY_ROLE_ID_SUMMARY = "PartyRole id'sine gore birey bilgisini guncelle";
	public static final String UPDATE_BY_PARTY_ROLE_ID_DESCRIPTION =
			"nationalId degistiriliyorsa, degerin baska bir bireyle cakismadigi dogrulanir.";

	private SwaggerText() {
	}
}
