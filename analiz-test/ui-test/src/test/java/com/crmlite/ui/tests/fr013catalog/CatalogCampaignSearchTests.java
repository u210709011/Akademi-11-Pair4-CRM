package com.crmlite.ui.tests.fr013catalog;

import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.pages.customer.CustomerDetailPage;
import com.crmlite.ui.pages.newsale.OfferSelectionPage;
import com.crmlite.ui.tests.AuthenticatedTest;
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
 * FR-013 — Katalog ve Kampanya Listeleme (UC-EACRML-013).
 *
 * <p><b>DOKUMAN NOTU:</b> bu FR'nin kabul kriterleri dokumanda UC-EACRML-012 basliginin
 * ALTINDA yer alir. Dokumanda icerik bloklari ait olduklari basliktan once geldigi icin
 * "UC-013 — Katalog ve Kampanya Listeleme" basliginin altindaki kriterler FR-014'e aittir.
 * Blogun FR-013'e ait oldugu, hemen ardindaki "FR-013 — Validasyon Tablosu" basligiyla
 * dogrulanmistir.
 *
 * <p>Testler urun kimliklerini SABITLEMEZ: seed verisi degistiginde kirilmamak icin ad
 * parcasiyla arama yapilir (V4__seed_catalog_demo_data.sql — 3 katalog, 23 urun teklifi,
 * 10 kampanya).
 */
@Epic("FR-013 Katalog ve Kampanya Listeleme")
@Feature("UC-EACRML-013")
public class CatalogCampaignSearchTests extends AuthenticatedTest {

    /** Seed verisinde bu parcayi iceren cok sayida urun var; sayfalama testi buna dayanir. */
    private static final String COMMON_NAME_FRAGMENT = "Home";
    private static final String NO_MATCH_FRAGMENT = "ZzzYokBoyleBirUrun";

    @Test(groups = {"smoke", "fr013"},
            description = "UI-FR013-01 | Ekran varsayilan olarak Catalog sekmesiyle acilir")
    @Story("ACC-001 — Catalog sekmesi varsayilan")
    @TmsLink("FR-013-ACC-001")
    @Severity(SeverityLevel.CRITICAL)
    public void catalogTabIsActiveByDefault() {
        OfferSelectionPage offers = openOfferSelection();

        assertThat(offers.activeTabLabel())
                .as("ACC-001 — varsayilan sekme Catalog")
                .isEqualTo(ExpectedMessages.get("newSale.catalogTab"));
    }

