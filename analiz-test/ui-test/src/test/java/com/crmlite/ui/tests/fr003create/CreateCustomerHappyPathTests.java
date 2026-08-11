package com.crmlite.ui.tests.fr003create;

import com.crmlite.ui.data.builder.CustomerBuilder;
import com.crmlite.ui.data.model.CustomerData;
import com.crmlite.ui.pages.customer.CustomerDetailPage;
import com.crmlite.ui.pages.customer.create.AddressStep;
import com.crmlite.ui.pages.customer.create.CreateCustomerPage;
import com.crmlite.ui.pages.customer.create.ContactStep;
import com.crmlite.ui.pages.customer.create.DemographicStep;
import com.crmlite.ui.tests.AuthenticatedTest;
import io.qameta.allure.Allure;
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
 * FR-003 — Musteri Olusturma (UC-EACRML-003) uctan uca mutlu yol.
 *
 * <p>Bu senaryolar sihirbazi <b>tamamen UI'dan</b> doldurur; API yalnizca
 * cakisma testlerinde on kosul kurmak icin kullanilir.
 */
@Epic("FR-003 Musteri Olusturma")
@Feature("UC-EACRML-003")
public class CreateCustomerHappyPathTests extends AuthenticatedTest {

    @Test(groups = {"smoke", "fr003"},
            description = "UI-FR003-01 | Create Customer secildiginde demografik ekran acilir")
    @Story("ACC-001 — Create Customer -> demografik ekran")
    @TmsLink("FR-003-ACC-001")
    @Severity(SeverityLevel.CRITICAL)
    public void wizardOpensOnDemographicStep() {
        CreateCustomerPage wizard = openCreateCustomer();

        assertThat(wizard.isAt()).as("sihirbaz acildi").isTrue();
        assertThat(wizard.demographicStep().isAt()).as("ilk adim demografik").isTrue();
        assertThat(wizard.isStepLocked(CreateCustomerPage.Step.ADDRESS))
                .as("ileri adimlar baslangicta kilitli").isTrue();
    }

