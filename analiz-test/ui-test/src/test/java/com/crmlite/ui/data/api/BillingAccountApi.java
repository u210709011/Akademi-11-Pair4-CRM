package com.crmlite.ui.data.api;

import com.crmlite.ui.core.exceptions.TestDataSetupException;
import com.crmlite.ui.data.model.BillingAccountResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

/**
 * Fatura hesabi (Billing Account) uclari — FR-007 ve FR-008 on kosullarini kurmak icin.
 *
 * <p><b>Onemli ayrim:</b> onboarding sirasinda her musteriye varsayilan bir {@code CUST_ACCT}
 * hesabi acilir; bu bir <b>fatura hesabi degildir</b>. FR-007 ACC-003'un engelledigi sey
 * {@code BILL_ACCT} tipindeki aktif hesaptir. Yani sade bir musteri silinebilir, ancak
 * bu sinifla bir fatura hesabi eklendikten sonra silinemez.
 * (bkz. {@code BillingAccountBusinessRules.ensureNoActiveBillingAccount})
 *
 * <p>Istek govdesinde {@code addressId} ile {@code newAddress} alanlarindan <b>tam olarak
 * biri</b> doldurulmalidir; ikisi birden ya da hicbiri 400 dondurur.
 */
public final class BillingAccountApi {

    private static final String ACCOUNTS = "/api/v1/customers/{custId}/accounts";

    private BillingAccountApi() {
    }

    public static List<BillingAccountResponse> list(long custId) {
        Response response = ApiClient.authenticated()
                .pathParam("custId", custId)
                .get(ACCOUNTS);

        if (response.statusCode() != 200) {
            throw new TestDataSetupException(String.format(
                    "Hesap listesi alinamadi (custId=%d, HTTP %d)", custId, response.statusCode()));
        }
        return response.as(new TypeRef<List<BillingAccountResponse>>() {
        });
    }

    /**
     * Musterinin var olan bir adresine bagli fatura hesabi olusturur.
     *
     * <p>{@code accountName} backend'de zorunludur ve otomatik turetilmez — bos gonderilirse 400 doner.
     */
    public static BillingAccountResponse create(long custId, long addressId, String accountName) {
        Response response = ApiClient.authenticated()
                .pathParam("custId", custId)
                .body(Map.of(
                        "accountName", accountName,
                        "accountDesc", "UI test fatura hesabi",
                        "addressId", addressId))
                .post(ACCOUNTS);

        if (response.statusCode() != 201 && response.statusCode() != 200) {
            throw new TestDataSetupException(String.format(
                    "Fatura hesabi olusturulamadi (custId=%d, HTTP %d). Yanit: %s",
                    custId, response.statusCode(), response.getBody().asString()));
        }
        return response.as(BillingAccountResponse.class);
    }

    /** Musterinin birincil adresine bagli bir fatura hesabi acar (FR-007 ACC-003 on kosulu). */
    public static BillingAccountResponse createOnPrimaryAddress(long custId) {
        long addressId = AddressApi.primaryAddress(custId).id();
        return create(custId, addressId, "UI Test Billing");
    }
}