    @Test(groups = {"fr013", "regression"},
            description = "UI-FR013-02 | Catalog sekmesinde kategori, ID ve ad arama alanlari bulunur")
    @Story("ACC-003 — Catalog arama kriterleri")
    @TmsLink("FR-013-ACC-003")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Dokuman uc kriter alani sart kosar: kategori Select'i, Prod Offer ID ve "
            + "Prod Offer Name; yaninda Search butonu.")
    public void catalogSearchFormHasDocumentedFields() {
        OfferSelectionPage offers = openOfferSelection();

        assertThat(offers.hasCatalogCategorySelect())
                .as("ACC-003 — kategori Select'i bulunur").isTrue();
        assertThat(offers.searchFieldCount())
                .as("ACC-003 — uc kriter alani bulunur").isEqualTo(3);
        assertThat(offers.hasSearchButton())
                .as("ACC-003 — Search butonu bulunur").isTrue();
    }

    @Test(groups = {"fr013", "regression"},
            description = "UI-FR013-03 | Hicbir kriter girilmeden Search pasif kalir")
    @Story("ACC-005 — Bos kriterde Search pasif")
    @TmsLink("FR-013-ACC-005")
    @Severity(SeverityLevel.NORMAL)
    public void catalogSearchIsDisabledWithoutAnyCriterion() {
        OfferSelectionPage offers = openOfferSelection();

        assertThat(offers.isSearchDisabled())
                .as("ACC-005 — kriter yokken Search pasif").isTrue();

        offers.enterOfferName(COMMON_NAME_FRAGMENT);

        assertThat(offers.isSearchEnabled())
                .as("ACC-005 — tek kriter yeterlidir, Search aktiflesir").isTrue();
    }

    @Test(groups = {"smoke", "fr013"},
            description = "UI-FR013-04 | Ada gore arama eslesen urunleri listeler")
    @Story("ACC-004 — Catalog arama sonuclari")
    @TmsLink("FR-013-ACC-004")
    @Severity(SeverityLevel.BLOCKER)
    public void catalogSearchListsMatchingOffers() {
        OfferSelectionPage offers = openOfferSelection()
                .enterOfferName(COMMON_NAME_FRAGMENT);
        offers.searchUntilResults();

        assertThat(offers.resultRowCount())
                .as("ACC-004 — kritere uyan urunler listelenir").isPositive();
    }

    @Test(groups = {"fr013", "regression"},
            description = "UI-FR013-05 | Catalog tablosu dokumandaki kolonlari gosterir")
    @Story("ACC-006 — Catalog tablo kolonlari")
    @TmsLink("FR-013-ACC-006")
    @Severity(SeverityLevel.CRITICAL)
    public void catalogTableShowsDocumentedColumns() {
        OfferSelectionPage offers = openOfferSelection();

        // Buyuk/kucuk harf duyarsiz: basliklar CSS ile (text-transform: uppercase) buyuk
        // harfe cevriliyor, getText() ekranda GORUNENI dondurur. Icerik dogru, sunum farkli.
        assertThat(offers.resultColumnHeaders())
                .as("ACC-006 — Prod Offer ID / Prod Offer Name / Price / Actions")
                .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
                .containsExactly(
                        ExpectedMessages.get("newSale.prodOfferId"),
                        ExpectedMessages.get("newSale.prodOfferName"),
                        ExpectedMessages.get("newSale.price"),
                        ExpectedMessages.get("newSale.actions"));
    }

    @Test(groups = {"fr013", "regression"},
            description = "UI-FR013-06 | Campaign sekmesinde kategori, ID ve ad arama alanlari bulunur")
    @Story("ACC-007 — Campaign arama kriterleri")
    @TmsLink("FR-013-ACC-007")
    @Severity(SeverityLevel.CRITICAL)
    public void campaignSearchFormHasDocumentedFields() {
        OfferSelectionPage offers = openOfferSelection();
        offers.selectTab(OfferSelectionPage.Tab.CAMPAIGNS);

        assertThat(offers.hasCampaignCategorySelect())
                .as("ACC-007 — kategori Select'i bulunur").isTrue();
        assertThat(offers.searchFieldCount())
                .as("ACC-007 — uc kriter alani bulunur").isEqualTo(3);
        assertThat(offers.hasSearchButton())
                .as("ACC-007 — Search butonu bulunur").isTrue();
    }

    @Test(groups = {"fr013", "regression"},
            description = "UI-FR013-07 | Campaign sekmesinde de bos kriterde Search pasif kalir")
    @Story("ACC-009 — Bos kriterde Search pasif")
    @TmsLink("FR-013-ACC-009")
    @Severity(SeverityLevel.NORMAL)
    public void campaignSearchIsDisabledWithoutAnyCriterion() {
        OfferSelectionPage offers = openOfferSelection();
        offers.selectTab(OfferSelectionPage.Tab.CAMPAIGNS);

        assertThat(offers.isSearchDisabled())
                .as("ACC-009 — kriter yokken Search pasif").isTrue();
    }

    @Test(groups = {"fr013", "regression"},
            description = "UI-FR013-08 | Campaign tablosu dokumandaki kolonlari gosterir")
    @Story("ACC-010 — Campaign tablo kolonlari")
    @TmsLink("FR-013-ACC-010")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Catalog tablosundan farki: arada Bundled Offers kolonu vardir.")
    public void campaignTableShowsDocumentedColumns() {
        OfferSelectionPage offers = openOfferSelection();
        offers.selectTab(OfferSelectionPage.Tab.CAMPAIGNS);

        // Basliklar CSS ile buyuk harfe cevriliyor; bkz. catalogTableShowsDocumentedColumns.
        assertThat(offers.resultColumnHeaders())
                .as("ACC-010 — Campaign ID / Name / Bundled Offers / Price / Actions")
                .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
                .containsExactly(
                        ExpectedMessages.get("newSale.campaignId"),
                        ExpectedMessages.get("newSale.campaignName"),
                        ExpectedMessages.get("newSale.bundledOffers"),
                        ExpectedMessages.get("newSale.price"),
                        ExpectedMessages.get("newSale.actions"));
    }

    @Test(groups = {"fr013", "regression"},
            description = "UI-FR013-09 | Kampanyaya bagli urunler expand ile acilip kapanir")
    @Story("ACC-011, ACC-012 — Bundled Offers genisletme")
    @TmsLink("FR-013-ACC-012")
    @Severity(SeverityLevel.NORMAL)
    @Description("Genisletildiginde bagli urunler satirin ALTINDA ayri bir satirda listelenir; "
            + "tekrar tiklandiginda gizlenir.")
    public void bundledOffersExpandAndCollapse() {
        OfferSelectionPage offers = openOfferSelection();
        offers.selectTab(OfferSelectionPage.Tab.CAMPAIGNS);
        offers.enterCampaignName(COMMON_NAME_FRAGMENT);
        // Kampanya listesi sekme acilirken asenkron yuklenir; arama bellekteki liste
        // uzerinde calistigi icin yuklenene kadar tekrarlanir.
        offers.searchUntilResults();

        assertThat(offers.resultRowCount()).as("on kosul: kampanya listelendi").isPositive();
        assertThat(offers.hasBundledOffersToggle())
                .as("ACC-011 — genisletme oku bulunur").isTrue();

        offers.toggleBundledOffers(0);

        assertThat(offers.isBundledOffersExpanded())
                .as("ACC-012 — bagli urunler satirin altinda acilir").isTrue();
        assertThat(offers.bundledOfferCount())
                .as("ACC-012 — en az bir bagli urun listelenir").isPositive();

        offers.toggleBundledOffers(0);

        assertThat(offers.isBundledOffersCollapsed())
                .as("ACC-012 — tekrar tiklandiginda gizlenir").isTrue();
    }

    @Test(groups = {"fr013", "regression"},
            description = "UI-FR013-10 | Eslesme yoksa 'No records found.' gosterilir")
    @Story("ACC-013 — Sonuc bulunamadi")
    @TmsLink("FR-013-ACC-013")
    @Severity(SeverityLevel.NORMAL)
    public void noRecordsMessageIsShownWhenNothingMatches() {
        OfferSelectionPage offers = openOfferSelection()
                .enterOfferName(NO_MATCH_FRAGMENT);
        offers.search().waitForResults();

        assertThat(offers.resultRowCount()).as("ACC-013 — sonuc satiri olmamali").isZero();
        assertThat(offers.emptyResultText())
                .as("ACC-013 — bilgilendirme metni")
                .isEqualTo(ExpectedMessages.get("newSale.noResults"));
    }

    @Test(groups = {"fr013", "regression"},
            description = "UI-FR013-11 | Sonuclar sayfalanir, ilk sayfada en fazla 5 kayit gosterilir")
    @Story("ACC-014 — Sayfalama")
    @TmsLink("FR-013-ACC-014")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Dokuman ilk sayfada EN FAZLA 5 kayit sart kosar. Test, seed verisinde cok "
            + "sayida eslesme donduren bir ad parcasiyla arama yapar.")
    public void resultsArePaginatedWithFivePerPage() {
        OfferSelectionPage offers = openOfferSelection()
                .enterOfferName(COMMON_NAME_FRAGMENT);
        offers.searchUntilResults();

        assertThat(offers.resultRowCount())
                .as("ACC-014 — ilk sayfada en fazla 5 kayit").isLessThanOrEqualTo(5);
        assertThat(offers.hasPagination())
                .as("ACC-014 — sayfalama gosterilir").isTrue();
        assertThat(offers.paginationPageCount())
                .as("ACC-014 — birden fazla sayfa olusur").isGreaterThan(1);
    }

    @Test(groups = {"fr013", "regression"},
            description = "UI-FR013-12 | Sekme degistirilip donuldugunde arama kriterleri korunur")
    @Story("ACC-002 — Sekme durumu korunur")
    @TmsLink("FR-013-ACC-002")
    @Severity(SeverityLevel.NORMAL)
    @Description("Dokuman, bir sekmeye girilen kriterlerin ve listelenen sonuclarin sekme "
            + "degisiminde SIFIRLANMAMASINI sart kosar. Paneller kosullu render edildigi icin "
            + "DOM'dan silinir; durumun bilesende yasamasi gerekir.")
    public void searchCriteriaSurviveTabSwitch() {
        OfferSelectionPage offers = openOfferSelection()
                .enterOfferName(COMMON_NAME_FRAGMENT);
        offers.searchUntilResults();

        int rowsBefore = offers.resultRowCount();

        offers.selectTab(OfferSelectionPage.Tab.CAMPAIGNS);
        offers.selectTab(OfferSelectionPage.Tab.CATALOG);

        assertThat(offers.offerNameValue())
                .as("ACC-002 — girilen kriter korunur").isEqualTo(COMMON_NAME_FRAGMENT);
        assertThat(offers.resultRowCount())
                .as("ACC-002 — listelenen sonuclar korunur").isEqualTo(rowsBefore);
    }

    /** Fatura hesabi olan bir musteri kurup Offer Selection ekranini acar. */
    private OfferSelectionPage openOfferSelection() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();
        detail.toggleAccountRow(0);
        return detail.startNewSale();
    }
}
