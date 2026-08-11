package com.crmlite.ui.tests.fr002search;

import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.model.ValidationCase;
import com.crmlite.ui.data.provider.ValidationDataProvider;
import com.crmlite.ui.pages.customer.SearchCustomerPage;
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
 * FR-002 — Arama filtrelerinin format kurallari ve Search butonunun aktiflik durumu.
 */
@Epic("FR-002 Musteri Arama ve Goruntuleme")
@Feature("UC-EACRML-002")
public class SearchFilterValidationTests extends AuthenticatedTest {

    @Test(groups = {"fr002", "regression"},
            description = "UI-FR002-01 | Hicbir filtre yokken Search butonu pasif kalir")
    @Story("ACC-003 — En az bir filtre olmadan Search pasif")
    @TmsLink("FR-002-ACC-003")
    @Severity(SeverityLevel.CRITICAL)
    public void searchDisabledWithoutAnyFilter() {
        SearchCustomerPage page = openSearchCustomer();

        assertThat(page.isSearchDisabled())
                .as("ACC-003 — filtre girilmeden Search aktif olmamalidir").isTrue();
    }

    @Test(groups = {"fr002", "regression"},
            description = "UI-FR002-02 | En az bir filtre dolunca Search aktiflesir")
    @Story("ACC-003 — En az bir filtre olmadan Search pasif")
    @TmsLink("FR-002-ACC-003")
    @Severity(SeverityLevel.CRITICAL)
    public void searchEnabledWithOneFilter() {
        SearchCustomerPage page = openSearchCustomer();
        page.enterFirstName("Ahmet");

        assertThat(page.isSearchEnabled()).as("tek filtre yeterlidir").isTrue();
    }

    @Test(groups = {"fr002", "regression"},
            dataProvider = "searchFilterValidation", dataProviderClass = ValidationDataProvider.class,
            description = "UI-FR002-19, -20 | Filtre format kurallari (NAT ID / GSM sinir degerleri)")
    @Story("Validasyon — Filtre format kurallari")
    @TmsLink("FR-002-VAL")
    @Severity(SeverityLevel.NORMAL)
    @Description("Vakalar testdata/search-filter-validation.json dosyasindan gelir. "
            + "Beklenen mesajlar expected/messages_en.properties icinde tutulur.")
    public void filterFormatRules(ValidationCase testCase) {
        SearchCustomerPage page = openSearchCustomer();
        SearchCustomerPage.Filter filter = SearchCustomerPage.Filter.valueOf(testCase.filter());

        // Format kontrolu blur olayinda degerlendirilir; deger girilip odak kaldirilir.
        page.enterAndBlur(filter, testCase.value());

        if (testCase.expectsError()) {
            assertThat(page.hasFilterError(filter))
                    .as("%s -> hata mesaji beklenir", testCase).isTrue();
            assertThat(page.filterError(filter))
                    .as("%s -> mesaj metni", testCase)
                    .isEqualTo(ExpectedMessages.get(testCase.expectedMessageKey()));
        } else {
            assertThat(page.hasFilterError(filter))
                    .as("%s -> hata mesaji OLMAMALIDIR", testCase).isFalse();
        }
    }

    @Test(groups = {"fr002"},
            description = "UI-FR002-21 | Sayisal alanlara harf girisi engellenir")
    @Story("Validasyon — Yalnizca rakam kurali")
    @TmsLink("FR-002-VAL-DIGITS")
    @Severity(SeverityLevel.MINOR)
    @Description("Customer ID, Account Number ve Order Number alanlari yalnizca rakam kabul eder. "
            + "Uygulama bunu hata mesaji yerine girisi temizleyerek (sanitize) saglar.")
    public void numericFiltersRejectLetters() {
        SearchCustomerPage page = openSearchCustomer();

        page.enterCustomerId("abcDEF");
        assertThat(page.valueOf(SearchCustomerPage.Filter.CUSTOMER_ID))
                .as("Customer ID harf kabul etmemelidir").doesNotContainPattern("[A-Za-z]");

        page.enterAccountNumber("xyz123");
        assertThat(page.valueOf(SearchCustomerPage.Filter.ACCOUNT_NUMBER))
                .as("Account Number harf kabul etmemelidir").doesNotContainPattern("[A-Za-z]");

        page.enterOrderNumber("ord42");
        assertThat(page.valueOf(SearchCustomerPage.Filter.ORDER_NUMBER))
                .as("Order Number harf kabul etmemelidir").doesNotContainPattern("[A-Za-z]");
    }
}
