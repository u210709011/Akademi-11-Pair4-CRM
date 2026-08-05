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

        if (response.statusCode() != 200) {
            throw new TestDataSetupException(String.format(
                    "API login basarisiz (HTTP %d). Kullanici='%s', adres='%s'. "
                            + "Ortamin ayakta ve kimlik bilgilerinin dogru oldugunu kontrol edin. Yanit: %s",
                    response.statusCode(), config.username(), config.apiBaseUrl(),
                    response.getBody().asString()));
        }

        cachedAccessToken = response.jsonPath().getString("accessToken");
        cachedRefreshToken = response.jsonPath().getString("refreshToken");

        if (cachedAccessToken == null || cachedAccessToken.isBlank()) {
            throw new TestDataSetupException("Login yaniti accessToken icermiyor: " + response.getBody().asString());
        }

        log.info("API token alindi (kullanici={}, gecerlilik={} sn)",
                config.username(), response.jsonPath().getString("expiresIn"));
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
