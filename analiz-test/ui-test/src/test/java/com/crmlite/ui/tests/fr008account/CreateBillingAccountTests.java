package com.crmlite.ui.tests.fr008account;

import com.crmlite.ui.data.ExpectedMessages;
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
 * FR-008 — Yeni Musteri Fatura Hesabi Olusturma (UC-EACRML-008).
 *
 * <p>Her test kendi musterisini kurar; hesap olusturma veriyi degistirir.
 * On kosul musteri ve adresleri API ile hazirlanir, dogrulanan davranis UI'dadir.
 *
 * <p>Onboarding her musteriye varsayilan bir {@code CUST_ACCT} hesabi acar; bu yuzden
 * "hesap eklendi" dogrulamalari <b>mutlak sayi</b> yerine <b>artis</b> uzerinden yapilir.
 */
@Epic("FR-008 Fatura Hesabi Olusturma")
@Feature("UC-EACRML-008")
public class CreateBillingAccountTests extends AuthenticatedTest {

    @Test(groups = {"smoke", "fr008"},
            description = "UI-FR008-01 | Customer Account tabinda fatura hesaplari listelenir")
    @Story("ACC-001 — Hesap ekrani acilir")
    @TmsLink("FR-008-ACC-001")
    @Severity(SeverityLevel.BLOCKER)
    public void accountsTabListsBillingAccounts() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        assertThat(detail.accountCount())
                .as("ACC-001 — musterinin fatura hesabi listelenir").isEqualTo(1);
        // Basliklar CSS ile buyuk harfe cevriliyor; getText() render edilmis metni dondurdugu
        // icin karsilastirma buyuk/kucuk harf duyarsiz yapilir.
        assertThat(detail.accountColumnHeaders())
                .as("ACC-001 — tablo kolonlari")
                .extracting(String::toUpperCase)
                .contains(ExpectedMessages.get("detail.accountNumber").toUpperCase(),
                        ExpectedMessages.get("detail.accountName").toUpperCase(),
                        ExpectedMessages.get("detail.accountType").toUpperCase());
    }

    @Test(groups = {"fr008", "regression"},
            description = "UI-FR008-17 | Fatura hesabi olmayan musteride tablo bostur")
    @Story("ACC-001 — Yalnizca fatura hesaplari listelenir")
    @TmsLink("FR-008-ACC-001")
    @Severity(SeverityLevel.NORMAL)
    @Description("Onboarding her musteriye varsayilan bir CUST_ACCT hesabi acar, ancak bu ekran "
            + "yalnizca BILL_ACCT tipini listeler (front-end mapper accountTpId'ye gore filtreler). "
            + "Yeni musteride tablo bu yuzden bostur — panel basligi da 'Billing Accounts (0)' der.")
    public void accountsTabExcludesDefaultCustomerAccount() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        assertThat(detail.accountCount())
                .as("varsayilan CUST_ACCT hesabi bu tabloda listelenmez").isZero();
    }

    @Test(groups = {"smoke", "fr008"},
            description = "UI-FR008-02 | Create New Account, Create Billing Account ekranini acar")
    @Story("ACC-002, ACC-003 — Hesap olusturma ekrani")
    @TmsLink("FR-008-ACC-002")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Modal acilir ve dokumanin sart kostugu Account Name / Account Description alanlarini icerir.")
    public void createNewAccountOpensModalWithRequiredFields() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        BillingAccountModalComponent modal = detail.openCreateAccountModal();

        assertThat(modal.isOpen()).as("ACC-002 — Create Billing Account ekrani acilir").isTrue();
        assertThat(modal.title())
                .as("ACC-002 — modal basligi")
                .isEqualTo(ExpectedMessages.get("detail.createBillingAccountTitle"));
        assertThat(modal.hasAccountNameField())
                .as("ACC-003 — Account Name alani bulunur").isTrue();
        assertThat(modal.hasAccountDescriptionField())
                .as("ACC-003 — Account Description alani bulunur").isTrue();
    }

    @Test(groups = {"fr008", "regression"},
            description = "UI-FR008-03 | Musterinin mevcut adreslerinden biri secilebilir")
    @Story("ACC-004, ACC-008 — Mevcut adres secimi")
    @TmsLink("FR-008-ACC-004")
    @Severity(SeverityLevel.CRITICAL)
    public void existingAddressCanBeSelected() {
        CreatedCustomer customer = TestDataFactory.customerWithAddresses(2);
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        BillingAccountModalComponent modal = detail.openCreateAccountModal();

        assertThat(modal.hasServiceAddressField())
                .as("ACC-004 — mevcut adres secim alani bulunur").isTrue();

        modal.selectFirstExistingAddress();

        assertThat(modal.selectedAddressText())
                .as("ACC-008 — secilen adres ekranda listelenir").isNotBlank();
    }

    @Test(groups = {"fr008", "regression"},
            description = "UI-FR008-04 | Yeni adres formu City, Street, House Number ve Description icerir")
    @Story("ACC-004, ACC-005 — Yeni adres olusturma")
    @TmsLink("FR-008-ACC-005")
    @Severity(SeverityLevel.CRITICAL)
    public void newAddressFormContainsAllRequiredFields() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        BillingAccountModalComponent modal = detail.openCreateAccountModal();
        modal.toggleAddressMode();

        assertThat(modal.hasNewAddressFields())
                .as("ACC-005 — City, Street, House/Flat Number ve Address Description bulunur").isTrue();
    }

    @Test(groups = {"smoke", "fr008"},
            description = "UI-FR008-05 | Mevcut adresle fatura hesabi olusturulur ve tabloda listelenir")
    @Story("ACC-011, ACC-013, ACC-014 — Hesap olusturma")
    @TmsLink("FR-008-ACC-011")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Create sonrasi basari mesaji gosterilir, modal kapanir ve yeni hesap tabloda gorunur.")
    public void billingAccountIsCreatedWithExistingAddress() {
        CreatedCustomer customer = TestDataFactory.customerWithAddresses(1);
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        int before = detail.accountCount();
        String accountName = "Fatura Hesabi " + customer.custId();

        BillingAccountModalComponent modal = detail.openCreateAccountModal();
        modal.enterAccountName(accountName)
                .enterAccountDescription("Aylik elektrik faturasi")
                .selectFirstExistingAddress();
        modal.create();
        modal.waitUntilClosed();

        assertThat(detail.hasSuccessToast())
                .as("ACC-013 — basari mesaji gosterilir").isTrue();
        assertThat(detail.successToastText())
                .as("ACC-013 — basari mesaji metni")
                .isEqualTo(ExpectedMessages.get("detail.createAccountSuccess"));
        assertThat(detail.waitForAccountCount(before + 1))
                .as("ACC-014 — hesap sayisi bir artar").isEqualTo(before + 1);
        assertThat(detail.accountNames())
                .as("ACC-014 — yeni hesap tabloda listelenir").contains(accountName);
    }

    @Test(groups = {"fr008", "regression"},
            description = "UI-FR008-06 | Yeni adresle birlikte fatura hesabi olusturulur")
    @Story("ACC-007, ACC-011 — Yeni adresle hesap olusturma")
    @TmsLink("FR-008-ACC-007")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Yeni adres hesapla birlikte tek istekte kaydedilir; ayri bir adres-kaydet adimi yoktur.")
    public void billingAccountIsCreatedWithNewAddress() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        int before = detail.accountCount();
        String accountName = "Yeni Adresli Hesap " + customer.custId();

        BillingAccountModalComponent modal = detail.openCreateAccountModal();
        modal.enterAccountName(accountName).enterAccountDescription("Su faturasi");
        modal.toggleAddressMode();
        modal.fillNewAddress("Fatura Sokak", "No:12", "Fatura adresi");
        modal.create();
        modal.waitUntilClosed();

        assertThat(detail.waitForAccountCount(before + 1))
                .as("ACC-011 — yeni adresle hesap olusturuldu").isEqualTo(before + 1);
        assertThat(detail.accountNames())
                .as("ACC-014 — yeni hesap tabloda listelenir").contains(accountName);
    }

    @Test(groups = {"fr008", "regression"},
            description = "UI-FR008-07 | Olusturulan hesap billing account tipinde listelenir")
    @Story("ACC-012 — Account Type billing account")
    @TmsLink("FR-008-ACC-012")
    @Severity(SeverityLevel.NORMAL)
    @Description("Dokuman hesabin 224 (billing account) tipinde olusturulmasini sart kosar. "
            + "Bu ekran yalnizca BILL_ACCT tipini listeledigi icin (mapper accountTpId'ye gore "
            + "filtreler), hesabin tabloda GORUNMESI tipin dogru oldugunun kanitidir: yanlis "
            + "tiple olussaydi listede hic yer almazdi.")
    public void createdAccountHasBillingAccountType() {
        CreatedCustomer customer = TestDataFactory.customerWithAddresses(1);
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        assertThat(detail.accountCount())
                .as("on kosul: henuz fatura hesabi yok").isZero();

        String accountName = "Tip Kontrol " + customer.custId();
        BillingAccountModalComponent modal = detail.openCreateAccountModal();
        modal.enterAccountName(accountName)
                .enterAccountDescription("Tip dogrulama")
                .selectFirstExistingAddress();
        modal.create();
        modal.waitUntilClosed();

        // Beklemeli okuma: tablo, modal kapandiktan SONRA asenkron tazeleniyor.
        assertThat(detail.waitForAccountNamed(accountName))
                .as("ACC-012 — hesap billing account tipinde olustu (tabloda listeleniyor)")
                .contains(accountName);
    }
}
