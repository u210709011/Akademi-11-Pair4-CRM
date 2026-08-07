package com.crmlite.ui.tests.fr007delete;

import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.pages.components.ConfirmDialogComponent;
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
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FR-007 — Musteri Silme (UC-EACRML-007).
 *
 * <p><b>Test verisi ayrimi kritik:</b> onboarding her musteriye varsayilan bir
 * {@code CUST_ACCT} hesabi acar, ancak silme engeli yalnizca {@code BILL_ACCT}
 * tipindeki aktif fatura hesabi icin gecerlidir. Bu yuzden:
 * <ul>
 *   <li>{@code simpleCustomer()} → silinebilir (ACC-005, ACC-006)</li>
 *   <li>{@code customerWithBillingAccount()} → silinemez (ACC-003)</li>
 * </ul>
 *
 * <p>Her test kendi musterisini kurar; silme geri alinamaz bir islemdir ve
 * paylasilan veri kullanilamaz.
 */
@Epic("FR-007 Musteri Silme")
@Feature("UC-EACRML-007")
public class DeleteCustomerTests extends AuthenticatedTest {

    @Test(groups = {"smoke", "fr007"},
            description = "UI-FR007-01 | Silme ikonuna tiklandiginda onay istenir")
    @Story("ACC-001 — Silme icin onay istenir")
    @TmsLink("FR-007-ACC-001")
    @Severity(SeverityLevel.BLOCKER)
    public void deleteIconAsksForConfirmation() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId());

        ConfirmDialogComponent dialog = detail.clickDeleteCustomer();

        assertThat(dialog.isOpen()).as("ACC-001 — onay diyalogu acilir").isTrue();
        assertThat(dialog.title())
                .as("ACC-001 — diyalog basligi")
                .isEqualTo(ExpectedMessages.get("detail.deleteConfirmTitle"));
    }

    @Test(groups = {"fr007", "regression"},
            description = "UI-FR007-02 | Onay diyalogunda iptal edilince musteri silinmez")
    @Story("ACC-001 — Onay olmadan silinmez")
    @TmsLink("FR-007-ACC-001")
    @Severity(SeverityLevel.CRITICAL)
    public void cancellingConfirmationKeepsCustomer() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId());

        ConfirmDialogComponent dialog = detail.clickDeleteCustomer();
        dialog.cancel();
        dialog.waitUntilClosed();

        assertThat(detail.customerId())
                .as("iptal edilince musteri detayinda kalinir")
                .isEqualTo(String.valueOf(customer.custId()));
    }

    @Test(groups = {"smoke", "fr007"},
            description = "UI-FR007-03 | Aktif fatura hesabi olmayan musteri silinir ve arama ekranina donulur")
    @Story("ACC-005, ACC-006 — Soft delete ve yonlendirme")
    @TmsLink("FR-007-ACC-006")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Onboarding'de acilan varsayilan CUST_ACCT hesabi silmeyi engellemez; "
            + "musteri silinir ve kullanici musteri arama ekranina yonlendirilir.")
    public void customerWithoutBillingAccountIsDeletedAndRedirects() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId());

        ConfirmDialogComponent dialog = detail.clickDeleteCustomer();
        dialog.confirm();
        dialog.waitUntilClosed();

        SearchCustomerPage search = new SearchCustomerPage(driver());
        search.waitUntilLoaded();

        assertThat(search.isAt())
                .as("ACC-006 — basarili silme sonrasi arama ekranina yonlendirilir").isTrue();
    }

    @Test(groups = {"fr007", "regression"},
            description = "UI-FR007-04 | Aktif fatura hesabi olan musteri silinemez ve bilgilendirme gosterilir")
    @Story("ACC-003 — Aktif fatura hesabi silmeyi engeller")
    @TmsLink("FR-007-ACC-003")
    @Severity(SeverityLevel.CRITICAL)
    @Description("BILL_ACCT tipinde aktif hesap bulunan musteri silinemez; "
            + "onay diyalogunda bilgilendirme mesaji gosterilir.")
    public void customerWithActiveBillingAccountCannotBeDeleted() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId());

        ConfirmDialogComponent dialog = detail.clickDeleteCustomer();
        dialog.confirm();

        assertThat(dialog.hasError())
                .as("ACC-003 — bilgilendirme mesaji gosterilir").isTrue();
        assertThat(dialog.errorText())
                .as("ACC-003 — mesaj metni")
                .isEqualTo(ExpectedMessages.get("detail.customerHasActiveBillingAccount"));
        assertThat(detail.customerId())
                .as("ACC-003 — silme gerceklesmez, musteri detayinda kalinir")
                .isEqualTo(String.valueOf(customer.custId()));
    }
}
