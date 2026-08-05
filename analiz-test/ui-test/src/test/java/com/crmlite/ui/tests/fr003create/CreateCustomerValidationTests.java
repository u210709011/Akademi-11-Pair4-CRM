package com.crmlite.ui.tests.fr003create;

import com.crmlite.ui.core.utils.DateUtil;
import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.builder.CustomerBuilder;
import com.crmlite.ui.data.model.CustomerData;
import com.crmlite.ui.data.model.ValidationCase;
import com.crmlite.ui.data.provider.ValidationDataProvider;
import com.crmlite.ui.pages.components.AddressModalComponent;
import com.crmlite.ui.pages.customer.create.AddressStep;
import com.crmlite.ui.pages.customer.create.ContactStep;
import com.crmlite.ui.pages.customer.create.CreateCustomerPage;
import com.crmlite.ui.pages.customer.create.DemographicStep;
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
 * FR-003 — Zorunlu alan, format ve sinir degeri kontrolleri.
 */
@Epic("FR-003 Musteri Olusturma")
@Feature("UC-EACRML-003")
public class CreateCustomerValidationTests extends AuthenticatedTest {

    @Test(groups = {"fr003", "regression"},
            dataProvider = "demographicValidation", dataProviderClass = ValidationDataProvider.class,
            description = "UI-FR003-03 | Zorunlu demografik alan eksikken Next pasif kalir")
    @Story("ACC-002 — Zorunlu alanlar dolmadan Next pasif")
    @TmsLink("FR-003-ACC-002")
    @Severity(SeverityLevel.CRITICAL)
    public void nextStaysDisabledWhenDemographicFieldMissing(ValidationCase testCase) {
        CreateCustomerPage wizard = openCreateCustomer();
        CustomerData data = CustomerBuilder.aValidCustomer().build();
        DemographicStep step = wizard.demographicStep();

        // Test edilen alan DISINDAKI tum zorunlu alanlar gecerli doldurulur.
        // NOT: Gender icin "doldur sonra bosalt" yaklasimi mumkun degil — placeholder
        // secenegi <option value="" disabled> oldugundan Selenium onu secemez
        // ("You may not select a disabled option"). Bu yuzden "eksik alan" senaryosu
        // alani sonradan bosaltarak degil, hic doldurmayarak kurulur.
        DemographicStep.Field field = DemographicStep.Field.valueOf(testCase.field());
        fillRequiredExcept(step, data, field);

        // Alan bos degil ama gecersizse (or. 10 haneli NAT ID) deger yine de girilir.
        if (!testCase.value().isEmpty()) {
            applyValue(step, field, testCase.value());
        }

        assertThat(wizard.isNextDisabled())
                .as("%s -> Next PASIF kalmalidir", testCase).isTrue();
    }

    @Test(groups = {"fr003"},
            description = "UI-FR003-04 | Tum zorunlu alanlar dolunca Next aktiflesir")
    @Story("ACC-002 — Zorunlu alanlar dolunca Next aktif")
    @TmsLink("FR-003-ACC-002")
    @Severity(SeverityLevel.CRITICAL)
    public void nextEnabledWhenAllRequiredFilled() {
        CreateCustomerPage wizard = openCreateCustomer();
        CustomerData data = CustomerBuilder.aValidCustomer().build();

        assertThat(wizard.isNextDisabled()).as("bos formda Next pasif").isTrue();

        wizard.demographicStep().fillRequired(
                data.individual().firstName(), data.individual().lastName(),
                data.individual().birthDate(), String.valueOf(data.individual().genderId()),
                data.individual().nationalId());

        assertThat(wizard.isNextEnabled()).as("zorunlu alanlar dolunca Next aktif").isTrue();
    }