    @Test(groups = {"smoke", "fr003"},
            description = "UI-FR003-02, -21 | Uctan uca: demografik -> adres -> kontakt -> Create")
    @Story("ACC-015, ACC-016 — Musteri olusturulur ve Customer Info acilir")
    @TmsLink("FR-003-ACC-015")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Sihirbazin tamami UI'dan doldurulur. Basarili kayit sonrasi uygulama "
            + "/detail-customer/{custId} rotasina gecer ve girilen bilgiler goruntulenir.")
    public void createCustomerEndToEnd() {
        CustomerData data = CustomerBuilder.aValidCustomer().build();
        Allure.parameter("Nationality ID", data.individual().nationalId());

        CustomerDetailPage detail = createCustomerThroughWizard(data);

        assertThat(detail.currentUrl()).as("Customer Info ekranina gecildi")
                .contains("/detail-customer/");
        assertThat(detail.infoValue(CustomerDetailPage.InfoField.FIRST_NAME))
                .isEqualTo(data.individual().firstName());
        assertThat(detail.infoValue(CustomerDetailPage.InfoField.LAST_NAME))
                .isEqualTo(data.individual().lastName());
        assertThat(detail.infoValue(CustomerDetailPage.InfoField.NATIONAL_ID))
                .isEqualTo(data.individual().nationalId());
    }

    @Test(groups = {"fr003"},
            description = "UI-FR003-05 | Cancel ile kayit olusturmadan arama ekranina donulur")
    @Story("ACC-003 — Previous ile arama ekranina donus")
    @TmsLink("FR-003-ACC-003")
    @Severity(SeverityLevel.NORMAL)
    @Description("DOKUMAN NOTU: ACC-003 bu butonu 'Previous' olarak adlandiriyor; "
            + "uygulamada etiketi 'Cancel'dir, islevi aynidir.")
    public void cancelReturnsToSearchWithoutCreating() {
        CreateCustomerPage wizard = openCreateCustomer();
        wizard.demographicStep().enterFirstName("VazgecilenKayit");

        assertThat(wizard.cancel().isAt())
                .as("arama ekranina donuldu").isTrue();
    }

    @Test(groups = {"fr003"},
            description = "UI-FR003-06 | Benzersiz Nationality ID ile adres adimina gecilir")
    @Story("ACC-004, ACC-008 — Tekillik kontrolu sonrasi adres adimi")
    @TmsLink("FR-003-ACC-004")
    @Severity(SeverityLevel.CRITICAL)
    public void uniqueNationalIdAdvancesToAddressStep() {
        CreateCustomerPage wizard = openCreateCustomer();
        fillDemographic(wizard, CustomerBuilder.aValidCustomer().build());

        wizard.clickNext();

        assertThat(wizard.addressStep().isAt())
                .as("ACC-008 — adres adimina gecildi").isTrue();
        assertThat(wizard.hasErrorBanner()).as("hata banner'i olmamali").isFalse();
    }

    @Test(groups = {"fr003"},
            description = "UI-FR003-09 | Tek adres eklenip kontakt adimina gecilir")
    @Story("ACC-009, ACC-012 — Adres ekleme ve kontakt adimi")
    @TmsLink("FR-003-ACC-012")
    @Severity(SeverityLevel.CRITICAL)
    public void singleAddressAllowsAdvancingToContact() {
        CreateCustomerPage wizard = openCreateCustomer();
        fillDemographic(wizard, CustomerBuilder.aValidCustomer().build());
        wizard.clickNext();

        AddressStep addressStep = wizard.addressStep();
        assertThat(addressStep.isEmptyStateDisplayed()).as("baslangicta adres yok").isTrue();

        addressStep.addAddress("Cumhuriyet Cad.", "No:1 D:2", "Ev adresi");
        assertThat(addressStep.addressCount()).isEqualTo(1);
        assertThat(addressStep.isPrimaryAt(0)).as("ilk adres birincil sayilir").isTrue();

        wizard.clickNext();
        assertThat(wizard.contactStep().isAt()).as("kontakt adimina gecildi").isTrue();
    }

    @Test(groups = {"fr003"},
            description = "UI-FR003-10 | Birden fazla adres eklenebilir")
    @Story("ACC-009 — Bir veya birden fazla adres")
    @TmsLink("FR-003-ACC-009")
    @Severity(SeverityLevel.NORMAL)
    public void multipleAddressesCanBeAdded() {
        CreateCustomerPage wizard = openCreateCustomer();
        fillDemographic(wizard, CustomerBuilder.aValidCustomer().build());
        wizard.clickNext();

        AddressStep addressStep = wizard.addressStep();
        addressStep.addAddress("Birinci Sokak", "No:1", "Ev adresi");
        addressStep.addAddress("Ikinci Sokak", "No:2", "Is adresi");
        addressStep.addAddress("Ucuncu Sokak", "No:3", "Yazlik");

        assertThat(addressStep.addressCount()).isEqualTo(3);
        assertThat(addressStep.isAddAddressEnabled())
                .as("5 limitine ulasilmadigi icin buton aktif").isTrue();
    }

    @Test(groups = {"fr003"},
            description = "UI-FR003-13 | Kontakt adiminda dort alan da girilebilir")
    @Story("ACC-013 — Email, Mobile, Home Phone, Fax")
    @TmsLink("FR-003-ACC-013")
    @Severity(SeverityLevel.NORMAL)
    public void allContactFieldsAcceptInput() {
        CreateCustomerPage wizard = openCreateCustomer();
        CustomerData data = CustomerBuilder.aValidCustomer().build();
        fillDemographic(wizard, data);
        wizard.clickNext();
        wizard.addressStep().addAddress("Cumhuriyet Cad.", "No:1", "Ev adresi");
        wizard.clickNext();

        ContactStep contact = wizard.contactStep();
        contact.fillAll(data.contact().email(), data.contact().mobilePhone(),
                data.contact().homePhone(), data.contact().fax());

        assertThat(contact.emailValue()).isEqualTo(data.contact().email());
        assertThat(contact.mobilePhoneValue()).isEqualTo(data.contact().mobilePhone());
        assertThat(contact.homePhoneValue()).isEqualTo(data.contact().homePhone());
        assertThat(contact.faxValue()).isEqualTo(data.contact().fax());
        assertThat(wizard.isNextEnabled()).as("gecerli veride Create aktif").isTrue();
    }

    // --- Ortak yardimcilar ---

    private void fillDemographic(CreateCustomerPage wizard, CustomerData data) {
        DemographicStep step = wizard.demographicStep();
        step.fillRequired(
                data.individual().firstName(),
                data.individual().lastName(),
                data.individual().birthDate(),
                String.valueOf(data.individual().genderId()),
                data.individual().nationalId());
    }

    private CustomerDetailPage createCustomerThroughWizard(CustomerData data) {
        CreateCustomerPage wizard = openCreateCustomer();

        fillDemographic(wizard, data);
        assertThat(wizard.isNextEnabled()).as("zorunlu alanlar dolunca Next aktif").isTrue();
        wizard.clickNext();

        wizard.addressStep().addAddress(
                data.addresses().get(0).streetName(),
                data.addresses().get(0).buildingName(),
                data.addresses().get(0).addressDesc());
        wizard.clickNext();

        wizard.contactStep().fillRequired(data.contact().email(), data.contact().mobilePhone());
        return wizard.clickCreate();
    }
}
