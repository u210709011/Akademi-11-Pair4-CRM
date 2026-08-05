package com.crmlite.ui.tests.fr001login;

import com.crmlite.ui.core.utils.BrowserStorageUtil;
import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.pages.auth.LoginPage;
import com.crmlite.ui.pages.customer.SearchCustomerPage;
import com.crmlite.ui.tests.BaseTest;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FR-001 — Sistem Girisi (UC-EACRML-001) mutlu yol ve temel negatif senaryolar.
 *
 * <p>Bu sinif {@code BaseTest}'i genisletir (AuthenticatedTest'i degil): FR-001'in
 * kendisi login akisini dogruladigi icin oturum enjekte edilmez.
 */
@Epic("FR-001 Sistem Girisi")
@Feature("UC-EACRML-001")
public class LoginTests extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void openLoginPage() {
        loginPage = new LoginPage(driver());
        loginPage.waitUntilLoaded();
    }

    @Test(groups = {"smoke", "fr001"},
            description = "UI-FR001-01 | Login ekrani kullanici adi, sifre ve Login butonuyla acilir")
    @Story("ACC-001 — Giris ekrani gosterilir")
    @TmsLink("FR-001-ACC-001")
    @Severity(SeverityLevel.BLOCKER)
    public void loginScreenIsDisplayed() {
        assertThat(loginPage.isUsernameFieldDisplayed()).as("kullanici adi alani").isTrue();
        assertThat(loginPage.isPasswordFieldDisplayed()).as("sifre alani").isTrue();
        assertThat(loginPage.title()).as("ekran basligi")
                .isEqualTo(ExpectedMessages.get("login.title"));
    }

    @Test(groups = {"smoke", "fr001"},
            description = "UI-FR001-02 | Gecerli bilgilerle giris -> Customer Search'e yonlendirilir")
    @Story("ACC-004, ACC-009, ACC-010 — Basarili giris")
    @TmsLink("FR-001-ACC-004")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Dogrulama basarili olduğunda oturum token'i olusturulur ve kullanici "
            + "Customer Search ekranina yonlendirilir.")
    public void validCredentialsRedirectToCustomerSearch() {
        SearchCustomerPage searchPage = loginPage.loginAs(config.username(), config.password());

        assertThat(searchPage.currentUrl()).as("yonlendirilen rota").contains("/search-customer");
        assertThat(searchPage.isAt()).as("Customer Search ekrani yuklendi").isTrue();

        assertThat(BrowserStorageUtil.getLocalStorage(driver(), "accessToken"))
                .as("ACC-009 — oturum token'i olusturuldu")
                .isNotBlank();
    }

    @Test(groups = {"fr001"},
            description = "UI-FR001-03 | Her iki alan dolunca Login butonu aktiflesir")
    @Story("ACC-002 — Iki alan doluysa Login aktif")
    @TmsLink("FR-001-ACC-002")
    @Severity(SeverityLevel.CRITICAL)
    public void submitBecomesEnabledWhenBothFieldsFilled() {
        assertThat(loginPage.isSubmitDisabled())
                .as("baslangicta (iki alan bos) Login pasif olmalidir").isTrue();

        loginPage.enterUsername(config.username()).enterPassword(config.password());

        assertThat(loginPage.isSubmitEnabled()).as("iki alan dolunca Login aktif").isTrue();
    }

    @Test(groups = {"fr001"},
            description = "UI-FR001-05 | Goz ikonu sifreyi duz metne cevirir, ikinci tikta maskeler")
    @Story("ACC-003 — Sifre goz ikonu")
    @TmsLink("FR-001-ACC-003")
    @Severity(SeverityLevel.NORMAL)
    public void passwordVisibilityToggle() {
        loginPage.enterPassword(config.password());

        assertThat(loginPage.isPasswordMasked()).as("varsayilan olarak maskeli").isTrue();

        loginPage.togglePasswordVisibility();
        loginPage.waitUntilPasswordVisible();
        assertThat(loginPage.isPasswordMasked()).as("ilk tiktan sonra duz metin").isFalse();

        loginPage.togglePasswordVisibility();
        assertThat(loginPage.isPasswordMasked()).as("ikinci tiktan sonra tekrar maskeli").isTrue();
    }

    @Test(groups = {"fr001"},
            description = "UI-FR001-06 | Hatali sifre -> hata mesaji kirmizi renkte gosterilir")
    @Story("ACC-005 — Hatali bilgide mesaj")
    @TmsLink("FR-001-ACC-005")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Mesajin METNI ve RENGI dogrulanir. Mesajin ekrandaki KONUMU gorsel bir "
            + "kriterdir ve otomasyon kapsami disindadir (manuel dogrulanir).")
    public void wrongPasswordShowsErrorMessage() {
        loginPage.loginExpectingFailure(config.username(), "kesinlikle-yanlis-sifre");

        assertThat(loginPage.hasErrorMessage()).as("hata mesaji gorunur").isTrue();
        assertThat(loginPage.errorMessage())
                .isEqualTo(ExpectedMessages.get("login.wrongCredentials"));
        assertThat(isReddish(loginPage.errorMessageColor()))
                .as("ACC-005 — mesaj kirmizi renkte (olculen: %s)", loginPage.errorMessageColor())
                .isTrue();
    }

    @Test(groups = {"fr001"},
            description = "UI-FR001-07 | Hatali kullanici adi -> ayni hata mesaji gosterilir")
    @Story("ACC-005 — Hatali bilgide mesaj")
    @TmsLink("FR-001-ACC-005")
    @Severity(SeverityLevel.NORMAL)
    public void wrongUsernameShowsSameErrorMessage() {
        loginPage.loginExpectingFailure("olmayan-kullanici-adi", config.password());

        assertThat(loginPage.hasErrorMessage()).isTrue();
        assertThat(loginPage.errorMessage())
                .as("kullanici adi mi sifre mi hatali oldugu sizdirilmamalidir")
                .isEqualTo(ExpectedMessages.get("login.wrongCredentials"));
    }

    @Test(groups = {"fr001"},
            description = "UI-FR001-08 | Hatali girişte oturum olusmaz, kullanici Login ekraninda kalir")
    @Story("ACC-007 — Hatali girişte oturum oluşmaz")
    @TmsLink("FR-001-ACC-007")
    @Severity(SeverityLevel.CRITICAL)
    public void failedLoginCreatesNoSession() {
        loginPage.loginExpectingFailure(config.username(), "kesinlikle-yanlis-sifre");

        assertThat(loginPage.hasErrorMessage()).as("hata mesaji beklenir").isTrue();
        assertThat(loginPage.currentUrl()).as("Login ekraninda kalinir").contains("/login");
        assertThat(BrowserStorageUtil.getLocalStorage(driver(), "accessToken"))
                .as("oturum token'i olusturulmamalidir")
                .isNull();
    }

    @Test(groups = {"fr001"},
            description = "UI-FR001-09 | Alana yeni karakter girilince hata mesaji kaybolur")
    @Story("ACC-006 — Yazinca mesaj kaybolur")
    @TmsLink("FR-001-ACC-006")
    @Severity(SeverityLevel.NORMAL)
    public void errorMessageDisappearsOnTyping() {
        loginPage.loginExpectingFailure(config.username(), "kesinlikle-yanlis-sifre");
        assertThat(loginPage.hasErrorMessage()).as("once hata gorunmeli").isTrue();

        loginPage.enterPassword("y");

        assertThat(loginPage.errorMessageDisappears())
                .as("ACC-006 — yeni karakter girilince mesaj kaybolur").isTrue();
    }

    @Test(groups = {"fr001"},
            description = "UI-FR001-10 | Kullanici adindaki bas/son bosluklar kirpilir")
    @Story("Validasyon — Bas/son bosluk")
    @TmsLink("FR-001-VAL-TRIM")
    @Severity(SeverityLevel.MINOR)
    @Description("Dokuman 'bosluk ile baslamamali veya bitmemelidir' diyor. Uygulama bunu "
            + "hata mesaji yerine OTOMATIK KIRPMA ile karsiliyor (login.component.ts trimUsername); "
            + "test bu davranisi belgeler.")
    public void usernameIsTrimmedOnBlur() {
        SearchCustomerPage searchPage =
                loginPage.loginAs("  " + config.username() + "  ", config.password());

        assertThat(searchPage.isAt())
                .as("bosluklar kirpildigi icin giris basarili olur").isTrue();
    }

    @Test(groups = {"fr001"},
            description = "UI-FR001-11 | Kullanici adi en fazla 50 karakter kabul eder")
    @Story("Validasyon — Maksimum 50 karakter")
    @TmsLink("FR-001-VAL-MAXLEN")
    @Severity(SeverityLevel.MINOR)
    @Description("Kural: 'En fazla 50 karakter girilebilir.' Uygulama bunu, alani gecersiz "
            + "kilmak yerine GIRISI 50 KARAKTERDE KESEREK karsiliyor. Test kuralin kendisini "
            + "dogrular (etkin deger 50'yi asamaz), uygulama bicimini degil.")
    public void usernameCannotExceed50Characters() {
        loginPage.enterUsername("a".repeat(51)).enterPassword(config.password());

        String actual = loginPage.usernameValue();
        Allure.parameter("Girilen uzunluk", 51);
        Allure.parameter("Alandaki uzunluk", actual.length());

        assertThat(actual.length())
                .as("kullanici adi 50 karakteri asmamalidir")
                .isLessThanOrEqualTo(50);
    }

    /** rgb/rgba metnini ayristirip kirmizi baskin mi diye bakar. */
    private static boolean isReddish(String cssColor) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile("(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)")
                .matcher(cssColor == null ? "" : cssColor);
        if (!matcher.find()) {
            return false;
        }
        int red = Integer.parseInt(matcher.group(1));
        int green = Integer.parseInt(matcher.group(2));
        int blue = Integer.parseInt(matcher.group(3));
        return red > green && red > blue && red >= 120;
    }
}
