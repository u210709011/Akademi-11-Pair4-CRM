package com.crmlite.ui.data.api;

import com.crmlite.ui.core.config.ConfigLoader;
import com.crmlite.ui.core.config.FrameworkConfig;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * API katmaninin ortak istek yapilandirmasi.
 *
 * <p><b>Kapsam:</b> Bu katman yalnizca <b>test on kosulu kurmak ve temizlemek</b> icin
 * kullanilir. Gereksinimlerin dogrulanmasi (assertion) her zaman UI uzerinden yapilir;
 * orta katman dogrulamalari Postman/Newman takiminin sorumlulugundadir.
 */
public final class ApiClient {

    private static final Logger log = LoggerFactory.getLogger(ApiClient.class);

    private static final int CONNECT_TIMEOUT_MS = 10_000;
    private static final int SOCKET_TIMEOUT_MS = 30_000;

    private ApiClient() {
    }

    /** Token gerektirmeyen istekler (ornegin login). */
    public static RequestSpecification anonymous() {
        return RestAssured.given()
                .spec(baseSpec())
                .contentType(ContentType.JSON);
    }

    /** Bearer token ile kimlik dogrulanmis istekler. */
    public static RequestSpecification authenticated() {
        return anonymous().header("Authorization", "Bearer " + AuthApi.accessToken());
    }

    private static RequestSpecification baseSpec() {
        FrameworkConfig config = ConfigLoader.get();
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(config.apiBaseUrl())
                .setConfig(RestAssuredConfig.config().httpClient(
                        HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", CONNECT_TIMEOUT_MS)
                                .setParam("http.socket.timeout", SOCKET_TIMEOUT_MS)));

        // API cagrilari yalnizca kurulum icin oldugundan varsayilan olarak sessizdir;
        // sorun ayiklarken -Dapi.log=true ile istek/yanit dokumu acilir.
        if (Boolean.getBoolean("api.log")) {
            builder.addFilter(new RequestLoggingFilter());
            builder.addFilter(new ResponseLoggingFilter());
        }

        return RestAssured.given().spec(builder.build());
    }

    /** Kutuphanenin bu JDK uzerinde yuklenebildigini dogrular (ag erisimi gerektirmez). */
    public static String selfCheck() {
        RequestSpecification spec = anonymousWithoutToken();
        return spec == null ? "spec olusturulamadi" : "ok";
    }

    private static RequestSpecification anonymousWithoutToken() {
        try {
            return anonymous();
        } catch (RuntimeException e) {
            log.error("REST Assured istek yapilandirmasi olusturulamadi", e);
            throw e;
        }
    }
}
