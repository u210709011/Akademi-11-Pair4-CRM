package com.crmlite.ui.tests.fr002search;

import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.pages.components.ResultsTableComponent;
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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FR-002 — Musteri Arama ve Goruntuleme (UC-EACRML-002) kriter bazli aramalar.
 *
 * <p>On kosul musteri, sinif basina bir kez API ile kurulur; aranan davranis
 * tamamen UI'dadir.
 */
@Epic("FR-002 Musteri Arama ve Goruntuleme")
@Feature("UC-EACRML-002")
public class CustomerSearchTests extends AuthenticatedTest {

    private CreatedCustomer customer;

    @BeforeClass(alwaysRun = true)
    public void createCustomer() {
        customer = TestDataFactory.simpleCustomer();
    }

    @Test(groups = {"smoke", "fr002"},
            description = "UI-FR002-03 | NAT ID ile arama dogru musteriyi listeler")
    @Story("ACC-001, ACC-004 — Filtre ile arama")
    @TmsLink("FR-002-ACC-004")
    @Severity(SeverityLevel.BLOCKER)
    public void searchByNationalId() {
        SearchCustomerPage page = openSearchCustomer();
        page.enterNatId(customer.nationalId()).search();

        ResultsTableComponent table = page.resultsTable();
        table.waitForRows();

        assertThat(table.containsCustomerId(customer.customerId()))
                .as("aranan musteri sonuclarda").isTrue();
        assertThat(table.cellText(0, ResultsTableComponent.Column.NAT_ID))
                .isEqualTo(customer.nationalId());
    }

    @Test(groups = {"fr002"},
            description = "UI-FR002-04 | Customer ID ile arama")
    @Story("ACC-001, ACC-004 — Filtre ile arama")
    @TmsLink("FR-002-ACC-004")
    @Severity(SeverityLevel.CRITICAL)
    public void searchByCustomerId() {
        SearchCustomerPage page = openSearchCustomer();
        page.enterCustomerId(customer.customerId()).search();

        page.resultsTable().waitForRows();
        assertThat(page.resultsTable().containsCustomerId(customer.customerId())).isTrue();
    }

    @Test(groups = {"fr002"},
            description = "UI-FR002-05 | Account Number ile arama")
    @Story("ACC-001, ACC-004 — Filtre ile arama")
    @TmsLink("FR-002-ACC-004")
    @Severity(SeverityLevel.NORMAL)
    public void searchByAccountNumber() {
        SearchCustomerPage page = openSearchCustomer();
        page.enterAccountNumber(customer.accountNo()).search();

        page.resultsTable().waitForRows();
        assertThat(page.resultsTable().containsCustomerId(customer.customerId())).isTrue();
    }

    @Test(groups = {"fr002"},
            description = "UI-FR002-06 | GSM Number ile arama")
    @Story("ACC-001, ACC-004 — Filtre ile arama")
    @TmsLink("FR-002-ACC-004")
    @Severity(SeverityLevel.NORMAL)
    public void searchByGsmNumber() {
        SearchCustomerPage page = openSearchCustomer();
        page.enterGsmNumber(customer.contact().mobilePhone()).search();

        page.resultsTable().waitForRows();
        assertThat(page.resultsTable().containsCustomerId(customer.customerId())).isTrue();
    }

