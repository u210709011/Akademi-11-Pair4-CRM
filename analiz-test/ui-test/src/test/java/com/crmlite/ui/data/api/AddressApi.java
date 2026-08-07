package com.crmlite.ui.data.api;

import com.crmlite.ui.core.exceptions.TestDataSetupException;
import com.crmlite.ui.data.model.AddressInfo;
import com.crmlite.ui.data.model.AddressResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;

import java.util.List;

/**
 * Adres uclari — FR-005 on kosullarini kurmak icin.
 *
 * <p>Ozellikle "5 adres limiti" (ACC-005) ve "birincil adres" (ACC-006/007)
 * senaryolarinda kritik: bu on kosullari UI'dan kurmak her adres icin bir modal
 * acmayi gerektirir ve testi hem yavaslatir hem kirilganlastirir.
 */
public final class AddressApi {

    private static final String ADDRESSES = "/api/v1/customers/{custId}/addresses";
    private static final String ADDRESS = "/api/v1/customers/{custId}/addresses/{addressId}";

    private AddressApi() {
    }

    public static List<AddressResponse> list(long custId) {
        Response response = ApiClient.authenticated()
                .pathParam("custId", custId)
                .get(ADDRESSES);

        if (response.statusCode() != 200) {
            throw new TestDataSetupException(String.format(
                    "Adres listesi alinamadi (custId=%d, HTTP %d)", custId, response.statusCode()));
        }
        return response.as(new TypeRef<List<AddressResponse>>() {
        });
    }

    public static AddressResponse create(long custId, AddressInfo address) {
        Response response = ApiClient.authenticated()
                .pathParam("custId", custId)
                .body(address)
                .post(ADDRESSES);

        if (response.statusCode() != 201 && response.statusCode() != 200) {
            throw new TestDataSetupException(String.format(
                    "Adres eklenemedi (custId=%d, HTTP %d). Yanit: %s",
                    custId, response.statusCode(), response.getBody().asString()));
        }
        return response.as(AddressResponse.class);
    }

    /** Musterinin adres sayisini hedeflenen degere tamamlar (limit testleri icin). */
    public static void ensureAddressCount(long custId, int targetCount) {
        int current = list(custId).size();
        for (int i = current; i < targetCount; i++) {
            create(custId, new AddressInfo(
                    AddressInfo.CITY_ANKARA,
                    "Hazirlik Sokak " + i,
                    "No:" + (i + 1),
                    "Ek adres " + i,
                    false));
        }
    }

    public static AddressResponse update(long custId, long addressId, AddressInfo address) {
        Response response = ApiClient.authenticated()
                .pathParam("custId", custId)
                .pathParam("addressId", addressId)
                .body(address)
                .put(ADDRESS);

        if (response.statusCode() != 200) {
            throw new TestDataSetupException(String.format(
                    "Adres guncellenemedi (custId=%d, addressId=%d, HTTP %d)",
                    custId, addressId, response.statusCode()));
        }
        return response.as(AddressResponse.class);
    }

    /** Adresi birincil yapar. */
    public static void setPrimary(long custId, long addressId) {
        AddressResponse current = list(custId).stream()
                .filter(a -> a.id() != null && a.id() == addressId)
                .findFirst()
                .orElseThrow(() -> new TestDataSetupException(
                        "Adres bulunamadi (custId=" + custId + ", addressId=" + addressId + ")"));

        update(custId, addressId, new AddressInfo(
                current.cityId(), current.streetName(), current.houseName(), current.addrDesc(), true));
    }

    /** Silme denemesinin HTTP kodu — 409 beklenen bir sonuc olabilir (ACC-009/011). */
    public static int deleteReturningStatus(long custId, long addressId) {
        return ApiClient.authenticated()
                .pathParam("custId", custId)
                .pathParam("addressId", addressId)
                .delete(ADDRESS)
                .statusCode();
    }

    /**
     * Sunucuda tam olarak <b>bir</b> birincil adres kalana kadar bekler.
     *
     * <p><b>Bu bir assertion degil, senkronizasyon noktasidir.</b> UI'dan "Set as Primary"
     * yapildiginda eski adresin bayragi aninda kalkmiyor; bu araliktaki bir
     * {@code GET /addresses} <b>iki birincil adres</b> dondurebiliyor ve arayuz iki karti
     * da "Primary" rozetiyle cizip testi haksiz yere kiriyor (bkz. README urun bulgusu).
     *
     * <p>API dogrudan cagrildiginda ayni sorun olusmuyor ({@code setPrimary} senkron
     * donuyor); yaris yalnizca UI akisinda, PUT havadayken sayfa yeniden yuklenirse cikiyor.
     *
     * <p>Burada beklenen sey <b>testin dogruladigi sey degil</b>: test, yerlesmis durumda
     * arayuzun dogru adresi birincil gosterdigini dogrular. Bu metot yalnizca "sunucu isini
     * bitirdi mi" sorusuna cevap verir; kalici bir cift-birincil hatasi olusursa
     * {@link TestDataSetupException} ile acikca patlar, sessizce gecmez.
     */
    public static void awaitSinglePrimary(long custId) {
        long deadline = System.currentTimeMillis() + 15_000;
        long primaries = -1;
        while (System.currentTimeMillis() < deadline) {
            primaries = list(custId).stream().filter(AddressResponse::isPrimary).count();
            if (primaries == 1) {
                return;
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new TestDataSetupException("Birincil adres beklenirken kesildi", e);
            }
        }
        throw new TestDataSetupException(String.format(
                "custId=%d icin 15 sn icinde tek birincil adrese ulasilamadi (son deger: %d). "
                        + "Bu kalici bir veri tutarsizligidir, gecici bir yaris degildir.",
                custId, primaries));
    }

    public static AddressResponse primaryAddress(long custId) {
        return list(custId).stream()
                .filter(AddressResponse::isPrimary)
                .findFirst()
                .orElse(null);
    }

    public static AddressResponse firstNonPrimaryAddress(long custId) {
        return list(custId).stream()
                .filter(address -> !address.isPrimary())
                .findFirst()
                .orElse(null);
    }
}
