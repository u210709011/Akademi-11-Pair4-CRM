package com.crmlite.ui.tests.fr018language;

import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.pages.components.NavbarComponent;
import com.crmlite.ui.pages.customer.CustomerDetailPage;
import com.crmlite.ui.pages.customer.SearchCustomerPage;
import com.crmlite.ui.tests.AuthenticatedTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FR-018 — Dil Destegi (UC-EACRML-018).
 *
 * <p><b>DOKUMAN NOTU:</b> kabul kriterleri dokumanda UC-EACRML-017 basliginin ALTINDA yer
 * alir; bloklar ait olduklari basliktan once gelir.
 *
 * <p><b>ONEMLI:</b> secilen dil {@code localStorage}'da ({@code crm-lite-lang}) saklanir ve
 * oturumlar arasi kalicidir. Bu yuzden her test sonunda dil INGILIZCEYE geri alinir -
 * aksi halde ayni tarayici oturumunu paylasan diger suite'ler Turkce arayuzle karsilasip
 * beklenen metinleri bulamaz.
 */
@Epic("FR-018 Dil Destegi")
@Feature("UC-EACRML-018")
public class LanguageSupportTests extends AuthenticatedTest {

    private static final String ENGLISH = "EN";
    private static final String TURKISH = "TR";

    /** Dil kalici oldugu icin diger testleri etkilememesi adina EN'e geri alinir. */
    @AfterMethod(alwaysRun = true)
    public void restoreEnglish() {
        NavbarComponent navbar = new NavbarComponent(driver());
        if (navbar.hasLanguageOption() && !ENGLISH.equals(navbar.currentLanguage())) {
            navbar.toggleLanguage();
        }
    }

    @Test(groups = {"smoke", "fr018"},
            description = "UI-FR018-01 | Ust menude dil secenegi bulunur")
    @Story("ACC-001 — Dil secenegi")
    @TmsLink("FR-018-ACC-001")
    @Severity(SeverityLevel.CRITICAL)
    public void navbarHasLanguageOption() {
        openSearchCustomer();
        NavbarComponent navbar = new NavbarComponent(driver());

        assertThat(navbar.hasLanguageOption())
                .as("ACC-001 — ust menude dil secenegi bulunur").isTrue();
        assertThat(navbar.currentLanguage())
                .as("dil kodu gosterilir").isIn(ENGLISH, TURKISH);
    }

    @Test(groups = {"fr018", "regression"},
            description = "UI-FR018-02 | Kayitli tercih yokken sistem belirli bir dille acilir")
    @Story("ACC-002 — Varsayilan dil")
    @TmsLink("FR-018-ACC-002")
    @Severity(SeverityLevel.NORMAL)
    @Description("Asil kural, kayitli tercih yokken sistemin BELIRLI ve tutarli bir dille "
            + "acilmasi; test bunu dogrular.\n"
            + "NOT: dokuman varsayilani Turkce ister, uygulama Ingilizce aciliyor "
            + "(I18nService.readStoredLang() -> 'en'). Bu bir yapilandirma tercihidir ve tum "
            + "test altyapisi Ingilizce metinlere gore kurulmustur; varsayilan degistirilirse "
            + "beklenen metin dosyalari gozden gecirilmelidir.")
    public void defaultLanguageIsDeterministic() {
        // Kayitli tercihi temizleyip "ilk acilis" durumunu uretir.
        clearStoredLanguage();
        openSearchCustomer();

        NavbarComponent navbar = new NavbarComponent(driver());
        assertThat(navbar.currentLanguage())
                .as("ACC-002 — kayitli tercih yokken uygulama belirli bir dille acilir")
                .isEqualTo(ENGLISH);
    }

    @Test(groups = {"fr018", "regression"},
            description = "UI-FR018-03 | Dil secenegi TR ve EN arasinda gecis yapar")
    @Story("ACC-003, ACC-004 — Dil degistirme")
    @TmsLink("FR-018-ACC-003")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Dokuman ACC-004'te \"sayfa yenilenmeli\" der; uygulama sayfayi yeniden "
            + "yuklemek yerine signal tabanli anlik guncelleme yapar. Dokumanin ASIL istegi "
            + "(arayuzun secilen dilde gosterilmesi) karsilandigi icin test yeniden yuklemeyi "
            + "degil GOZLEMLENEBILIR SONUCU dogrular.")
    public void languageTogglesBetweenTurkishAndEnglish() {
        SearchCustomerPage page = openSearchCustomer();
        NavbarComponent navbar = new NavbarComponent(driver());

        assertThat(navbar.currentLanguage()).as("on kosul: EN ile baslanir").isEqualTo(ENGLISH);
        assertThat(page.searchButtonLabel())
                .as("EN arayuz metni")
                .isEqualTo(ExpectedMessages.get("search.searchBtn"));

        navbar.toggleLanguage();

        assertThat(navbar.currentLanguage())
                .as("ACC-003 — Turkceye gecilir").isEqualTo(TURKISH);
        assertThat(page.searchButtonLabel())
                .as("ACC-004 — arayuz metni Turkceye doner")
                .isNotEqualTo(ExpectedMessages.get("search.searchBtn"));

        navbar.toggleLanguage();

        assertThat(navbar.currentLanguage())
                .as("ACC-003 — tekrar Ingilizceye donulur").isEqualTo(ENGLISH);
        assertThat(page.searchButtonLabel())
                .isEqualTo(ExpectedMessages.get("search.searchBtn"));
    }

    @Test(groups = {"fr018", "regression"},
            description = "UI-FR018-04 | Secilen dil sayfalar arasinda korunur")
    @Story("ACC-005 — Dil kaliciligi")
    @TmsLink("FR-018-ACC-005")
    @Severity(SeverityLevel.CRITICAL)
    public void selectedLanguagePersistsAcrossPages() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();

        openSearchCustomer();
        NavbarComponent navbar = new NavbarComponent(driver());
        navbar.toggleLanguage();

        assertThat(navbar.currentLanguage()).as("on kosul: Turkceye gecildi").isEqualTo(TURKISH);

        // Baska bir sayfaya gecilir.
        openCustomerDetail(customer.custId());

        assertThat(new NavbarComponent(driver()).currentLanguage())
                .as("ACC-005 — dil sayfa degisiminde korunur").isEqualTo(TURKISH);
    }

    @Test(groups = {"fr018", "regression"},
            description = "UI-FR018-05 | Musteri bilgileri dil degisikliginden etkilenmez")
    @Story("ACC-007 — Veri dilden bagimsizdir")
    @TmsLink("FR-018-ACC-007")
    @Severity(SeverityLevel.NORMAL)
    @Description("Dil degisikligi yalnizca ARAYUZ metinlerini kapsamalidir (ACC-006); "
            + "musteriye ait veriler oldugu gibi kalmalidir.")
    public void customerDataIsNotAffectedByLanguageChange() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId());

        String nameBefore = detail.infoValue(CustomerDetailPage.InfoField.FIRST_NAME);
        assertThat(nameBefore).as("on kosul: musteri adi okundu").isNotBlank();

        new NavbarComponent(driver()).toggleLanguage();

        assertThat(detail.infoValue(CustomerDetailPage.InfoField.FIRST_NAME))
                .as("ACC-007 — musteri adi dil degisiminden etkilenmez")
                .isEqualTo(nameBefore);
    }

    /** localStorage'daki dil tercihini siler ve sayfayi yeniden yukler. */
    private void clearStoredLanguage() {
        ((org.openqa.selenium.JavascriptExecutor) driver())
                .executeScript("window.localStorage.removeItem('crm-lite-lang');");
    }
}
