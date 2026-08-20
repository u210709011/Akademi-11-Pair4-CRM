package com.crmlite.ui.tests.fr010accountupdate;

import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.api.AddressApi;
import com.crmlite.ui.data.api.BillingAccountApi;
import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.pages.components.BillingAccountModalComponent;
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
 * FR-010 — Billing Account Guncelleme (UC-EACRML-010).
 *
 * <p><b>Yapisal not:</b> uygulama guncelleme icin <b>ayri bir ekran acmaz</b>; olusturma
 * modalinin aynisini kullanir ve basligi {@code editingAccount()} degerine gore degistirir.
 * Alan id'leri ortak oldugu icin {@link BillingAccountModalComponent} yeniden kullanilir.
 * FR-008'de bulunan modal tasmasi (yeni adres modunda butonlara ulasilamamasi) bu akis icin
 * de gecerlidir.
 */
@Epic("FR-010 Billing Account Guncelleme")
@Feature("UC-EACRML-010")
public class UpdateBillingAccountTests extends AuthenticatedTest {

    @Test(groups = {"smoke", "fr010"},
            description = "UI-FR010-01 | Edit butonu Update Billing Account ekranini acar")
    @Story("ACC-001 — Guncelleme ekrani acilir")
    @TmsLink("FR-010-ACC-001")
    @Severity(SeverityLevel.BLOCKER)
    public void editButtonOpensUpdateScreen() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        BillingAccountModalComponent modal = detail.openEditAccountModal(0);

        assertThat(modal.isOpen())
                .as("ACC-001 — Update Billing Account ekrani acilir").isTrue();
        assertThat(modal.title())
                .as("ACC-001 — baslik guncelleme modunda farklidir")
                .isEqualTo(ExpectedMessages.get("detail.updateBillingAccountTitle"));
    }

    @Test(groups = {"smoke", "fr010"},
            description = "UI-FR010-02 | Alanlar hesabin mevcut bilgileriyle dolu gelir")
    @Story("ACC-002 — Mevcut bilgiler yuklenir")
    @TmsLink("FR-010-ACC-002")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Dokuman Account Name ve Account Description alanlarinin guncellenmek istenen "
            + "hesabin mevcut bilgileriyle dolu gelmesini sart kosar.")
    public void fieldsArePrefilledWithCurrentValues() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        long addressId = AddressApi.primaryAddress(customer.custId()).id();
        BillingAccountApi.create(customer.custId(), addressId, "Mevcut Hesap Adi");

        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();
        BillingAccountModalComponent modal = detail.openEditAccountModal(0);

        assertThat(modal.accountNameValue())
                .as("ACC-002 — Account Name mevcut degerle dolu gelir").isEqualTo("Mevcut Hesap Adi");
        assertThat(modal.accountDescriptionValue())
                .as("ACC-002 — Account Description mevcut degerle dolu gelir").isNotBlank();
    }

    @Test(groups = {"fr010", "regression"},
            description = "UI-FR010-03 | Secili adres guncelleme ekraninda listelenir")
    @Story("ACC-007 — Secilen adres listelenir")
    @TmsLink("FR-010-ACC-007")
    @Severity(SeverityLevel.CRITICAL)
    public void selectedAddressIsShown() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        BillingAccountModalComponent modal = detail.openEditAccountModal(0);

        assertThat(modal.selectedAddressValue())
                .as("ACC-007 — hesabin adresi secili gelir").isNotBlank();
        assertThat(modal.selectedAddressText())
                .as("ACC-007 — secili adres ekranda listelenir").isNotBlank();
    }

    @Test(groups = {"fr010", "regression"},
            description = "UI-FR010-04 | Yeni adres formu dokumandaki dort alani icerir")
    @Story("ACC-003, ACC-004 — Yeni adres olusturma")
    @TmsLink("FR-010-ACC-004")
    @Severity(SeverityLevel.NORMAL)
    public void newAddressFormHasDocumentedFields() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        BillingAccountModalComponent modal = detail.openEditAccountModal(0);
        modal.toggleAddressMode();

        assertThat(modal.hasNewAddressFields())
                .as("ACC-004 — City, Street, House/Flat Number ve Address Description bulunur").isTrue();
    }

    @Test(groups = {"fr010", "regression"},
            description = "UI-FR010-05 | Account Name bosaltilinca Save pasiflesir")
    @Story("ACC-008 — Zorunlu alanlar")
    @TmsLink("FR-010-ACC-008")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Dokuman Account Name, Account Description ve en az bir adres girilmeden "
            + "Save butonunun aktif olmamasini sart kosar.")
    public void saveIsDisabledWhenAccountNameCleared() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        BillingAccountModalComponent modal = detail.openEditAccountModal(0);
        modal.clearAccountName();

        assertThat(modal.isSaveDisabled())
                .as("ACC-008 — Account Name bosken Save pasif").isTrue();
    }

    @Test(groups = {"smoke", "fr010"},
            description = "UI-FR010-06 | Hesap guncellenir, basari mesaji gosterilir ve tabloda yeni ad listelenir")
    @Story("ACC-010, ACC-011, ACC-012 — Guncelleme tamamlanir")
    @TmsLink("FR-010-ACC-010")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Dokumanin ana senaryosu: kullanici bilgileri gunceller, Save'e tiklar; "
            + "dogrulama mesaji gosterilir, Customer Account ekranina donulur ve hesap "
            + "guncel bilgileriyle tabloda listelenir.")
    public void accountIsUpdatedAndListedWithNewName() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        String newName = "Guncellenmis Hesap " + customer.custId();

        BillingAccountModalComponent modal = detail.openEditAccountModal(0);
        modal.enterAccountName(newName);
        modal.save();
        modal.waitUntilClosed();

        assertThat(detail.hasSuccessToast())
                .as("ACC-011 — dogrulama mesaji gosterilir").isTrue();
        assertThat(detail.successToastText())
                .as("ACC-011 — mesaj metni")
                .isEqualTo(ExpectedMessages.get("detail.updateAccountSuccess"));
        assertThat(detail.accountNames())
                .as("ACC-012 — hesap guncel adiyla tabloda listelenir").contains(newName);
    }
}
