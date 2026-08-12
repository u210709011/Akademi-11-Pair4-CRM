package com.etiya.crm.apigateway.constants;

/** @Operation/@Tag/@Parameter icindeki uzun/coklu-satir aciklama metinleri buraya tasindi - annotation'lari okunur tutmak icin. */
public final class SwaggerText {

	// AuthController
	public static final String AUTH_TAG_NAME = "Auth";
	public static final String AUTH_TAG_DESCRIPTION =
			"Keycloak (realm: crm) onunde confidential client olarak calisan token uclari.";

	public static final String LOGIN_SUMMARY = "Kullanici adi/sifre ile giris yap";
	public static final String LOGIN_DESCRIPTION =
			"clientId alani opsiyoneldir. Bos birakilirsa ya da 'default' gonderilirse 8 saat gecerli "
					+ "token doner. 'short-lived' gonderilirse sadece 30 saniye gecerli bir token doner.";

	public static final String REFRESH_SUMMARY = "Refresh token ile yeni access token al";
	public static final String DEFAULT_CLIENT_ONLY_DESCRIPTION = "Her zaman varsayilan client (crm-client) uzerinden calisir.";

	public static final String LOGOUT_SUMMARY = "Oturumu kapat (refresh token'i gecersiz kil)";

	private SwaggerText() {
	}
}