    @Test(groups = {"fr002"},
            description = "UI-FR002-07 | First Name + Last Name birlikte AND mantigi ile calisir")
    @Story("ACC-002 — First+Last AND mantigi")
    @TmsLink("FR-002-ACC-002")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Dogru ad+soyad ciftinde kayit gelir; ayni ad ile YANLIS soyad girildiginde "
            + "AND mantigi geregi ayni kayit gelmemelidir.")
    public void firstAndLastNameUseAndLogic() {
        SearchCustomerPage page = openSearchCustomer();
        page.enterFirstName(customer.individual().firstName())
                .enterLastName(customer.individual().lastName())
                .search();
        page.resultsTable().waitForRows();

        assertThat(page.resultsTable().containsCustomerId(customer.customerId()))
                .as("dogru ad+soyad ciftinde kayit bulunur").isTrue();

        SearchCustomerPage second = openSearchCustomer();
        second.enterFirstName(customer.individual().firstName())
                .enterLastName("EslesmeyenSoyad" + System.nanoTime())
                .search();

        assertThat(second.isEmptyStateDisplayed())
                .as("AND mantigi: soyad eslesmezse kayit donmemelidir").isTrue();
    }

    @Test(groups = {"smoke", "fr002"},
            description = "UI-FR002-09, -10 | Sonuclar tablo halinde ve dogru kolonlarla listelenir")
    @Story("ACC-005, ACC-006 — Sonuc tablosu ve kolonlar")
    @TmsLink("FR-002-ACC-006")
    @Severity(SeverityLevel.CRITICAL)
    public void resultsTableShowsRequiredColumns() {
        SearchCustomerPage page = openSearchCustomer();
        page.enterNatId(customer.nationalId()).search();
        page.resultsTable().waitForRows();

        List<String> headers = page.resultsTable().headerTexts();

        assertThat(headers).as("ACC-006 — alti kolon").hasSize(6);
        assertThat(headers.get(ResultsTableComponent.Column.CUSTOMER_ID.index()))
                .containsIgnoringCase("Customer ID");
        assertThat(headers.get(ResultsTableComponent.Column.NAT_ID.index()))
                .containsIgnoringCase("NAT ID");
        assertThat(page.resultsTable().rowCount()).as("en az bir satir").isPositive();
    }

    @Test(groups = {"smoke", "fr002"},
            description = "UI-FR002-15 | Customer ID'ye tiklayinca Customer Info ekrani acilir")
    @Story("ACC-009 — Customer ID -> Customer Info")
    @TmsLink("FR-002-ACC-009")
    @Severity(SeverityLevel.BLOCKER)
    public void clickingCustomerIdOpensDetail() {
        SearchCustomerPage page = openSearchCustomer();
        page.enterNatId(customer.nationalId()).search();
        page.resultsTable().waitForRows();

        CustomerDetailPage detail = page.openCustomer(customer.customerId());

        assertThat(detail.currentUrl()).contains("/detail-customer/" + customer.custId());
        assertThat(detail.infoValue(CustomerDetailPage.InfoField.NATIONAL_ID))
                .as("acilan musteri dogru").isEqualTo(customer.nationalId());
    }

    @Test(groups = {"fr002"},
            description = "UI-FR002-16 | Kayit bulunamadiginda bilgilendirme mesaji gosterilir")
    @Story("ACC-010 — Kayit yoksa bilgilendirme")
    @TmsLink("FR-002-ACC-010")
    @Severity(SeverityLevel.CRITICAL)
    @Description("DOKUMAN CELISKISI: ACC-010 tablosu ile UC-002 Adim 6.2 farkli metinler veriyor. "
            + "Uygulama UC surumunu (baslik + alt baslik) gerceklestiriyor; beklenen metinler "
            + "expected/messages_en.properties icinde tek noktada tutulur.")
    public void noResultsShowsInformationMessage() {
        SearchCustomerPage page = openSearchCustomer();
        page.enterNatId("99999999999").search();

        assertThat(page.isEmptyStateDisplayed()).as("bos sonuc ekrani").isTrue();
        assertThat(page.emptyStateTitle()).isEqualTo(ExpectedMessages.get("search.noResults"));
        assertThat(page.emptyStateDescription())
                .isEqualTo(ExpectedMessages.get("search.noResultsSubtitle"));
    }

    @Test(groups = {"smoke", "fr002"},
            description = "UI-FR002-17 | Create Customer butonu musteri olusturma ekranini acar")
    @Story("ACC-011 — Create Customer butonu")
    @TmsLink("FR-002-ACC-011")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Butonun VARLIGI ve ISLEVI dogrulanir. 'Sag ust kosede' olmasi gorsel bir "
            + "kriterdir; otomasyon kapsami disindadir.")
    public void createCustomerButtonOpensWizard() {
        SearchCustomerPage page = openSearchCustomer();

        assertThat(page.goToCreateCustomer().isAt())
                .as("Create Customer sihirbazi acildi").isTrue();
    }

    @Test(groups = {"fr002"},
            description = "UI-FR002-18 | Clear tum filtreleri ve sonuclari temizler")
    @Story("ACC-012 — Clear butonu")
    @TmsLink("FR-002-ACC-012")
    @Severity(SeverityLevel.NORMAL)
    public void clearResetsFiltersAndResults() {
        SearchCustomerPage page = openSearchCustomer();
        page.enterNatId(customer.nationalId()).search();
        page.resultsTable().waitForRows();

        page.clearFilters();

        assertThat(page.valueOf(SearchCustomerPage.Filter.NAT_ID_NUMBER))
                .as("filtre alani temizlendi").isEmpty();
        assertThat(page.isSearchDisabled())
                .as("filtre kalmadigi icin Search tekrar pasif").isTrue();
    }
}
