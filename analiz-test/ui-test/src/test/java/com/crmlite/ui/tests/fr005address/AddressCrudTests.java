package com.crmlite.ui.tests.fr005address;

import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.pages.components.AddressCardComponent;
import com.crmlite.ui.pages.components.AddressModalComponent;
import com.crmlite.ui.pages.components.ConfirmDialogComponent;
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

@Epic("FR-005 Adres Yonetimi")
@Feature("UC-EACRML-005")
public class AddressCrudTests extends AuthenticatedTest {

    @Test(groups = {"smoke", "fr005"},
            description = "UI-FR005-01 | Address tabinda musterinin adresleri listelenir")
    @Story("ACC-001 — Adresler listelenir")
    @TmsLink("FR-005-ACC-001")
    @Severity(SeverityLevel.BLOCKER)
    public void addressTabListsCustomerAddresses() {
        CreatedCustomer customer = TestDataFactory.customerWithAddresses(2);
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAddressTab();

        assertThat(detail.addressCount()).as("iki adres listelenir").isEqualTo(2);
        assertThat(detail.addressCard(0).cityName()).as("sehir adi gosterilir").isNotBlank();
    }

    @Test(groups = {"smoke", "fr005"},
            description = "UI-FR005-02 | Add New Address ile yeni adres kaydedilir ve listeye eklenir")
    @Story("ACC-002, ACC-004 — Adres ekleme")
    @TmsLink("FR-005-ACC-004")
    @Severity(SeverityLevel.BLOCKER)
    public void newAddressIsSavedAndListed() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAddressTab();

        int before = detail.addressCount();
        detail.addAddress("Yeni Sokak", "No:42", "Is adresi");

