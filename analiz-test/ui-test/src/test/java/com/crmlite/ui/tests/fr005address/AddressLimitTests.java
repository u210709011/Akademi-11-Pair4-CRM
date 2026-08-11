package com.crmlite.ui.tests.fr005address;

import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.pages.customer.CustomerDetailPage;
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
 * FR-005 ACC-005 — 5 adres limiti (sinir degeri testleri).
 *
 * <p>On kosul adresler API ile kurulur: ayni durumu UI'dan hazirlamak 5 kez modal
 * acmak demektir ve testi hem yavaslatir hem kirilganlastirir.
 */
@Epic("FR-005 Adres Yonetimi")
@Feature("UC-EACRML-005")
public class AddressLimitTests extends AuthenticatedTest {

    @Test(groups = {"fr005", "regression"},
            description = "UI-FR005-06 | 4 adres varken 5. adres eklenebilir (sinir alt degeri)")
    @Story("ACC-005 — En fazla 5 adres")
    @TmsLink("FR-005-ACC-005")
    @Severity(SeverityLevel.NORMAL)
    public void fifthAddressCanStillBeAdded() {
        CreatedCustomer customer = TestDataFactory.customerWithAddresses(4);
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAddressTab();

        assertThat(detail.addressCount()).as("on kosul: 4 adres").isEqualTo(4);
        assertThat(detail.isAddAddressEnabled())
                .as("limit dolmadigi icin buton aktif").isTrue();

        detail.addAddress("Besinci Sokak", "No:5", "Bes numarali adres");

        assertThat(detail.addressCount()).as("5. adres eklendi").isEqualTo(5);
    }

    @Test(groups = {"fr005", "regression"},
            description = "UI-FR005-05 | 5 adres varken Add New Address pasiflesir")
    @Story("ACC-005 — En fazla 5 adres")
    @TmsLink("FR-005-ACC-005")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Sinir degeri ust siniri: limit dolunca yeni adres eklenemez.")
    public void addAddressDisabledAtLimit() {
        CreatedCustomer customer = TestDataFactory.customerAtAddressLimit();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAddressTab();

        assertThat(detail.addressCount())
                .as("on kosul: limit dolu").isEqualTo(CustomerDetailPage.MAX_ADDRESSES);
        assertThat(detail.isAddAddressDisabled())
                .as("ACC-005 — limit dolunca 'Add New Address' pasiflesir").isTrue();
        assertThat(detail.addressCountStat())
                .as("ozet sayaci 5/5 gosterir").contains("5");
    }
}
