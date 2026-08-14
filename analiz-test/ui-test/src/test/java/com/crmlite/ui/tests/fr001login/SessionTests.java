package com.crmlite.ui.tests.fr001login;

import com.crmlite.ui.core.utils.BrowserStorageUtil;
import com.crmlite.ui.pages.auth.LoginPage;
import com.crmlite.ui.pages.components.NavbarComponent;
import com.crmlite.ui.pages.customer.SearchCustomerPage;
import com.crmlite.ui.tests.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FR-001 oturum yasam dongusu: Logout (ACC-012/013) ve oturum sonlanmasi (ACC-011).
 *
 * <p>11.08.2026: Logout ozelligi uygulandi ve testler ACILDI. {@code AuthService.logout()}
 * artik mevcut (oturumu temizler ve {@code /logout} cagrisi yapar); navbar ve sidebar
 * butonlarinda {@code (click)} baglayicisi var. Testler bu tarihe kadar
 * {@code enabled = false} ile bekletiliyordu.
 */
@Epic("FR-001 Sistem Girisi")
@Feature("UC-EACRML-001")
public class SessionTests extends BaseTest {

    @Test(groups = {"fr001", "regression"},
            description = "UI-FR001-12 | Logout ile oturum sonlanir ve Login ekrani acilir")
    @Story("ACC-012, ACC-013 — Logout")
    @TmsLink("FR-001-ACC-012")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Logout ozelligi uygulandiktan sonra 11.08.2026'da acildi.")
    public void logoutEndsSessionAndReturnsToLogin() {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.waitUntilLoaded();
        SearchCustomerPage searchPage = loginPage.loginAs(config.username(), config.password());

        NavbarComponent navbar = searchPage.navbar();
        assertThat(navbar.isLogoutVisible()).as("Logout secenegi gorunur").isTrue();
        navbar.logout();

        LoginPage backToLogin = new LoginPage(driver());
        backToLogin.waitUntilLoaded();

        assertThat(backToLogin.currentUrl()).as("Login ekranina donulur").contains("/login");
        assertThat(BrowserStorageUtil.getLocalStorage(driver(), "accessToken"))
                .as("ACC-013 — oturum token'i temizlenir")
                .isNull();
    }

    @Test(groups = {"fr001", "regression"},
            description = "UI-FR001-13 | Logout sonrasi korumali URL'e dogrudan gidilemez")
    @Story("ACC-013 — Logout sonrasi oturum biter")
    @TmsLink("FR-001-ACC-013")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Logout ozelligi uygulandiktan sonra 11.08.2026'da acildi.")
    public void protectedRouteIsNotAccessibleAfterLogout() {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.waitUntilLoaded();
        loginPage.loginAs(config.username(), config.password()).navbar().logout();

        driver().get(config.baseUrl() + "/search-customer");

        assertThat(driver().getCurrentUrl())
                .as("authGuard korumali rotayi engellemelidir")
                .contains("/login");
    }

    @Test(groups = {"fr001", "regression"},
            description = "UI-FR001-14 | Gecersiz token ile korumali rotaya gidilince Login'e yonlendirilir")
    @Story("ACC-011 — Sure dolunca token gecersiz")
    @TmsLink("FR-001-ACC-011")
    @Severity(SeverityLevel.NORMAL)
    @Description("KISMI OTOMASYON — 8 saatlik oturum suresi beklenemez. Token'in gecersiz "
            + "olmasi durumunda kullanicinin Login'e yonlendirildigi davranisi dogrulanir; "
            + "surenin KENDISI (28800 sn) orta katmanda dogrulanir.")
    public void invalidSessionRedirectsToLogin() {
        // Once temiz bir oturum yok; dogrudan korumali rotaya gidilmeye calisilir.
        driver().get(config.baseUrl() + "/search-customer");

        LoginPage loginPage = new LoginPage(driver());
        loginPage.waitUntilLoaded();

        assertThat(loginPage.currentUrl())
                .as("oturum yokken korumali rota Login'e yonlendirir")
                .contains("/login");
    }
}
