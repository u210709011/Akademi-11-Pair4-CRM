package com.crmlite.ui.tests.fr004update;

import com.crmlite.ui.data.model.Gender;
import com.crmlite.ui.core.utils.DateUtil;
import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.pages.customer.UpdateCustomerPage;
import com.crmlite.ui.tests.AuthenticatedTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FR-004 — Guncelleme ekraninin zorunlu alan ve sinir degeri kontrolleri.
 *
 * <p>Bu testler veriyi <b>kaydetmedigi</b> icin tek bir musteri paylasilabilir.
 */
@Epic("FR-004 Musteri Bilgilerini Guncelleme")
@Feature("UC-EACRML-004")
public class UpdateValidationTests extends AuthenticatedTest {

    private CreatedCustomer customer;

    @BeforeClass(alwaysRun = true)
    public void createCustomer() {
        customer = TestDataFactory.simpleCustomer();
    }

    private UpdateCustomerPage openUpdateScreen() {
        return openCustomerDetail(customer.custId()).clickEdit();
    }

    /** ACC-004 kapsamindaki zorunlu alanlar. */
    @DataProvider(name = "requiredFields")
    public static Object[][] requiredFields() {
        return new Object[][]{
                {UpdateCustomerPage.Field.FIRST_NAME},
                {UpdateCustomerPage.Field.LAST_NAME},
                {UpdateCustomerPage.Field.NATIONAL_ID}
        };
    }

    @Test(groups = {"fr004", "regression"}, dataProvider = "requiredFields",
            description = "UI-FR004-06 | Zorunlu alan silinince Save pasiflesir")
    @Story("ACC-004 — Zorunlu alan bossa Save pasif")
    @TmsLink("FR-004-ACC-004")
    @Severity(SeverityLevel.CRITICAL)
    public void saveDisabledWhenRequiredFieldCleared(UpdateCustomerPage.Field field) {
        UpdateCustomerPage update = openUpdateScreen();

        assertThat(update.isSaveEnabled()).as("baslangicta form gecerli").isTrue();

        update.clearField(field);

        assertThat(update.isSaveDisabled())
                .as("%s bosaltilinca Save PASIF kalmalidir", field).isTrue();
    }

    @Test(groups = {"fr004", "regression"},
            description = "UI-FR004-10 | Gelecek tarihli dogum tarihi reddedilir")
    @Story("Validasyon — Gelecek tarih girilemez")
    @TmsLink("FR-004-VAL-FUTUREDATE")
    @Severity(SeverityLevel.CRITICAL)
    public void futureBirthDateIsRejected() {
        UpdateCustomerPage update = openUpdateScreen();

        update.enterBirthDate(DateUtil.tomorrow());

        assertThat(update.isSaveDisabled())
                .as("gelecek tarih (%s) reddedilmelidir", DateUtil.tomorrow()).isTrue();
    }

    @Test(groups = {"fr004", "regression"},
            description = "UI-FR004-11 | 01/01/1900 oncesi tarih reddedilir, sinir degeri kabul edilir")
    @Story("Validasyon — 1900 oncesi tarih girilemez")
    @TmsLink("FR-004-VAL-MINDATE")
    @Severity(SeverityLevel.NORMAL)
    @Description("Sinir degeri: 01/01/1900 DAHIL kabul edilmeli, 31/12/1899 reddedilmelidir.")
    public void birthDateLowerBoundary() {
        UpdateCustomerPage update = openUpdateScreen();

        update.enterBirthDate(DateUtil.minValidBirthDate());
        assertThat(update.isSaveEnabled()).as("01/01/1900 kabul edilmeli").isTrue();

        update.enterBirthDate(DateUtil.justBeforeMinBirthDate());
        assertThat(update.isSaveDisabled()).as("31/12/1899 reddedilmeli").isTrue();
    }

    @Test(groups = {"fr004", "regression"},
            description = "UI-FR004-12 | Nationality ID 11 hane kurali ve hata mesaji")
    @Story("Validasyon — NAT ID 11 hane")
    @TmsLink("FR-004-VAL-NATID")
    @Severity(SeverityLevel.CRITICAL)
    public void nationalIdMustBeElevenDigits() {
        UpdateCustomerPage update = openUpdateScreen();

        update.enterNationalId("1234567890");
        // Hata mesaji ancak alan "touched" olduktan sonra gosterilir.
        update.blurField(UpdateCustomerPage.Field.NATIONAL_ID);

        assertThat(update.isSaveDisabled()).as("10 haneli NAT ID reddedilir").isTrue();
        assertThat(update.fieldError(UpdateCustomerPage.Field.NATIONAL_ID))
                .isEqualTo(ExpectedMessages.get("create.nationalIdError"));
    }

    @Test(groups = {"fr004"},
            description = "UI-FR004-13 | Isim alanlari 50 karakteri asamaz")
    @Story("Validasyon — Maksimum 50 karakter")
    @TmsLink("FR-004-VAL-MAXLEN")
    @Severity(SeverityLevel.MINOR)
    @Description("Ekranda 'n/50' karakter sayaci bulunur; alan 50 karakterde kesilir.")
    public void namesCannotExceed50Characters() {
        UpdateCustomerPage update = openUpdateScreen();

        update.enterFirstName("A".repeat(60));

        assertThat(update.firstNameValue().length())
                .as("First Name 50 karakteri asmamalidir").isLessThanOrEqualTo(50);
        assertThat(update.charCounter(UpdateCustomerPage.Field.FIRST_NAME))
                .as("karakter sayaci gosterilir").contains("/50");
    }

    @Test(groups = {"fr004"},
            description = "UI-FR004-14 | Gender bosaltilamaz — zorunluluk placeholder'in pasifligiyle uygulanir")
    @Story("Validasyon — Gender zorunlu")
    @TmsLink("FR-004-VAL-GENDER")
    @Severity(SeverityLevel.NORMAL)
    @Description("Gender guncelleme ekraninda her zaman dolu gelir ve placeholder secenegi "
            + "<option value=\"\" disabled> oldugu icin bosaltilamaz. Bu nedenle "
            + "'gender bos -> Save pasif' durumu UI uzerinden hic olusturulamaz; "
            + "zorunluluk kurali secimin geri alinamamasiyla uygulanir. Test edilen budur.")
    public void genderCannotBeCleared() {
        UpdateCustomerPage update = openUpdateScreen();

        assertThat(update.genderValue())
                .as("ACC-002 — gender mevcut degerle dolu gelir").isNotEmpty();
        assertThat(update.isGenderPlaceholderDisabled())
                .as("placeholder secilemez, dolayisiyla gender bosaltilamaz").isTrue();

        // Diger secenege gecilebilir; form gecerli kalmalidir.
        update.selectGender(Gender.FEMALE);
        assertThat(update.isSaveEnabled())
                .as("gecerli bir gender secildiginde Save aktif").isTrue();
    }
}
