package com.crmlite.ui.data.api;

import com.crmlite.ui.core.exceptions.TestDataSetupException;
import io.restassured.response.Response;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lookup (GNL_TP) uclari — referans id'lerini <b>calisma zamaninda</b> cozer.
 *
 * <p><b>Neden sabit id yok:</b> 07.08.2026'da sehir id'si seed migration'i degistigi icin
 * 201'den 5'e kaydi. Frontend 201'i sabit kodlu tuttugu ve backend'e yeni bir
 * {@code ensureCityExists} dogrulamasi eklendigi icin adres iceren <b>tum</b> musteri
 * olusturma akisi kirildi. Uygulama tarafi bunu dinamik cozume gecerek duzeltti
 * ({@code core/lookup/lookup.service.ts}); test verisi de ayni yaklasimi izler, aksi halde
 * seed her degistiginde testler sessizce kirilir.
 *
 * <p>Kullanilan uc lookup-service'in tam bu amac icin yazdigi
 * {@code /resolve/{entCodeName}/{shrtCode}} ucudur.
 *
 * <p>Cozulen degerler surec omru boyunca onbellege alinir: id'ler kosum sirasinda degismez
 * ve her on kosul icin ek bir HTTP cagrisi yapmanin anlami yoktur.
 */
public final class LookupApi {

    private static final String RESOLVE = "/api/v1/general-types/resolve/{entCodeName}/{shrtCode}";

    /** {@code entCodeName + "/" + shrtCode} -> id. */
    private static final Map<String, Integer> CACHE = new ConcurrentHashMap<>();

    public static final String GROUP_CITY = "CITY";
    public static final String GROUP_GENDER = "GENDER";
    public static final String ANKARA = "ANKARA";

    private LookupApi() {
    }

    /** Ankara'nin gecerli lookup id'si. Adres kuran her on kosul bunu kullanir. */
    public static int cityAnkara() {
        return resolve(GROUP_CITY, ANKARA);
    }

    /**
     * Cinsiyet id'si. {@code cityId} gibi {@code genderId} de {@code @ExistsInLookupGroup}
     * ile dogrulaniyor (bkz. {@code IndividualInfo.genderId}), o yuzden o da sabitlenmez.
     */
    public static int gender(String shrtCode) {
        return resolve(GROUP_GENDER, shrtCode);
    }

    /**
     * Verilen grup ve kisa koda karsilik gelen lookup id'sini dondurur.
     *
     * @throws TestDataSetupException kayit bulunamazsa — testin belirsiz bir sekilde
     *         kirmizi olmasi yerine on kosulun kurulamadigi acikca soylenir.
     */
    public static int resolve(String entCodeName, String shrtCode) {
        return CACHE.computeIfAbsent(entCodeName + "/" + shrtCode, key -> fetch(entCodeName, shrtCode));
    }

    private static int fetch(String entCodeName, String shrtCode) {
        Response response = ApiClient.authenticated()
                .pathParam("entCodeName", entCodeName)
                .pathParam("shrtCode", shrtCode)
                .get(RESOLVE);

        if (response.statusCode() != 200) {
            throw new TestDataSetupException(String.format(
                    "Lookup cozulemedi: grup=%s kod=%s (HTTP %d). Seed verisi degismis olabilir. Yanit: %s",
                    entCodeName, shrtCode, response.statusCode(), response.getBody().asString()));
        }

        Integer id = response.jsonPath().getInt("gnlTpId");
        if (id == null) {
            throw new TestDataSetupException(String.format(
                    "Lookup yaniti gnlTpId icermiyor: grup=%s kod=%s. Yanit: %s",
                    entCodeName, shrtCode, response.getBody().asString()));
        }
        return id;
    }
}
