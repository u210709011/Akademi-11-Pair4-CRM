package com.crmlite.ui.data.api;

import com.crmlite.ui.core.config.ConfigLoader;
import com.crmlite.ui.core.config.FrameworkConfig;
import com.crmlite.ui.core.exceptions.TestDataSetupException;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * {@code POST /api/v1/auth/login} — test kullanicisinin token'ini alir.
 *
 * <p>Token suite basina bir kez alinir ve cache'lenir: her testte yeniden login olmak
 * hem yavastir hem de Keycloak brute-force sayacini gereksiz mesgul eder.
 *
 * <p>Token omru 8 saattir (FR-001 ACC-009), suite suresi boyunca gecerli kalir.
 */
public final class AuthApi {

    private static final Logger log = LoggerFactory.getLogger(AuthApi.class);

    private static final String LOGIN_PATH = "/api/v1/auth/login";

    private static volatile String cachedAccessToken;
    private static volatile String cachedRefreshToken;

    private AuthApi() {
    }

    public static String accessToken() {
        if (cachedAccessToken == null) {
            synchronized (AuthApi.class) {
                if (cachedAccessToken == null) {
                    login();
                }
            }
        }
        return cachedAccessToken;
    }

    public static String refreshToken() {
        accessToken();
        return cachedRefreshToken;
    }

    /** Onbellegi temizler — ornegin farkli bir kullanici ile calisilacaksa. */
    public static void reset() {
        synchronized (AuthApi.class) {
            cachedAccessToken = null;
            cachedRefreshToken = null;
        }
    }

    private static void login() {
        FrameworkConfig config = ConfigLoader.get();

        Response response = ApiClient.anonymous()
                .body(Map.of("username", config.username(), "password", config.password()))
                .post(LOGIN_PATH);

        // 20.08.2026: uc, token'i YANIT GOVDESINDE dondurmekten HttpOnly CEREZE gecti
        // ("JWT cookie" degisikligi). Basari kodu da 200 yerine 204 oldu. Iki bicim de
        // desteklenir: ortamlar farkli surumlerde olabilir ve degisiklik geri alinabilir.
        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new TestDataSetupException(String.format(
                    "API login basarisiz (HTTP %d). Kullanici='%s', adres='%s'. "
                            + "Ortamin ayakta ve kimlik bilgilerinin dogru oldugunu kontrol edin. Yanit: %s",
                    response.statusCode(), config.username(), config.apiBaseUrl(),
                    response.getBody().asString()));
        }

        // Once cerez okunur. Govde YALNIZCA cerez yoksa ayristirilir: 204 yanitinin govdesi
        // bostur ve bos govdede jsonPath() JsonPathException firlatir.
        cachedAccessToken = response.getCookie("access_token");
        cachedRefreshToken = response.getCookie("refresh_token");

        if (cachedAccessToken == null || cachedAccessToken.isBlank()) {
            cachedAccessToken = bodyValue(response, "accessToken");
            cachedRefreshToken = bodyValue(response, "refreshToken");
        }

        if (cachedAccessToken == null || cachedAccessToken.isBlank()) {
            throw new TestDataSetupException(String.format(
                    "Login yaniti token icermiyor. HTTP %d, cerezler=%s, govde=%s",
                    response.statusCode(), response.getCookies().keySet(),
                    response.getBody().asString()));
        }

        log.info("API token alindi (kullanici={}, kaynak={})",
                config.username(),
                response.getCookie("access_token") != null ? "cerez" : "govde");
    }

    /**
     * Govdeden alan okur; govde bos veya JSON degilse {@code null} doner.
     *
     * <p>Ayristirma hatasi yutulur ÇUNKU bu yol yalnizca eski (govde tabanli) bicim icin
     * bir yedektir; gercek hata "token bulunamadi" olarak cagiran tarafta bildirilir.
     */
    private static String bodyValue(Response response, String field) {
        String raw = response.getBody() == null ? null : response.getBody().asString();
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return response.jsonPath().getString(field);
        } catch (RuntimeException e) {
            return null;
        }
    }

    /** Ortamin erisilebilir olup olmadigini hizlica kontrol eder. */
    public static boolean isApiReachable() {
        try {
            accessToken();
            return true;
        } catch (RuntimeException e) {
            log.warn("API erisilemiyor: {}", e.getMessage());
            return false;
        }
    }
}
