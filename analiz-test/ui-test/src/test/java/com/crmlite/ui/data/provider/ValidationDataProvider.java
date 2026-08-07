package com.crmlite.ui.data.provider;

import com.crmlite.ui.core.utils.JsonReader;
import com.crmlite.ui.data.model.ValidationCase;
import org.testng.annotations.DataProvider;

import java.util.List;

/**
 * FR-001…FR-005 validasyon matrislerini JSON fixture'lardan besleyen DataProvider'lar.
 *
 * <p>Yeni bir sinir degeri eklemek icin <b>kod degistirmek gerekmez</b>; ilgili
 * {@code src/test/resources/testdata/*.json} dosyasina bir satir eklemek yeterlidir.
 */
public final class ValidationDataProvider {

    private static final String LOGIN = "testdata/login-validation.json";
    private static final String SEARCH_FILTER = "testdata/search-filter-validation.json";
    private static final String DEMOGRAPHIC = "testdata/demographic-validation.json";
    private static final String CONTACT = "testdata/contact-validation.json";
    private static final String ADDRESS = "testdata/address-validation.json";
    private static final String CONTACT_MEDIUM = "testdata/contact-medium-validation.json";

    private ValidationDataProvider() {
    }

    /** FR-001 ACC-002 — Login butonu aktiflik matrisi. */
    @DataProvider(name = "loginValidation")
    public static Object[][] loginValidation() {
        return toDataProvider(LOGIN);
    }

    /** FR-002 — arama filtresi format kurallari. */
    @DataProvider(name = "searchFilterValidation")
    public static Object[][] searchFilterValidation() {
        return toDataProvider(SEARCH_FILTER);
    }

    /** FR-003 ACC-002 — demografik zorunlu alan / format matrisi. */
    @DataProvider(name = "demographicValidation")
    public static Object[][] demographicValidation() {
        return toDataProvider(DEMOGRAPHIC);
    }

    /** FR-003 ACC-014 — kontakt format matrisi. */
    @DataProvider(name = "contactValidation")
    public static Object[][] contactValidation() {
        return toDataProvider(CONTACT);
    }

    /** FR-005 ACC-003/ACC-014 — adres zorunlu alan matrisi. */
    @DataProvider(name = "addressValidation")
    public static Object[][] addressValidation() {
        return toDataProvider(ADDRESS);
    }

    /**
     * FR-006 ACC-005 — Contact Medium guncelleme format matrisi.
     *
     * <p>FR-003'un {@code contactValidation} matrisinden ayridir: FR-006 Home Phone'u
     * zorunlu tutmaz ve 10-11 hane kabul eder, FR-003 ise "2 ile baslar, 10 hane" der.
     * Iki kural ayni fixture'da tutulursa hangi ekranin test edildigi belirsizlesir.
     */
    @DataProvider(name = "contactMediumValidation")
    public static Object[][] contactMediumValidation() {
        return toDataProvider(CONTACT_MEDIUM);
    }

    /** Bir fixture dosyasini dogrudan okumak icin (dogrulama testleri). */
    public static List<ValidationCase> read(String classpathResource) {
        return JsonReader.readList(classpathResource, ValidationCase.class);
    }

    public static List<String> allFixtures() {
        return List.of(LOGIN, SEARCH_FILTER, DEMOGRAPHIC, CONTACT, ADDRESS, CONTACT_MEDIUM);
    }

    private static Object[][] toDataProvider(String classpathResource) {
        List<ValidationCase> cases = JsonReader.readList(classpathResource, ValidationCase.class);
        Object[][] rows = new Object[cases.size()][1];
        for (int i = 0; i < cases.size(); i++) {
            rows[i][0] = cases.get(i);
        }
        return rows;
    }
}
