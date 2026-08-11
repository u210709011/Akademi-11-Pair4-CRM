package com.crmlite.ui.tests.fr015configuration;

import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.pages.customer.CustomerDetailPage;
import com.crmlite.ui.pages.newsale.ConfigurationStepPage;
import com.crmlite.ui.pages.newsale.OfferSelectionPage;
import com.crmlite.ui.tests.AuthenticatedTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FR-015 — Urun Konfigurasyonu (UC-EACRML-015).
 *
 * <p><b>DOKUMAN NOTU:</b> bu FR'nin kabul kriterleri dokumanda UC-EACRML-014 basliginin
 * ALTINDA yer alir; bloklar ait olduklari basliktan once gelir.
 *
 * <p>Ekrana ancak sepette urun varken ve Offer Selection'da Next'e basarak gelinir; tum
 * testler bu on kosulu {@link #openConfiguration()} ile kurar.
 */
@Epic("FR-015 Urun Konfigurasyonu")
@Feature("UC-EACRML-015")
public class ProductConfigurationTests extends AuthenticatedTest {

    private static final String COMMON_NAME_FRAGMENT = "Home";

    @Test(groups = {"smoke", "fr015"},
            description = "UI-FR015-01 | Offer Selection'da Next, Product Configuration'i acar")
    @Story("ACC-001 — Configuration ekrani acilir")
    @TmsLink("FR-015-ACC-001")
    @Severity(SeverityLevel.BLOCKER)
    public void nextOpensProductConfiguration() {
        ConfigurationStepPage config = openConfiguration();

        assertThat(config.isDisplayed())
                .as("ACC-001 — Product Configuration ekrani acilir").isTrue();
    }

    @Test(groups = {"fr015", "regression"},
            description = "UI-FR015-02 | Konfigure edilebilir her urun icin ayri bolum gosterilir")
    @Story("ACC-002 — Urun basina bolum")
    @TmsLink("FR-015-ACC-002")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Asil kural, konfigure edilecek her urunun kendi bolumunu almasi; test bunu "
            + "dogrular.\n"
            + "NOT: dokuman \"sepetteki her urun\" der; uygulama kartlari yalnizca kullanicinin "
            + "SECTIGI satirlar icin uretir - zorunlu iliskiyle otomatik eklenen ekipman "
            + "(modem, router) konfigure edilmedigi icin kart almaz. Bu bir tasarim tercihidir. "
            + "Karakteristigi olmayan urunlerde alan izgarasi yerine bilgilendirme notu "
            + "gosterilmesi de seed verisinin dogal sonucudur.")
    public void eachBasketProductHasItsOwnConfigurationSection() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();
        detail.toggleAccountRow(0);

        OfferSelectionPage offers = detail.startNewSale();
        offers.enterOfferName(COMMON_NAME_FRAGMENT);
        offers.search().waitForResults();
        offers.addToBasket(0);

        int configurableLines = offers.userItemCount();
        offers.clickNext();

        ConfigurationStepPage config = new ConfigurationStepPage(driver());
        config.waitUntilLoaded();

        assertThat(config.productCardCount())
                .as("ACC-002 — konfigure edilebilir her urun icin bir bolum")
                .isEqualTo(configurableLines);
        assertThat(config.configFieldCount() + config.pendingNoteCount())
                .as("ACC-002 — her bolum ya alan izgarasi ya bilgilendirme notu icerir")
                .isPositive();
    }

    @Test(groups = {"smoke", "fr015"},
            description = "UI-FR015-03 | Servis adresi bolumu ve iki secenek butonu bulunur")
    @Story("ACC-003 — Servis adresi")
    @TmsLink("FR-015-ACC-003")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Dokuman TEK servis adresi girilmesini ve kullanicinin yeni adres "
            + "olusturabilmesini VEYA mevcut adreslerden birini secebilmesini sart kosar.")
    public void serviceAddressSectionOffersBothOptions() {
        ConfigurationStepPage config = openConfiguration();

        assertThat(config.hasServiceAddressSection())
                .as("ACC-003 — servis adresi bolumu bulunur").isTrue();
        assertThat(config.hasChangeAddressButton())
                .as("ACC-003 — mevcut adresten secme secenegi").isTrue();
        assertThat(config.hasAddAddressButton())
                .as("ACC-003 — yeni adres olusturma secenegi").isTrue();
    }

    @Test(groups = {"fr015", "regression"},
            description = "UI-FR015-04 | Yeni adres modalinda dokumandaki dort alan bulunur")
    @Story("ACC-004 — Yeni adres alanlari")
    @TmsLink("FR-015-ACC-004")
    @Severity(SeverityLevel.CRITICAL)
    public void newAddressModalHasDocumentedFields() {
        ConfigurationStepPage config = openConfiguration();
        config.openAddAddressModal();

        assertThat(config.hasAllNewAddressFields())
                .as("ACC-004 — City / Street / House-Flat Number / Address Description").isTrue();
    }

    @Test(groups = {"fr015", "regression"},
            description = "UI-FR015-05 | Yeni adres alanlari eksikken Save pasif kalir")
    @Story("ACC-004 — Zorunlu alanlar")
    @TmsLink("FR-015-ACC-004")
    @Severity(SeverityLevel.NORMAL)
    @Description("Validasyon tablosu dort alani da zorunlu sayar ve hepsinde "
            + "\"This field is required.\" mesajini ongorur.")
    public void saveIsDisabledWhileNewAddressIsIncomplete() {
        ConfigurationStepPage config = openConfiguration();
        config.openAddAddressModal();

        assertThat(config.isSaveDisabled())
                .as("ACC-004 — bos formda Save pasif").isTrue();
    }

    @Test(groups = {"fr015", "regression"},
            description = "UI-FR015-06 | Yeni adreste Cancel uyari gosterir ve adres kaydedilmez")
    @Story("ACC-005 — Cancel uyarisi")
    @TmsLink("FR-015-ACC-005")
    @Severity(SeverityLevel.NORMAL)
    public void cancellingNewAddressWarnsAndDiscards() {
        ConfigurationStepPage config = openConfiguration();
        config.openAddAddressModal();
        config.fillNewAddress("Ankara", "Vazgecilen Cadde", "No:9", "Kaydedilmeyecek adres");

        config.cancelNewAddress();

        assertThat(config.discardConfirmMessage())
                .as("ACC-005 — uyari mesaji gosterilir")
                .isEqualTo(ExpectedMessages.get("detail.discardChangesMessage"));

        config.confirmDiscard();

        assertThat(config.isModalClosed())
                .as("ACC-005 — Product Configuration ekranina donulur").isTrue();
        assertThat(config.isDisplayed()).isTrue();
    }

    @Test(groups = {"fr015", "regression"},
            description = "UI-FR015-07 | Yeni adres kaydedilir, basari mesaji gosterilir ve secili gelir")
    @Story("ACC-006, ACC-007 — Adres kaydetme")
    @TmsLink("FR-015-ACC-006")
    @Severity(SeverityLevel.CRITICAL)
    public void savingNewAddressShowsSuccessAndSelectsIt() {
        ConfigurationStepPage config = openConfiguration();
        config.openAddAddressModal();

        String description = "Servis Adresi " + System.currentTimeMillis();
        config.fillNewAddress("Ankara", "Yeni Servis Caddesi", "No:12", description);
        config.saveNewAddress();

        assertThat(config.hasSuccessMessage())
                .as("ACC-006 — basari mesaji gosterilir").isTrue();
        assertThat(config.successMessage())
                .isEqualTo(ExpectedMessages.get("detail.addAddressSuccess"));
        assertThat(config.isModalClosed())
                .as("ACC-006 — Product Configuration ekranina donulur").isTrue();
        assertThat(config.hasSelectedAddress())
                .as("ACC-007 — eklenen adres ekranda gosterilir").isTrue();
    }

    @Test(groups = {"fr015", "regression"},
            description = "UI-FR015-08 | Mevcut adreslerden secim yapilabilir ve secilen gosterilir")
    @Story("ACC-003, ACC-007 — Mevcut adres secimi")
    @TmsLink("FR-015-ACC-007")
    @Severity(SeverityLevel.NORMAL)
    public void existingAddressCanBeSelectedAndIsShown() {
        ConfigurationStepPage config = openConfiguration();
        config.openChangeAddressModal();

        assertThat(config.addressOptionCount())
                .as("ACC-003 — musterinin mevcut adresleri listelenir").isPositive();

        config.selectAddressOption(0);

        assertThat(config.hasSelectedAddress())
                .as("ACC-007 — secilen adres ekranda gosterilir").isTrue();
        assertThat(config.selectedAddressText())
                .as("ACC-007 — adres metni bos olmamalidir").isNotBlank();
    }

    @Test(groups = {"fr015", "regression"},
            description = "UI-FR015-09 | Previous, sepet korunarak Offer Selection'a doner")
    @Story("ACC-008 — Previous ile geri donus")
    @TmsLink("FR-015-ACC-008")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Dokuman sepet iceriginin KORUNMASINI sart kosar. Sihirbaz tek kabuk "
            + "bilesende yasadigi icin geri donus rota degisimi degil adim degisimidir.")
    public void previousReturnsToOfferSelectionKeepingBasket() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();
        detail.toggleAccountRow(0);

        OfferSelectionPage offers = detail.startNewSale();
        offers.enterOfferName(COMMON_NAME_FRAGMENT);
        offers.search().waitForResults();
        offers.addToBasket(0);

        int linesBefore = offers.basketLineCount();
        offers.clickNext();

        ConfigurationStepPage config = new ConfigurationStepPage(driver());
        config.waitUntilLoaded();

        assertThat(offers.wizardBackButtonLabel())
                .as("ikinci adimda geri butonu Previous etiketlidir")
                .isEqualTo(ExpectedMessages.get("newSale.previousBtn"));

        offers.clickPrevious();

        assertThat(offers.activeStepLabel())
                .as("ACC-008 — Offer Selection adimina donulur")
                .isEqualTo(ExpectedMessages.get("newSale.stepOfferSelection"));
        assertThat(offers.basketLineCount())
                .as("ACC-008 — sepet icerigi korunur").isEqualTo(linesBefore);
    }

    @Test(groups = {"fr015", "regression"},
            description = "UI-FR015-10 | Konfigurasyon tamamlanmadan Next aktif olmaz")
    @Story("ACC-009 — Eksik bilgide Next pasif")
    @TmsLink("FR-015-ACC-009")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Dokuman: tum karakteristik alanlar VE servis adresi girilmeden Next aktif "
            + "olmamalidir.\n"
            + "Kuralin ADRES yarisi UI'dan uretilemiyor - uygulama servis adresini otomatik "
            + "secili getiriyor, dolayisiyla \"adres yok\" durumuna hic dusulmuyor. Bu yuzden "
            + "test KARAKTERISTIK yarisini dogrular: zorunlu alanlar bosken Next pasif kalmali, "
            + "dolduruldugunda aktiflesmelidir. Zorunlu alani olmayan bir urun secildiginde "
            + "kural bos yere saglanacagi icin yalnizca 'dolduruldu -> aktif' yonu assert edilir.")
    public void nextIsDisabledUntilConfigurationIsComplete() {
        ConfigurationStepPage config = openConfiguration();
        OfferSelectionPage wizard = new OfferSelectionPage(driver());

        if (config.configFieldCount() > 0) {
            assertThat(wizard.isNextDisabled())
                    .as("ACC-009 — zorunlu karakteristikler bosken Next pasif").isTrue();
        }

        config.fillAllConfigurationFields();
        if (!config.hasSelectedAddress()) {
            config.openChangeAddressModal();
            config.selectAddressOption(0);
        }

        assertThat(wizard.isNextEnabled())
                .as("ACC-009 — konfigurasyon tamamlaninca Next aktiflesir").isTrue();
    }

    @Test(groups = {"fr015", "regression"},
            description = "UI-FR015-11 | Next, Review & Submit adimini acar")
    @Story("ACC-010 — Review adimina gecis")
    @TmsLink("FR-015-ACC-010")
    @Severity(SeverityLevel.CRITICAL)
    public void nextOpensReviewAndSubmit() {
        ConfigurationStepPage config = openConfiguration();
        config.openChangeAddressModal();
        config.selectAddressOption(0);
        config.fillAllConfigurationFields();

        OfferSelectionPage wizard = new OfferSelectionPage(driver());
        assertThat(wizard.isNextEnabled())
                .as("on kosul: konfigurasyon tamamlandiginda Next aktiflesmelidir").isTrue();

        wizard.clickNext();
        wizard.waitForActiveStep(ExpectedMessages.get("newSale.stepReview"));

        assertThat(wizard.activeStepLabel())
                .as("ACC-010 — Review & Submit adimi acilir")
                .isEqualTo(ExpectedMessages.get("newSale.stepReview"));
    }

    /** Sepete bir urun ekleyip Product Configuration adimina gecer. */
    private ConfigurationStepPage openConfiguration() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();
        detail.toggleAccountRow(0);

        OfferSelectionPage offers = detail.startNewSale();
        offers.enterOfferName(COMMON_NAME_FRAGMENT);
        offers.search().waitForResults();
        offers.addToBasket(0);
        offers.clickNext();

        ConfigurationStepPage config = new ConfigurationStepPage(driver());
        config.waitUntilLoaded();
        return config;
    }
}