    @Test(groups = {"fr003", "regression"},
            description = "UI-FR003-17 | Gelecek tarihli dogum tarihi kabul edilmez")
    @Story("Validasyon — Dogum tarihi gelecek olamaz")
    @TmsLink("FR-003-VAL-FUTUREDATE")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Sinir degeri: yarin. Datepicker'da [max]=today tanimli oldugu icin "
            + "gelecek tarih form'u gecersiz kilar ve Next aktiflesmez.")
    public void futureBirthDateIsRejected() {
        CreateCustomerPage wizard = openCreateCustomer();
        CustomerData data = CustomerBuilder.aValidCustomer().withFutureBirthDate().build();

        wizard.demographicStep().fillRequired(
                data.individual().firstName(), data.individual().lastName(),
                data.individual().birthDate(), String.valueOf(data.individual().genderId()),
                data.individual().nationalId());

        assertThat(wizard.isNextDisabled())
                .as("gelecek tarih (%s) reddedilmelidir", data.individual().birthDate()).isTrue();
    }

    @Test(groups = {"fr003", "regression"},
            description = "UI-FR003-18 | 01/01/1900 kabul, 31/12/1899 reddedilir")
    @Story("Validasyon — 1900 oncesi tarih girilemez")
    @TmsLink("FR-003-VAL-MINDATE")
    @Severity(SeverityLevel.NORMAL)
    @Description("Sinir degeri testi: alt sinir 01/01/1900 DAHIL kabul edilmelidir.")
    public void birthDateLowerBoundary() {
        CreateCustomerPage wizard = openCreateCustomer();
        CustomerData valid = CustomerBuilder.aValidCustomer().withEarliestValidBirthDate().build();

        wizard.demographicStep().fillRequired(
                valid.individual().firstName(), valid.individual().lastName(),
                DateUtil.minValidBirthDate(), String.valueOf(valid.individual().genderId()),
                valid.individual().nationalId());
        assertThat(wizard.isNextEnabled())
                .as("01/01/1900 kabul edilmelidir").isTrue();

        wizard.demographicStep().enterBirthDate(DateUtil.justBeforeMinBirthDate());
        assertThat(wizard.isNextDisabled())
                .as("31/12/1899 reddedilmelidir").isTrue();
    }

    @Test(groups = {"fr003", "regression"},
            description = "UI-FR003-19 | Nationality ID 11 haneden kisa olamaz")
    @Story("Validasyon — NAT ID 11 hane")
    @TmsLink("FR-003-VAL-NATID")
    @Severity(SeverityLevel.CRITICAL)
    public void nationalIdMustBeElevenDigits() {
        CreateCustomerPage wizard = openCreateCustomer();
        CustomerData data = CustomerBuilder.aValidCustomer().build();
        DemographicStep step = wizard.demographicStep();

        step.fillRequired(data.individual().firstName(), data.individual().lastName(),
                data.individual().birthDate(), String.valueOf(data.individual().genderId()),
                "1234567890");
        // Hata mesaji ancak alan "touched" olduktan sonra gosterilir.
        step.blurField(DemographicStep.Field.NATIONAL_ID);

        assertThat(wizard.isNextDisabled()).as("10 haneli NAT ID reddedilir").isTrue();
        assertThat(step.fieldError(DemographicStep.Field.NATIONAL_ID))
                .isEqualTo(ExpectedMessages.get("create.nationalIdError"));
    }

    @Test(groups = {"fr003"},
            description = "UI-FR003-08 | Adres eklenmeden sonraki adima gecilemez")
    @Story("ACC-011 — En az bir adres zorunlu")
    @TmsLink("FR-003-ACC-011")
    @Severity(SeverityLevel.CRITICAL)
    public void cannotAdvanceWithoutAddress() {
        CreateCustomerPage wizard = openCreateCustomer();
        fillValidDemographic(wizard);
        wizard.clickNext();

        assertThat(wizard.addressStep().isEmptyStateDisplayed()).isTrue();
        assertThat(wizard.isNextDisabled())
                .as("ACC-011 — adres yokken Next pasif kalmalidir").isTrue();
    }

    @Test(groups = {"fr003", "regression"},
            dataProvider = "addressValidation", dataProviderClass = ValidationDataProvider.class,
            description = "UI-FR003-12 | Adres zorunlu alanlari eksikken Save pasif kalir")
    @Story("ACC-002 — Adres zorunlu alanlari")
    @TmsLink("FR-003-ACC-002")
    @Severity(SeverityLevel.NORMAL)
    public void addressModalSaveRequiresAllFields(ValidationCase testCase) {
        CreateCustomerPage wizard = openCreateCustomer();
        fillValidDemographic(wizard);
        wizard.clickNext();

        AddressModalComponent modal = wizard.addressStep().openAddAddressModal();

        if ("NONE".equals(testCase.field())) {
            modal.fill("Cumhuriyet Cad.", "No:1", "Ev adresi");
            assertThat(modal.isSaveEnabled()).as("%s -> Save AKTIF", testCase).isTrue();
            return;
        }

        // Test edilen alan DISINDAKI tum zorunlu alanlar doldurulur.
        // NOT: City icin "temizleme" mumkun degil — placeholder secenegi
        // <option value="" disabled> oldugundan Selenium onu secemez
        // ("You may not select a disabled option"). Bu yuzden eksik alan
        // sonradan bosaltilmak yerine hic doldurulmaz.
        fillAddressExcept(modal, AddressModalComponent.Field.valueOf(testCase.field()));

        assertThat(modal.isSaveDisabled())
                .as("%s -> Save PASIF kalmalidir", testCase).isTrue();
    }

    @Test(groups = {"fr003", "regression"},
            description = "UI-FR003-11 | 5 adres eklendiginde 'Add address' pasiflesir ve limit mesaji cikar")
    @Story("ACC-010 — En fazla 5 adres")
    @TmsLink("FR-003-ACC-010")
    @Severity(SeverityLevel.NORMAL)
    public void addressLimitOfFiveIsEnforced() {
        CreateCustomerPage wizard = openCreateCustomer();
        fillValidDemographic(wizard);
        wizard.clickNext();

        AddressStep addressStep = wizard.addressStep();
        for (int i = 0; i < AddressStep.MAX_ADDRESSES; i++) {
            addressStep.addAddress("Sokak " + i, "No:" + (i + 1), "Adres " + i);
        }

        assertThat(addressStep.addressCount()).isEqualTo(AddressStep.MAX_ADDRESSES);
        assertThat(addressStep.isAddAddressDisabled())
                .as("ACC-010 — limit dolunca buton pasiflesir").isTrue();
        assertThat(addressStep.limitMessage())
                .isEqualTo(ExpectedMessages.get("create.addressLimitReached"));
    }

    @Test(groups = {"fr003", "regression"},
            dataProvider = "contactValidation", dataProviderClass = ValidationDataProvider.class,
            description = "UI-FR003-14, -15, -16 | Kontakt format kurallari")
    @Story("ACC-014 — Gecerli format olmadan Create pasif")
    @TmsLink("FR-003-ACC-014")
    @Severity(SeverityLevel.CRITICAL)
    @Description("E-posta, Mobile Phone ve Home Phone format kurallari. Home Phone kurali "
            + "FR-003 ile FR-006 arasinda celisiyor; uygulama FR-003 surumunu uyguluyor.")
    public void contactFormatRules(ValidationCase testCase) {
        CreateCustomerPage wizard = openCreateCustomer();
        CustomerData data = CustomerBuilder.aValidCustomer().build();
        fillValidDemographic(wizard, data);
        wizard.clickNext();
        wizard.addressStep().addAddress("Cumhuriyet Cad.", "No:1", "Ev adresi");
        wizard.clickNext();

        ContactStep contact = wizard.contactStep();
        contact.fillRequired(data.contact().email(), data.contact().mobilePhone());

        ContactStep.Field field = ContactStep.Field.valueOf(testCase.field());
        applyContactValue(contact, field, testCase.value());
        // Hata mesaji ancak alan "touched" olduktan sonra gosterilir.
        contact.blurField(field);

        assertThat(wizard.isNextDisabled())
                .as("%s -> Create PASIF kalmalidir", testCase).isTrue();

        if (testCase.expectedMessageKey() != null) {
            assertThat(contact.fieldError(field))
                    .as("%s -> hata mesaji", testCase)
                    .isEqualTo(ExpectedMessages.get(testCase.expectedMessageKey()));
        }
    }

    // --- Yardimcilar ---

    private void fillValidDemographic(CreateCustomerPage wizard) {
        fillValidDemographic(wizard, CustomerBuilder.aValidCustomer().build());
    }

    private void fillValidDemographic(CreateCustomerPage wizard, CustomerData data) {
        wizard.demographicStep().fillRequired(
                data.individual().firstName(), data.individual().lastName(),
                data.individual().birthDate(), String.valueOf(data.individual().genderId()),
                data.individual().nationalId());
    }

    /** Verilen alan haric tum zorunlu demografik alanlari gecerli degerle doldurur. */
    private void fillRequiredExcept(DemographicStep step, CustomerData data,
                                    DemographicStep.Field skipped) {
        if (skipped != DemographicStep.Field.FIRST_NAME) {
            step.enterFirstName(data.individual().firstName());
        }
        if (skipped != DemographicStep.Field.LAST_NAME) {
            step.enterLastName(data.individual().lastName());
        }
        if (skipped != DemographicStep.Field.BIRTH_DATE) {
            step.enterBirthDate(data.individual().birthDate());
        }
        if (skipped != DemographicStep.Field.GENDER) {
            step.selectGender(String.valueOf(data.individual().genderId()));
        }
        if (skipped != DemographicStep.Field.NATIONAL_ID) {
            step.enterNationalId(data.individual().nationalId());
        }
    }

    private void applyValue(DemographicStep step, DemographicStep.Field field, String value) {
        switch (field) {
            case FIRST_NAME -> step.enterFirstName(value);
            case MIDDLE_NAME -> step.enterMiddleName(value);
            case LAST_NAME -> step.enterLastName(value);
            case BIRTH_DATE -> step.enterBirthDate(value);
            case GENDER -> step.selectGender(value);
            case FATHER_NAME -> step.enterFatherName(value);
            case MOTHER_NAME -> step.enterMotherName(value);
            case NATIONAL_ID -> step.enterNationalId(value);
        }
    }

    private void applyContactValue(ContactStep step, ContactStep.Field field, String value) {
        switch (field) {
            case EMAIL -> step.enterEmail(value);
            case HOME_PHONE -> step.enterHomePhone(value);
            case MOBILE_PHONE -> step.enterMobilePhone(value);
            case FAX -> step.enterFax(value);
        }
    }

    /** Verilen alan haric tum zorunlu adres alanlarini doldurur. */
    private void fillAddressExcept(AddressModalComponent modal, AddressModalComponent.Field skipped) {
        if (skipped != AddressModalComponent.Field.CITY) {
            modal.selectAnkara();
        }
        if (skipped != AddressModalComponent.Field.STREET) {
            modal.enterStreet("Cumhuriyet Cad.");
        }
        if (skipped != AddressModalComponent.Field.HOUSE_NUMBER) {
            modal.enterHouseNumber("No:1");
        }
        if (skipped != AddressModalComponent.Field.DESCRIPTION) {
            modal.enterDescription("Ev adresi");
        }
    }
}
