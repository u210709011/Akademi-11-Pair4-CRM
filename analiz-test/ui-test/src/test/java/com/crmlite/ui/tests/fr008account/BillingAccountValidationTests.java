package com.crmlite.ui.tests.fr008account;

import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.pages.components.BillingAccountModalComponent;
import com.crmlite.ui.pages.customer.CustomerDetailPage;
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
 * FR-008 ACC-009 — "Create" butonunun aktiflik kurallari.
 *
 * <p>Dokuman ucunu birlikte sart kosar: <b>Account Name</b>, <b>Account Description</b>
 * ve <b>en az bir adres</b>. Uygulama Account Description'i zorunlu tutmuyor
 * ({@code accountForm} yalnizca {@code accountName} istiyor, alanda {@code *} isareti
 * ve {@code field-error} blogu yok) — bu fark ayri bir test olarak isaretlendi.
 */
@Epic("FR-008 Fatura Hesabi Olusturma")
@Feature("UC-EACRML-008")
public class BillingAccountValidationTests extends AuthenticatedTest {

    @Test(groups = {"fr008", "regression"},
            description = "UI-FR008-08 | Bos formda Create pasiftir")
    @Story("ACC-009 — Zorunlu alanlar")
    @TmsLink("FR-008-ACC-009")
    @Severity(SeverityLevel.CRITICAL)
    public void createIsDisabledOnEmptyForm() {
        CreatedCustomer customer = TestDataFactory.customerWithAddresses(1);
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        BillingAccountModalComponent modal = detail.openCreateAccountModal();

        assertThat(modal.isCreateDisabled())
                .as("ACC-009 — hicbir alan doldurulmadan Create pasif").isTrue();
    }

    @Test(groups = {"fr008", "regression"},
            description = "UI-FR008-09 | Adres secilmeden Create aktiflesmez")
    @Story("ACC-009 — En az bir adres zorunlu")
    @TmsLink("FR-008-ACC-009")
    @Severity(SeverityLevel.CRITICAL)
    public void createIsDisabledWithoutAddress() {
        CreatedCustomer customer = TestDataFactory.customerWithAddresses(1);
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        BillingAccountModalComponent modal = detail.openCreateAccountModal();
        modal.enterAccountName("Adressiz Hesap").enterAccountDescription("Aciklama");

        assertThat(modal.isCreateDisabled())
                .as("ACC-009 — adres secilmeden Create pasif kalmalidir").isTrue();
    }

    @Test(groups = {"fr008", "regression"},
            description = "UI-FR008-10 | Account Name girilmeden Create aktiflesmez")
    @Story("ACC-009 — Account Name zorunlu")
    @TmsLink("FR-008-ACC-009")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Adres secimi Account Name'i OTOMATIK DOLDURUR (commit 220375e: secilen adresin "
            + "addrDesc'i isme yazilir, kullanici elle yazmadiysa). Bu yuzden alan, adres "
            + "secildikten sonra bilincli olarak temizlenir — aksi halde 'isim bos' durumu "
            + "hic olusmaz ve test dogrulamak istedigi kurali hic sinamaz.")
    public void createIsDisabledWithoutAccountName() {
        CreatedCustomer customer = TestDataFactory.customerWithAddresses(1);
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        BillingAccountModalComponent modal = detail.openCreateAccountModal();
        modal.enterAccountDescription("Aciklama").selectFirstExistingAddress();
        modal.clearAccountName();

        assertThat(modal.isCreateDisabled())
                .as("ACC-009 — Account Name bosken Create pasif").isTrue();
    }

    @Test(groups = {"fr008", "documented-gap"},
            description = "UI-FR008-11 | Account Description girilmeden Create aktiflesmez")
    @Story("ACC-009 — Account Description zorunlu")
    @TmsLink("FR-008-ACC-009")
    @Issue("FR-008-GAP-ACC009")
    @Severity(SeverityLevel.NORMAL)
    @Description("BILINEN UYUMSUZLUK — kirmizi kalmasi beklenir. Dokuman ACC-009 ve validasyon "
            + "tablosu Account Description'i ZORUNLU sayar; uygulamada alan opsiyoneldir "
            + "(accountForm yalnizca accountName istiyor, alanda * isareti ve hata blogu yok, "
            + "istek govdesinde accountDesc null gonderilebiliyor).")
    public void createIsDisabledWithoutAccountDescription() {
        CreatedCustomer customer = TestDataFactory.customerWithAddresses(1);
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        BillingAccountModalComponent modal = detail.openCreateAccountModal();
        modal.enterAccountName("Aciklamasiz Hesap").selectFirstExistingAddress();

        assertThat(modal.isCreateDisabled())
                .as("ACC-009 — Account Description bosken Create pasif olmalidir").isTrue();
    }

    @Test(groups = {"fr008", "regression"},
            description = "UI-FR008-12 | Tum zorunlu alanlar dolunca Create aktiflesir")
    @Story("ACC-009 — Zorunlu alanlar")
    @TmsLink("FR-008-ACC-009")
    @Severity(SeverityLevel.CRITICAL)
    public void createIsEnabledWhenAllRequiredFieldsFilled() {
        CreatedCustomer customer = TestDataFactory.customerWithAddresses(1);
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        BillingAccountModalComponent modal = detail.openCreateAccountModal();
        modal.enterAccountName("Tam Dolu Hesap")
                .enterAccountDescription("Aciklama")
                .selectFirstExistingAddress();

        assertThat(modal.isCreateEnabled())
                .as("ACC-009 — tum zorunlu alanlar dolunca Create aktif").isTrue();
    }

    @Test(groups = {"fr008", "regression"},
            description = "UI-FR008-13 | Yeni adres alanlari eksikken Create aktiflesmez")
    @Story("ACC-005 — Yeni adres zorunlu alanlari")
    @TmsLink("FR-008-VAL-NEWADDRESS")
    @Severity(SeverityLevel.NORMAL)
    @Description("Yeni adres modunda City, Street, House/Flat Number ve Address Description "
            + "dokumana gore zorunludur; biri eksikken Create aktiflesmemelidir.")
    public void createIsDisabledWhenNewAddressIncomplete() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        BillingAccountModalComponent modal = detail.openCreateAccountModal();
        modal.enterAccountName("Eksik Adresli").enterAccountDescription("Aciklama");
        modal.toggleAddressMode();
        // Address Description bilincli olarak bos birakilir.
        modal.selectNewAddressCity(BillingAccountModalComponent.CITY_ANKARA_LABEL)
                .enterNewStreet("Eksik Sokak")
                .enterNewHouseNumber("No:1");

        assertThat(modal.isCreateDisabled())
                .as("Address Description bosken Create pasif kalmalidir").isTrue();
    }
}
