package com.crmlite.ui.tests.fr006contact;

import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.CreatedCustomer;
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
 * FR-006 — Iletisim Bilgileri Yonetimi (UC-EACRML-006): goruntuleme ve guncelleme.
 *
 * <p>Her test kendi musterisini kurar; guncelleme testleri veriyi degistirdigi icin
 * paylasilan musteri kullanilamaz. On kosul iletisim bilgileri API ile hazirlanir,
 * dogrulanan davranis UI'dadir.
 */
@Epic("FR-006 Iletisim Bilgileri Yonetimi")
@Feature("UC-EACRML-006")
public class ContactMediumTests extends AuthenticatedTest {

    @Test(groups = {"smoke", "fr006"},
            description = "UI-FR006-01 | Contact Medium tabinda iletisim bilgileri goruntulenir")
    @Story("ACC-001 — Iletisim bilgileri listelenir")
    @TmsLink("FR-006-ACC-001")
    @Severity(SeverityLevel.BLOCKER)
    public void contactTabDisplaysContactInformation() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openContactTab();

        assertThat(detail.contactRowCount())
                .as("ACC-001 — dort iletisim alani gosterilir").isEqualTo(4);
        assertThat(detail.contactValue(CustomerDetailPage.ContactField.EMAIL))
                .as("ACC-001 — e-posta musterinin verisiyle eslesir")
                .isEqualTo(customer.contact().email());
        assertThat(detail.contactValue(CustomerDetailPage.ContactField.MOBILE_PHONE))
                .as("ACC-001 — cep telefonu gosterilir")
                .contains(customer.contact().mobilePhone());
    }

    @Test(groups = {"smoke", "fr006"},
            description = "UI-FR006-02 | Kalem ikonu Contact Medium Update ekranini acar")
    @Story("ACC-002 — Kalem ikonu guncelleme ekranini acar")
    @TmsLink("FR-006-ACC-002")
    @Severity(SeverityLevel.BLOCKER)
    public void pencilIconOpensContactUpdateModal() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openContactTab();

        ContactModalComponent modal = detail.openEditContactModal();

        assertThat(modal.isOpen()).as("ACC-002 — guncelleme ekrani acilir").isTrue();
        assertThat(modal.title())
                .as("ACC-002 — modal basligi")
                .isEqualTo(ExpectedMessages.get("detail.editContactTitle"));
    }

    @Test(groups = {"smoke", "fr006"},
            description = "UI-FR006-03 | Mevcut iletisim bilgileri guncelleme ekraninda dolu gelir")
    @Story("ACC-003 — Mevcut bilgiler dolu gosterilir")
    @TmsLink("FR-006-ACC-003")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Modal, musterinin API ile kurulan iletisim bilgileriyle dolu acilmalidir.")
    public void existingContactValuesArePrefilled() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openContactTab();

        ContactModalComponent modal = detail.openEditContactModal();

        assertThat(modal.emailValue())
                .as("ACC-003 — e-posta dolu gelir").isEqualTo(customer.contact().email());
        assertThat(modal.mobilePhoneValue())
                .as("ACC-003 — cep telefonu dolu gelir").isEqualTo(customer.contact().mobilePhone());
        assertThat(modal.homePhoneValue())
                .as("ACC-003 — ev telefonu dolu gelir").isEqualTo(customer.contact().homePhone());
    }

    @Test(groups = {"smoke", "fr006"},
            description = "UI-FR006-04 | Guncellenen iletisim bilgileri kaydedilir ve ekrana yansir")
    @Story("ACC-004, ACC-007 — Guncelleme ve basari mesaji")
    @TmsLink("FR-006-ACC-007")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Save sonrasi basari mesaji gosterilir, modal kapanir ve yeni deger listede gorunur.")
    public void contactInformationIsUpdatedAndListed() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openContactTab();

        String newEmail = "guncel." + customer.custId() + "@example.com";
        ContactModalComponent modal = detail.openEditContactModal();
        modal.enterEmail(newEmail);
        modal.save();
        modal.waitUntilClosed();

        assertThat(detail.hasSuccessToast())
                .as("ACC-007 — basari mesaji gosterilir").isTrue();
        assertThat(detail.successToastText())
                .as("ACC-007 — basari mesaji metni")
                .isEqualTo(ExpectedMessages.get("detail.contactSaveSuccess"));
        assertThat(detail.contactValue(CustomerDetailPage.ContactField.EMAIL))
                .as("ACC-004 — guncel e-posta Contact Medium ekraninda gorunur")
                .isEqualTo(newEmail);
    }

    @Test(groups = {"fr006", "regression"},
            description = "UI-FR006-05 | Opsiyonel alanlar bos birakilarak kaydedilebilir")
    @Story("Validasyon — Home Phone ve Fax zorunlu degildir")
    @TmsLink("FR-006-VAL-OPTIONAL")
    @Severity(SeverityLevel.NORMAL)
    @Description("Dokuman Home Phone ve Fax'i zorunlu tutmaz; ikisi de bosken kayit gecmelidir.")
    public void optionalFieldsCanBeClearedAndSaved() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openContactTab();

        ContactModalComponent modal = detail.openEditContactModal();
        modal.enterHomePhone("").enterFax("");

        assertThat(modal.isSaveEnabled())
                .as("opsiyonel alanlar bosken Save aktif kalmalidir").isTrue();

        modal.save();
        modal.waitUntilClosed();

        assertThat(detail.hasSuccessToast())
                .as("opsiyonel alanlar bos kaydedilebilir").isTrue();
    }

    @Test(groups = {"fr006", "regression"},
            description = "UI-FR006-06 | Cancel degisiklikleri kaydetmeden Contact Medium ekranina doner")
    @Story("ACC-006 — Cancel ile vazgecme")
    @TmsLink("FR-006-ACC-006")
    @Severity(SeverityLevel.NORMAL)
    @Description("Cancel sonrasi degisiklik kaydedilmemeli ve Contact Medium ekranina donulmelidir.")
    public void cancelDiscardsChanges() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openContactTab();

        String originalEmail = detail.contactValue(CustomerDetailPage.ContactField.EMAIL);

        ContactModalComponent modal = detail.openEditContactModal();
        modal.enterEmail("vazgecilen@example.com");
        modal.cancel();
        modal.waitUntilClosed();

        assertThat(detail.contactValue(CustomerDetailPage.ContactField.EMAIL))
                .as("ACC-006 — degisiklik kaydedilmez").isEqualTo(originalEmail);
    }
}