        assertThat(detail.addressCount()).as("ACC-004 — adres listeye eklendi").isEqualTo(before + 1);
        assertThat(detail.addressCards())
                .as("eklenen adres listede gorunur")
                .anyMatch(card -> card.addressLine().contains("Yeni Sokak"));
    }

    @Test(groups = {"fr005", "regression"},
            description = "UI-FR005-04 | Zorunlu alanlar dolunca Save aktiflesir")
    @Story("ACC-003 — Zorunlu alan yoksa Save pasif")
    @TmsLink("FR-005-ACC-003")
    @Severity(SeverityLevel.CRITICAL)
    public void saveEnabledWhenAllRequiredFieldsFilled() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAddressTab();

        AddressModalComponent modal = detail.openAddAddressModal();
        assertThat(modal.isSaveDisabled()).as("bos formda Save pasif").isTrue();

        modal.fill("Tam Dolu Sokak", "No:7", "Ev adresi");
        assertThat(modal.isSaveEnabled()).as("ACC-003 — alanlar dolunca Save aktif").isTrue();
    }

    @Test(groups = {"smoke", "fr005"},
            description = "UI-FR005-14, -15 | Edit ile adres guncellenir ve listeye yansir")
    @Story("ACC-013, ACC-015 — Adres guncelleme")
    @TmsLink("FR-005-ACC-013")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Edit modal'i mevcut degerlerle dolu acilir; kaydedilen degisiklik listede gorunur.")
    public void addressIsUpdatedThroughEdit() {
        CreatedCustomer customer = TestDataFactory.customerWithAddresses(2);
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAddressTab();

        AddressModalComponent modal = detail.editAddress(1);

        assertThat(modal.streetValue()).as("ACC-013 — mevcut bilgiler dolu gelir").isNotBlank();

        modal.enterStreet("Guncellenmis Sokak").enterDescription("Guncel aciklama");
        modal.save();
        modal.waitUntilClosed();

        assertThat(detail.addressCards())
                .as("ACC-015 — guncel adres listede")
                .anyMatch(card -> card.addressLine().contains("Guncellenmis Sokak"));
    }

    @Test(groups = {"fr005", "regression"},
            description = "UI-FR005-16 | Guncellemede zorunlu alan bosaltilinca Save pasiflesir")
    @Story("ACC-014 — Guncellemede zorunlu alanlar")
    @TmsLink("FR-005-ACC-014")
    @Severity(SeverityLevel.NORMAL)
    public void saveDisabledWhenRequiredFieldClearedOnEdit() {
        CreatedCustomer customer = TestDataFactory.customerWithAddresses(2);
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAddressTab();

        AddressModalComponent modal = detail.editAddress(1);
        modal.enterStreet("");

        assertThat(modal.isSaveDisabled())
                .as("ACC-014 — Street bosalinca Save pasif").isTrue();
    }

    @Test(groups = {"smoke", "fr005"},
            description = "UI-FR005-10, -11 | Birincil olmayan, faturasiz adres onaydan sonra silinir")
    @Story("ACC-008, ACC-010, ACC-012 — Adres silme")
    @TmsLink("FR-005-ACC-012")
    @Severity(SeverityLevel.BLOCKER)
    public void nonPrimaryAddressIsDeletedAfterConfirmation() {
        CreatedCustomer customer = TestDataFactory.customerWithAddresses(2);
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAddressTab();

        int before = detail.addressCount();
        int nonPrimaryIndex = detail.primaryAddressIndex() == 0 ? 1 : 0;

        ConfirmDialogComponent dialog = detail.deleteAddress(nonPrimaryIndex);
        assertThat(dialog.isOpen()).as("ACC-010 — silme icin onay istenir").isTrue();

        dialog.confirm();
        dialog.waitUntilClosed();
        detail.waitUntilAddressCountIsLessThan(before);

        assertThat(detail.addressCount())
                .as("ACC-012 — adres silindi").isEqualTo(before - 1);
    }

    @Test(groups = {"fr005", "regression"},
            description = "UI-FR005-12 | Onay diyalogunda iptal edilince adres silinmez")
    @Story("ACC-010 — Silme icin onay istenir")
    @TmsLink("FR-005-ACC-010")
    @Severity(SeverityLevel.NORMAL)
    public void cancellingConfirmationKeepsAddress() {
        CreatedCustomer customer = TestDataFactory.customerWithAddresses(2);
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAddressTab();

        int before = detail.addressCount();
        int nonPrimaryIndex = detail.primaryAddressIndex() == 0 ? 1 : 0;

        ConfirmDialogComponent dialog = detail.deleteAddress(nonPrimaryIndex);
        dialog.cancel();
        dialog.waitUntilClosed();

        assertThat(detail.addressCount())
                .as("iptal edilince adres listesi degismez").isEqualTo(before);
    }

    @Test(groups = {"fr005"},
            description = "UI-FR005-18 | City secilmeden Save aktiflesmez")
    @Story("Validasyon — City zorunlu")
    @TmsLink("FR-005-VAL-CITY")
    @Severity(SeverityLevel.NORMAL)
    public void cityIsRequired() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAddressTab();

        AddressModalComponent modal = detail.openAddAddressModal();
        modal.enterStreet("Sokak").enterHouseNumber("No:1").enterDescription("Aciklama");

        assertThat(modal.isSaveDisabled())
                .as("City secilmeden Save pasif kalmalidir").isTrue();
    }

    @Test(groups = {"fr005"},
            description = "UI-FR005-17 | Street 200 karakter sinirini asamaz")
    @Story("Validasyon — Street maks 200 karakter")
    @TmsLink("FR-005-VAL-STREETLEN")
    @Severity(SeverityLevel.MINOR)
    public void streetCannotExceed200Characters() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAddressTab();

        AddressModalComponent modal = detail.openAddAddressModal();
        modal.selectAnkara().enterStreet("S".repeat(250))
                .enterHouseNumber("No:1").enterDescription("Aciklama");

        assertThat(modal.streetValue().length())
                .as("Street 200 karakteri asmamalidir").isLessThanOrEqualTo(200);
    }

    @SuppressWarnings("unused")
    private static boolean matchesStreet(AddressCardComponent card, String street) {
        return card.addressLine().contains(street);
    }
}
