package com.crmlite.ui.data.api;

import com.crmlite.ui.core.exceptions.TestDataSetupException;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.data.model.CustomerData;
import com.crmlite.ui.data.model.IndividualInfo;
import com.crmlite.ui.data.model.SearchCriteria;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Musteri uclari — <b>yalnizca test on kosulu kurmak ve dogrulamak icin</b>.
 *
 * <p>Gereksinim dogrulamalari UI'da yapilir; buradaki metotlar assertion icermez,
 * yalnizca veri kurar veya durum okur.
 */
public final class CustomerApi {

    private static final Logger log = LoggerFactory.getLogger(CustomerApi.class);

    private static final String ONBOARDING = "/api/v1/customers/onboarding";
    private static final String CUSTOMER = "/api/v1/customers/{custId}";
    private static final String INDIVIDUAL = "/api/v1/customers/{custId}/individual";
    private static final String SEARCH = "/api/v1/customers/search";

    private CustomerApi() {
    }

    /**
     * Yeni musteri olusturur ({@code POST /api/v1/customers/onboarding}).
     * Yanit: {@code custId}, {@code partyRoleId} ve otomatik acilan 223 tipi hesap.
     */
    public static CreatedCustomer onboard(CustomerData data) {
        Response response = ApiClient.authenticated()
                .body(data.forOnboarding())
                .post(ONBOARDING);

        if (response.statusCode() != 201) {
            throw new TestDataSetupException(String.format(
                    "Musteri olusturulamadi (HTTP %d). Yanit: %s",
                    response.statusCode(), response.getBody().asString()));
        }

        Long custId = response.jsonPath().getLong("custId");
        if (custId == null) {
            throw new TestDataSetupException("Onboarding yaniti custId icermiyor: " + response.getBody().asString());
        }

        CreatedCustomer created = new CreatedCustomer(
                custId,
                response.jsonPath().getLong("partyRoleId"),
                response.jsonPath().getString("accounts[0].accountNo"),
                response.jsonPath().getObject("accounts[0].accountTpId", Integer.class),
                data);

        TestDataCleaner.track(created);
        log.info("Test musterisi olusturuldu | custId={} | natId={}", custId, data.individual().nationalId());
        return created;
    }

    /** Demografik bilgileri okur ({@code GET .../individual}). */
    public static IndividualInfo getIndividual(long custId) {
        Response response = ApiClient.authenticated()
                .pathParam("custId", custId)
                .get(INDIVIDUAL);

        if (response.statusCode() != 200) {
            throw new TestDataSetupException(String.format(
                    "Musteri demografik bilgisi okunamadi (custId=%d, HTTP %d)",
                    custId, response.statusCode()));
        }
        return response.as(IndividualInfo.class);
    }

    /** Demografik bilgileri gunceller ({@code PUT .../individual}). */
    public static void updateIndividual(long custId, IndividualInfo individual) {
        Response response = ApiClient.authenticated()
                .pathParam("custId", custId)
                .body(individual)
                .put(INDIVIDUAL);

        if (response.statusCode() != 200) {
            throw new TestDataSetupException(String.format(
                    "Musteri guncellenemedi (custId=%d, HTTP %d). Yanit: %s",
                    custId, response.statusCode(), response.getBody().asString()));
        }
    }

    /** Musteri detayini dondurur; bulunamazsa {@code null}. */
    public static Map<String, Object> findCustomer(long custId) {
        Response response = ApiClient.authenticated()
                .pathParam("custId", custId)
                .get(CUSTOMER);
        return response.statusCode() == 200 ? response.jsonPath().getMap("$") : null;
    }

    public static boolean exists(long custId) {
        return findCustomer(custId) != null;
    }

    /** Arama sonucundaki {@code content} dizisini dondurur. */
    public static List<Map<String, Object>> search(SearchCriteria criteria) {
        Response response = ApiClient.authenticated()
                .queryParams(Map.copyOf(criteria.toQueryParams()))
                .get(SEARCH);

        if (response.statusCode() != 200) {
            throw new TestDataSetupException(String.format(
                    "Arama basarisiz (HTTP %d). Yanit: %s",
                    response.statusCode(), response.getBody().asString()));
        }
        return response.jsonPath().getList("content");
    }

    public static int countMatching(SearchCriteria criteria) {
        return search(criteria).size();
    }

    /**
     * Musteriyi soft-delete eder. Basariliysa {@code true}.
     *
     * <p>Fatura hesabi olan musteriler silinemez (409) — bu bir hata degil,
     * bilinen bir urun kisitidir; {@link TestDataCleaner} bunu "artik" olarak raporlar.
     */
    public static boolean delete(long custId) {
        Response response = ApiClient.authenticated()
                .pathParam("custId", custId)
                .delete(CUSTOMER);
        return response.statusCode() == 204 || response.statusCode() == 200;
    }

    /** Silme denemesinin HTTP kodunu dondurur (temizlik raporu icin). */
    public static int deleteReturningStatus(long custId) {
        return ApiClient.authenticated()
                .pathParam("custId", custId)
                .delete(CUSTOMER)
                .statusCode();
    }
}
