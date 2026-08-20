package com.crmlite.ui.tests.fr006contact;

import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.data.model.ValidationCase;
import com.crmlite.ui.data.provider.ValidationDataProvider;
import com.crmlite.ui.pages.components.ContactModalComponent;
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
 * FR-006 ACC-005 — Save butonu aktiflik matrisi ve validasyon kurallari.
 *
 * <p>Kurallar <b>FR-006 tablosundan</b> alinir (FR-003'ten degil):
 * <ul>
 *   <li>Email — zorunlu, gecerli e-posta formati</li>
 *   <li>Mobile Phone — zorunlu, yalnizca rakam, 10 hane, 5 ile baslar</li>
 *   <li>Home Phone — <b>zorunlu degil</b>, yalnizca rakam, <b>10-11 hane</b></li>
 *   <li>Fax — zorunlu degil, yalnizca rakam, gecerli faks formati</li>
 * </ul>
 *
 * <p>FR-003 tablosu Home Phone icin "2 ile baslar, 10 hane" der; bu ekran icin
 * bilincli olarak FR-006 surumu esas alinmistir.
 */
@Epic("FR-006 Iletisim Bilgileri Yonetimi")
@Feature("UC-EACRML-006")
public class ContactMediumValidationTests extends AuthenticatedTest {

    @Test(groups = {"fr006", "regression"},
            dataProvider = "contactMediumValidation", dataProviderClass = ValidationDataProvider.class,
            description = "UI-FR006-07 | Save butonu, alan gecerliligine gore aktiflesir")
    @Story("ACC-005 — Zorunlu ve format kurallari")
    @TmsLink("FR-006-ACC-005")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Her satir bir alani test eder; digerleri gecerli birakilir, "
            + "boylece Save durumu yalnizca test edilen alandan etkilenir.")
    public void saveButtonStateMatchesFieldValidity(ValidationCase testCase) {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openContactTab();

        ContactModalComponent modal = detail.openEditContactModal();
        ContactModalComponent.Field field = ContactModalComponent.Field.valueOf(testCase.field());

        modal.enterAndBlur(field, testCase.value());

        if (testCase.expectsActionEnabled()) {
            assertThat(modal.isSaveEnabled())
                    .as("ACC-005 — gecerli deger: Save aktif | %s", testCase).isTrue();
        } else {
            assertThat(modal.isSaveDisabled())
                    .as("ACC-005 — gecersiz deger: Save pasif | %s", testCase).isTrue();
        }
    }

    @Test(groups = {"fr006", "regression"},
            dataProvider = "contactMediumErrorMessages", dataProviderClass = ValidationDataProvider.class,
            description = "UI-FR006-08 | Gecersiz alanlarda dokumandaki hata mesaji gosterilir")
    @Story("Validasyon — Hata mesajlari")
    @TmsLink("FR-006-VAL-MESSAGES")
    @Severity(SeverityLevel.NORMAL)
    @Description("Beklenen metinler dokumandaki validasyon tablosundan alinir "
            + "(expected/messages_en.properties). Uygulama farkli metin kullaniyorsa "
            + "bu test uyumsuzlugu ortaya cikarir. Provider yalnizca hata bekleyen satirlari "
            + "verir; pozitif satirlar saveButtonStateMatchesFieldValidity tarafindan kontrol edilir.")
    public void invalidFieldShowsDocumentedErrorMessage(ValidationCase testCase) {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openContactTab();

        ContactModalComponent modal = detail.openEditContactModal();
        ContactModalComponent.Field field = ContactModalComponent.Field.valueOf(testCase.field());

        modal.enterAndBlur(field, testCase.value());

        assertThat(modal.hasFieldError(field))
                .as("hata mesaji gorunur olmalidir | %s", testCase).isTrue();
        assertThat(modal.fieldError(field))
                .as("dokumandaki hata metni | %s", testCase)
                .isEqualTo(ExpectedMessages.get(testCase.expectedMessageKey()));
    }

    @Test(groups = {"fr006", "regression"},
            description = "UI-FR006-09 | Telefon alanlarina rakam disi karakter girilemez")
    @Story("Validasyon — Yalnizca rakam")
    @TmsLink("FR-006-VAL-DIGITS")
    @Severity(SeverityLevel.NORMAL)
    @Description("Dokuman telefon ve faks alanlari icin 'yalnizca rakam' der; "
            + "uygulama harf girisini giris aninda engeller.")
    public void phoneFieldsRejectNonNumericInput() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openContactTab();

        ContactModalComponent modal = detail.openEditContactModal();
        modal.enterAndBlur(ContactModalComponent.Field.HOME_PHONE, "abcdefghij");

        assertThat(modal.homePhoneValue())
                .as("rakam disi karakterler alana yazilmamalidir").doesNotContainPattern("[a-zA-Z]");
    }
}
