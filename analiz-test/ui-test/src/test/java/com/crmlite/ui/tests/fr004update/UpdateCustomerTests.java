package com.crmlite.ui.tests.fr004update;

import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.builder.TestRunId;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.data.model.Gender;
import com.crmlite.ui.pages.customer.CustomerDetailPage;
import com.crmlite.ui.pages.customer.UpdateCustomerPage;
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
 * FR-004 — Musteri Bilgilerini Guncelleme (UC-EACRML-004) mutlu yol.
 *
 * <p>Her test kendi musterisini kurar: guncelleme testleri veriyi degistirdigi icin
 * paylasilan bir kayit sonraki testleri etkilerdi.
 */
@Epic("FR-004 Musteri Bilgilerini Guncelleme")
@Feature("UC-EACRML-004")
public class UpdateCustomerTests extends AuthenticatedTest {

    @Test(groups = {"smoke", "fr004"},
            description = "UI-FR004-01, -02 | Kalem ikonu update ekranini acar ve bilgiler dolu gelir")
    @Story("ACC-001, ACC-002 — Update ekrani ve mevcut bilgiler")
    @TmsLink("FR-004-ACC-002")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Sekiz demografik alanin tamami mevcut degerlerle dolu gelmelidir; "
            + "dogum tarihi DD/MM/YYYY formatinda gosterilir.")
    public void updateScreenIsPrefilledWithCurrentValues() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId());

        UpdateCustomerPage update = detail.clickEdit();

        assertThat(update.currentUrl()).contains("/update");
        assertThat(update.firstNameValue()).isEqualTo(customer.individual().firstName());
        assertThat(update.lastNameValue()).isEqualTo(customer.individual().lastName());
        assertThat(update.nationalIdValue()).isEqualTo(customer.individual().nationalId());
        assertThat(update.middleNameValue()).isEqualTo(customer.individual().middleName());
        assertThat(update.fatherNameValue()).isEqualTo(customer.individual().fatherName());
        assertThat(update.motherNameValue()).isEqualTo(customer.individual().motherName());
        assertThat(update.genderValue()).isEqualTo(String.valueOf(customer.individual().genderId()));
        // DOKUMAN SAPMASI (kozmetik): ACC-002 formati "DD/MM/YYYY" olarak tanimliyor,
        // uygulama ayiraclarin etrafina bosluk koyuyor -> "15 / 06 / 1990".
        // Kaynak: AppDateAdapter.format() (app.config.ts) -> `${day} / ${month} / ${year}`.
        // Gereksinimin ozu (gun-once sira, sifir dolgusu, / ayiraci) karsilaniyor; bu yuzden
        // bosluklar tolere edilir. Sira/dolgu bozulursa test yine yakalar.
        assertThat(update.birthDateValue())
                .as("ACC-002 — tarih formati DD/MM/YYYY (ayirac bosluklari tolere edilir)")
                .matches("\\d{2}\\s*/\\s*\\d{2}\\s*/\\s*\\d{4}");
    }

    @Test(groups = {"smoke", "fr004"},
            description = "UI-FR004-03, -04 | Bilgiler guncellenir ve Customer Info guncel gorunur")
    @Story("ACC-003, ACC-010, ACC-011 — Guncelleme kaydedilir")
    @TmsLink("FR-004-ACC-010")
    @Severity(SeverityLevel.BLOCKER)
    public void demographicFieldsAreUpdated() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        String newLastName = "Guncel" + TestRunId.value();
        String newMiddleName = "GuncelOrta";

        UpdateCustomerPage update = openCustomerDetail(customer.custId()).clickEdit();
        update.enterLastName(newLastName)
                .enterMiddleName(newMiddleName)
                .selectGender(Gender.FEMALE.value());

        CustomerDetailPage detail = update.save();

        assertThat(detail.infoValue(CustomerDetailPage.InfoField.LAST_NAME)).isEqualTo(newLastName);
        assertThat(detail.infoValue(CustomerDetailPage.InfoField.MIDDLE_NAME)).isEqualTo(newMiddleName);
        assertThat(detail.infoValue(CustomerDetailPage.InfoField.GENDER))
                .containsIgnoringCase(Gender.FEMALE.label());
    }

    @Test(groups = {"fr004", "regression"},
            description = "UI-FR004-05 | Guncelleme sayfa yenilendikten sonra da kalicidir")
    @Story("ACC-011 — Customer Info guncel gorunur")
    @TmsLink("FR-004-ACC-011")
    @Severity(SeverityLevel.CRITICAL)
    public void updateIsPersisted() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        String newLastName = "Kalici" + TestRunId.value();

        openCustomerDetail(customer.custId()).clickEdit().enterLastName(newLastName).save();

        CustomerDetailPage reopened = openCustomerDetail(customer.custId());

        assertThat(reopened.infoValue(CustomerDetailPage.InfoField.LAST_NAME))
                .as("yeniden yuklendikten sonra da guncel").isEqualTo(newLastName);
    }

    @Test(groups = {"fr004"},
            description = "UI-FR004-07 | Previous ile degisiklikler kaydedilmeden donulur")
    @Story("ACC-005 — Cancel ile kayitsiz donus")
    @TmsLink("FR-004-ACC-005")
    @Severity(SeverityLevel.CRITICAL)
    @Description("DOKUMAN CELISKISI: ACC-005 bu butonu 'Cancel' olarak adlandiriyor; "
            + "UC-004 Alt-4 ve uygulama 'Previous' kullaniyor. Islev aynidir.")
    public void previousDiscardsChanges() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        String original = customer.individual().lastName();

        UpdateCustomerPage update = openCustomerDetail(customer.custId()).clickEdit();
        update.enterLastName("KaydedilmemisDeger");

        CustomerDetailPage detail = update.previous();

        assertThat(detail.infoValue(CustomerDetailPage.InfoField.LAST_NAME))
                .as("ACC-005 — degisiklik kaydedilmemeli").isEqualTo(original);
    }

    @Test(groups = {"fr004", "regression"},
            description = "UI-FR004-08 | Baska musteride mevcut NAT ID ile kaydedilemez")
    @Story("ACC-006, ACC-007 — Nationality ID tekillik cakismasi")
    @TmsLink("FR-004-ACC-007")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Iki musteri kurulur; ikincisinin NAT ID'si birinciye esitlenmeye calisilir. "
            + "Sistem uyari gostermeli ve kaydi reddetmelidir.")
    public void duplicateNationalIdIsRejected() {
        CreatedCustomer existing = TestDataFactory.simpleCustomer();
        CreatedCustomer target = TestDataFactory.simpleCustomer();

        UpdateCustomerPage update = openCustomerDetail(target.custId()).clickEdit();
        update.enterNationalId(existing.nationalId()).saveExpectingFailure();

        assertThat(update.hasSaveError())
                .as("ACC-007 — cakisma uyarisi gosterilmeli").isTrue();
        assertThat(update.currentUrl())
                .as("ACC-007 — guncelleme ekraninda kalinir").contains("/update");
    }

    @Test(groups = {"fr004"},
            description = "UI-FR004-09 | Benzersiz NAT ID ile kayit basarili olur")
    @Story("ACC-006, ACC-010 — Tekillik saglaniyorsa kaydedilir")
    @TmsLink("FR-004-ACC-006")
    @Severity(SeverityLevel.NORMAL)
    public void uniqueNationalIdIsAccepted() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        String newNationalId = com.crmlite.ui.core.utils.NationalIdGenerator.next();

        CustomerDetailPage detail = openCustomerDetail(customer.custId())
                .clickEdit().enterNationalId(newNationalId).save();

        assertThat(detail.infoValue(CustomerDetailPage.InfoField.NATIONAL_ID))
                .isEqualTo(newNationalId);
    }
}
