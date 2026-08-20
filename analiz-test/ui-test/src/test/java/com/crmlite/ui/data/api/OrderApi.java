package com.crmlite.ui.data.api;

import com.crmlite.ui.core.exceptions.TestDataSetupException;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

/**
 * Siparis uclari — FR-009'un "fatura hesabina bagli urun" on kosulunu kurmak icin.
 *
 * <p>Urun dogrudan olusturulamaz; siparis akisinin tamami islemek zorundadir:
 * <ol>
 *   <li>{@code POST /orders} — siparisi WAIT durumunda acar, urun teklifini kaleme baglar</li>
 *   <li>{@code PUT /orders/{id}/configuration} — servis adresini yazar</li>
 *   <li>{@code POST /orders/{id}/finish} — siparisi tamamlar; <b>urun ancak burada olusur</b>
 *       ve hesaba baglanir ({@code prodId} dolar)</li>
 * </ol>
 * Yapilandirma adimi atlanirsa finish {@code "Order N has no service address yet."} ile 400 doner.
 *
 * <p><b>Not:</b> bu akis 07.08.2026'ya kadar tamamen kirikti — order-service'in product-service'e
 * giden Feign yolunda yazim hatasi vardi ({@code product-procutOfferings}) ve her siparis
 * olusturma 500 donuyordu. Duzeltme sonrasi dogrulandi.
 */
public final class OrderApi {

    private static final String ORDERS = "/api/v1/orders";
    private static final String CONFIGURATION = "/api/v1/orders/{custOrdId}/configuration";
    private static final String FINISH = "/api/v1/orders/{custOrdId}/finish";
    private static final String BY_ACCOUNT = "/api/v1/orders/by-account";

    /** Seed'deki ilk urun teklifi — "Home Fiber 200Mbps". */
    public static final long PRODUCT_OFFERING_HOME_FIBER = 1L;

    private OrderApi() {
    }

    /**
     * Hesaba bagli <b>tamamlanmis</b> bir urun olusturur ve urun adini dondurur.
     *
     * @param custId    musteri
     * @param custAcctId <b>fatura</b> hesabi (BILL_ACCT) — varsayilan CUST_ACCT degil
     * @param addressId  servis adresi; musterinin var olan bir adresi olmalidir
     */
    public static String createFinishedProduct(long custId, long custAcctId, long addressId) {
        long orderId = createOrder(custId, custAcctId);
        long itemId = firstItemId(orderId);
        saveConfiguration(orderId, itemId, addressId);
        return finish(orderId);
    }

    private static long createOrder(long custId, long custAcctId) {
        Response response = ApiClient.authenticated()
                .body(Map.of(
                        "custId", custId,
                        "custAcctId", custAcctId,
                        "items", List.of(Map.of("prodOfrId", PRODUCT_OFFERING_HOME_FIBER))))
                .post(ORDERS);

        if (response.statusCode() != 201 && response.statusCode() != 200) {
            throw new TestDataSetupException(String.format(
                    "Siparis olusturulamadi (custId=%d, custAcctId=%d, HTTP %d). Yanit: %s",
                    custId, custAcctId, response.statusCode(), response.getBody().asString()));
        }
        return response.jsonPath().getLong("custOrdId");
    }

    private static long firstItemId(long orderId) {
        Response response = ApiClient.authenticated()
                .pathParam("custOrdId", orderId)
                .get(ORDERS + "/{custOrdId}");

        if (response.statusCode() != 200) {
            throw new TestDataSetupException(String.format(
                    "Siparis okunamadi (custOrdId=%d, HTTP %d)", orderId, response.statusCode()));
        }
        // getLong ilkel tip dondurdugu icin null kontrolu ise yaramaz; kalemin varligi
        // liste uzerinden dogrulanir.
        List<Object> items = response.jsonPath().getList("items");
        if (items == null || items.isEmpty()) {
            throw new TestDataSetupException(String.format(
                    "Siparis kalemi bulunamadi (custOrdId=%d). Yanit: %s",
                    orderId, response.getBody().asString()));
        }
        return response.jsonPath().getLong("items[0].custOrdItemId");
    }

    private static void saveConfiguration(long orderId, long itemId, long addressId) {
        Response response = ApiClient.authenticated()
                .pathParam("custOrdId", orderId)
                .body(Map.of(
                        "items", List.of(Map.of("custOrdItemId", itemId, "charVals", List.of())),
                        "addressId", addressId))
                .put(CONFIGURATION);

        if (response.statusCode() != 200) {
            throw new TestDataSetupException(String.format(
                    "Siparis yapilandirilamadi (custOrdId=%d, addressId=%d, HTTP %d). Yanit: %s",
                    orderId, addressId, response.statusCode(), response.getBody().asString()));
        }
    }

    /** Siparisi tamamlar ve olusan urunun adini dondurur. */
    private static String finish(long orderId) {
        Response response = ApiClient.authenticated()
                .pathParam("custOrdId", orderId)
                .post(FINISH);

        if (response.statusCode() != 200) {
            throw new TestDataSetupException(String.format(
                    "Siparis tamamlanamadi (custOrdId=%d, HTTP %d). Yanit: %s",
                    orderId, response.statusCode(), response.getBody().asString()));
        }
        String productName = response.jsonPath().getString("items[0].prodName");
        if (productName == null) {
            throw new TestDataSetupException(String.format(
                    "Siparis tamamlandi ama urun olusmadi (custOrdId=%d). Yanit: %s",
                    orderId, response.getBody().asString()));
        }
        return productName;
    }

    /** Hesaba bagli urunler — arayuzun urun tablosunu doldurdugu ucun aynisi. */
    public static List<Map<String, Object>> productsOnAccount(long custAcctId) {
        Response response = ApiClient.authenticated()
                .queryParam("custAcctId", custAcctId)
                .get(BY_ACCOUNT);

        if (response.statusCode() != 200) {
            throw new TestDataSetupException(String.format(
                    "Hesabin urunleri alinamadi (custAcctId=%d, HTTP %d)", custAcctId, response.statusCode()));
        }
        return response.as(new TypeRef<List<Map<String, Object>>>() {
        });
    }
}
