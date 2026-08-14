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

	// LoginRequest
	public static final String CLIENT_ID_DESCRIPTION =
			"Opsiyonel - hangi Keycloak client'i ile token alinacak. Bos birakilirse "
					+ "varsayilan (8 saat) client kullanilir. Test amacli 30sn'lik token icin 'short-lived' gonderin. "
					+ "Bu ikisi disinda (orn. gercek Keycloak client_id'si) bir deger gonderilirse istek reddedilir.";

	// OpenApiConfig
	public static final String OPENAPI_TITLE = "API Gateway - Auth";
	public static final String OPENAPI_DESCRIPTION =
			"Keycloak (realm: crm) onunde confidential client (crm-client) olarak calisan "
					+ "token endpoint'leri. Diger tum servislerin gercek is API'leri icin sagdaki "
					+ "dropdown'dan ilgili servisi secin - bu sayfa sadece login/refresh/logout icindir.";

	private SwaggerText() {
	}
}
