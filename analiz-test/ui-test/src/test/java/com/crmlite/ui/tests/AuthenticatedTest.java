package com.crmlite.ui.tests;

import com.crmlite.ui.core.exceptions.TestDataSetupException;
import com.crmlite.ui.core.utils.BrowserStorageUtil;
import com.crmlite.ui.data.api.AuthApi;
import com.crmlite.ui.pages.customer.CustomerDetailPage;
import com.crmlite.ui.pages.customer.SearchCustomerPage;
import org.openqa.selenium.WebDriver;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;

/**
 * Oturum acik olmasi gereken testlerin atasi (FR-002…FR-005).
 *
 * <p>Her testte UI'dan login olmak yerine, API'den alinan token dogrudan
 * {@code localStorage}'a yazilir. Nedenleri:
 * <ul>
 *   <li>Hiz: test basina ~3-4 saniye kazanilir</li>
 *   <li>Odak: FR-002'nin bir testi FR-001'in login akisi bozuldugu icin kirilmamalidir</li>
 *   <li>Guvenlik: Keycloak brute-force sayaci gereksiz mesgul edilmez</li>
 * </ul>
 *
 * <p>FR-001 testleri bu sinifi <b>kullanmaz</b>; onlar gercek login akisini dogrular.
 *
 * <p>Anahtar adlari front-end ile ayni olmalidir
 * ({@code auth.service.ts}: {@code accessToken} / {@code refreshToken}).
 */
public abstract class AuthenticatedTest extends BaseTest {

    private static final String ACCESS_TOKEN_KEY = "accessToken";
    private static final String REFRESH_TOKEN_KEY = "refreshToken";

    /** Oturum, uygulama acildiktan sonra enjekte edilir. */
    @BeforeMethod(alwaysRun = true)
    public void authenticate() {
        String accessToken;
        String refreshToken;
        try {
            accessToken = AuthApi.accessToken();
            refreshToken = AuthApi.refreshToken();
        } catch (TestDataSetupException e) {
            // Ortam/kimlik sorunu bir urun hatasi degildir; test ATLANIR.
            throw new SkipException("Oturum kurulamadi: " + e.getMessage());
        }

        WebDriver driver = driver();
        BrowserStorageUtil.setLocalStorage(driver, ACCESS_TOKEN_KEY, accessToken);
        if (refreshToken != null) {
            BrowserStorageUtil.setLocalStorage(driver, REFRESH_TOKEN_KEY, refreshToken);
        }
    }

    // --- Gezinme kisayollari ---

    /** Musteri arama ekranini acar. */
    protected SearchCustomerPage openSearchCustomer() {
        driver().get(urlOf("/search-customer"));
        SearchCustomerPage page = new SearchCustomerPage(driver());
        page.waitUntilLoaded();
        return page;
    }

    /** Musteri detay ekranini dogrudan acar (ara adimlari atlar). */
    protected CustomerDetailPage openCustomerDetail(long custId) {
        driver().get(urlOf("/detail-customer/" + custId));
        CustomerDetailPage page = new CustomerDetailPage(driver());
        page.waitUntilLoaded();
        return page;
    }

    protected com.crmlite.ui.pages.customer.create.CreateCustomerPage openCreateCustomer() {
        driver().get(urlOf("/create-customer"));
        com.crmlite.ui.pages.customer.create.CreateCustomerPage page =
                new com.crmlite.ui.pages.customer.create.CreateCustomerPage(driver());
        page.waitUntilLoaded();
        return page;
    }
}
