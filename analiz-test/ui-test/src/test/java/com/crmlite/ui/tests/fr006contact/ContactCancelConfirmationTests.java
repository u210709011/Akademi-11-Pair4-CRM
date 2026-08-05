package com.crmlite.ui.tests.fr006contact;

import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.pages.components.ConfirmDialogComponent;
import com.crmlite.ui.pages.components.ContactModalComponent;
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
 * FR-006 ACC-006 — Cancel onay mesaji.
 *
 * <p><b>BILINEN UYUMSUZLUK — bu sinif kirmizi kalmasi beklenerek yazilmistir.</b>
 *
 * <p>Dokuman (ACC-006 ve UC-EACRML-006 Alternatif Senaryo 2, Adim 5.2) sunu sart kosar:
 * <blockquote>
 * "Sistem, degisikliklerin kaydedilmeden Contact Medium ekranina donulecegine dair
 * mesaj gosterir. Kullanici onaylar ve sistem degisiklikleri kaydetmeden doner."
 * </blockquote>
 *
 * <p>Uygulamada boyle bir onay adimi yok: {@code closeContactModal()} modal'i dogrudan
 * kapatiyor (bkz. {@code detail-customer.component.ts}); ayrica {@code .modal-backdrop}
 * tiklamasi da ayni metodu cagirdigi icin modal yanlislikla kapatilabiliyor.
 *
 * <p>Talimat geregi test <b>requirement'a gore</b> yazilmistir. Uygulama duzeltilirse
 * test kendiliginden yesile doner; dokuman degisirse bu sinif silinmelidir.
 *
 * <p>Not: "degisiklikler kaydedilmez" kismi ayri bir test olarak
 * {@code ContactMediumTests.cancelDiscardsChanges()} icinde dogrulanir ve o test gecer —
 * eksik olan yalnizca <b>onay mesaji adimidir</b>.
 */
@Epic("FR-006 Iletisim Bilgileri Yonetimi")
@Feature("UC-EACRML-006")
public class ContactCancelConfirmationTests extends AuthenticatedTest {

    @Test(groups = {"fr006", "documented-gap"},
            description = "UI-FR006-10 | Cancel'da kaydedilmeden donulecegine dair onay mesaji gosterilir")
    @Story("ACC-006 — Cancel onay mesaji")
    @TmsLink("FR-006-ACC-006")
    @Issue("FR-006-GAP-ACC006")
    @Severity(SeverityLevel.NORMAL)
    @Description("Dokuman Cancel sonrasi bir onay mesaji sart kosuyor; uygulama modal'i "
            + "dogrudan kapatiyor. Bu test uyumsuzlugu gorunur kilar.")
    public void cancelShowsUnsavedChangesConfirmation() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openContactTab();

        ContactModalComponent modal = detail.openEditContactModal();
        modal.enterEmail("vazgecilecek@example.com");

        ConfirmDialogComponent confirmation = modal.cancelExpectingConfirmation();

        assertThat(confirmation.isOpen())
                .as("ACC-006 — kaydedilmemis degisiklikler icin onay mesaji gosterilmelidir")
                .isTrue();
    }
}
