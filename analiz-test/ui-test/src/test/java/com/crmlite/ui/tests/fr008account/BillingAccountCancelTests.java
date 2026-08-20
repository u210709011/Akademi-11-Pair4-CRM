package com.crmlite.ui.tests.fr008account;

import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.pages.components.BillingAccountModalComponent;
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

/**
 * FR-008 ACC-006 / ACC-010 — Cancel uyari adimlari.
 *
 * <p><b>BILINEN UYUMSUZLUK — bu sinif kirmizi kalmasi beklenerek yazilmistir.</b>
 *
 * <p>Dokuman iki ayri noktada onay adimi sart kosar:
 * <ul>
 *   <li><b>ACC-006</b>: yeni adres ekraninda Cancel → uyari → onaylanirsa adres
 *       kaydedilmeden Create Billing Account ekranina donulur</li>
 *   <li><b>ACC-010</b>: Create Billing Account ekraninda Cancel → uyari → onaylanirsa
 *       Customer Account ekranina donulur, <b>onaylanmazsa ayni ekranda kalinir</b></li>
 * </ul>
 *
 * <p>Uygulamada hicbir onay adimi yok: {@code closeCreateAccountModal()} modal'i dogrudan
 * kapatiyor, {@code .modal-backdrop} tiklamasi da ayni metodu cagiriyor. Ayrica ACC-006'nin
 * varsaydigi <b>ayri yeni-adres ekrani</b> da yok — adres, ayni modal icinde satir ici bir
 * form ile aliniyor ve hesapla birlikte tek istekte kaydediliyor. Bu yuzden ACC-006 icin
 * "kendi Cancel'i olan bir adres ekrani" hic bulunmuyor.
 *
 * <p>Testler dokumana gore yazildi. Uygulama duzeltilirse kendiliginden yesile doner;
 * dokuman degisirse bu sinif silinmelidir.
 */
@Epic("FR-008 Fatura Hesabi Olusturma")
@Feature("UC-EACRML-008")
public class BillingAccountCancelTests extends AuthenticatedTest {

    @Test(groups = {"fr008", "regression"},
            description = "UI-FR008-14 | Create Billing Account ekraninda Cancel uyari gosterir")
    @Story("ACC-010 — Cancel uyarisi")
    @TmsLink("FR-008-ACC-010")
    @Severity(SeverityLevel.NORMAL)
    @Description("Dokuman Cancel sonrasi onay mesaji sart kosuyor; uygulama modal'i dogrudan "
            + "kapatiyor ve girilen veriler uyarisiz kayboluyor.")
    public void cancelShowsUnsavedChangesWarning() {
        CreatedCustomer customer = TestDataFactory.customerWithAddresses(1);
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        BillingAccountModalComponent modal = detail.openCreateAccountModal();
        modal.enterAccountName("Vazgecilecek Hesap").enterAccountDescription("Aciklama");

        ConfirmDialogComponent confirmation = modal.cancelExpectingConfirmation();

        assertThat(confirmation.isOpen())
                .as("ACC-010 — kaydedilmemis degisiklikler icin uyari gosterilmelidir").isTrue();
    }

    @Test(groups = {"fr008", "regression"},
            description = "UI-FR008-15 | Cancel onaylanmazsa Create Billing Account ekraninda kalinir")
    @Story("ACC-010 — Onaylanmazsa ayni ekranda kalinir")
    @TmsLink("FR-008-ACC-010")
    @Severity(SeverityLevel.NORMAL)
    @Description("ACC-010'un ikinci yarisi: uyari reddedilirse modal acik kalmali ve "
            + "girilen veriler korunmalidir.")
    public void decliningCancelKeepsModalOpen() {
        CreatedCustomer customer = TestDataFactory.customerWithAddresses(1);
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        BillingAccountModalComponent modal = detail.openCreateAccountModal();
        modal.enterAccountName("Korunacak Hesap").enterAccountDescription("Aciklama");

        ConfirmDialogComponent confirmation = modal.cancelExpectingConfirmation();
        if (confirmation.isOpen()) {
            confirmation.cancel();
            confirmation.waitUntilClosed();
        }

        assertThat(modal.isOpen())
                .as("ACC-010 — uyari reddedilince Create Billing Account ekraninda kalinmalidir")
                .isTrue();
    }

    @Test(groups = {"fr008", "regression"},
            description = "UI-FR008-18 | Yeni adres modunda Create/Cancel butonlarina erisilebilir")
    @Story("ACC-011 — Hesap olusturulabilmeli")
    @TmsLink("FR-008-ACC-011")
    @Severity(SeverityLevel.CRITICAL)
    @Description("GERCEK URUN HATASI. Yeni adres modunda modal viewport'tan uzun oluyor ve "
            + "Create/Cancel butonlari ekranin altinda kaliyor. .modal-backdrop position:fixed "
            + "ve kaydirilamaz, .modal-card'da max-height/overflow yok (styles.scss) — bu yuzden "
            + "kullanici kaydirarak da ulasamiyor ve YENI ADRESLE HESAP OLUSTURAMIYOR. "
            + "1920x914 maximize pencerede tekrarlanabilir.")
    public void newAddressModalActionsAreReachable() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        BillingAccountModalComponent modal = detail.openCreateAccountModal();
        modal.enterAccountName("Erisim Testi").enterAccountDescription("Aciklama");
        modal.toggleAddressMode();
        modal.fillNewAddress("Tasma Sokak", "No:1", "Tasma adresi");

        assertThat(modal.areActionsInViewport())
                .as("yeni adres modunda Create/Cancel butonlari goruntulenen alanda olmalidir")
                .isTrue();
    }

    @Test(groups = {"fr008", "regression"},
            description = "UI-FR008-16 | Yeni adres formunda Cancel uyari gosterir")
    @Story("ACC-006 — Yeni adres ekraninda Cancel uyarisi")
    @TmsLink("FR-008-ACC-006")
    @Severity(SeverityLevel.MINOR)
    @Description("Dokuman ayri bir yeni-adres ekrani ve o ekranda Cancel uyarisi tarif ediyor. "
            + "Uygulamada ayri ekran yok; adres satir ici formda aliniyor ve kendi Cancel'i "
            + "bulunmuyor. En yakin karsilik modal'in Cancel'idir, o da uyari gostermiyor.")
    public void newAddressCancelShowsWarning() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        BillingAccountModalComponent modal = detail.openCreateAccountModal();
        modal.enterAccountName("Adres Iptali").enterAccountDescription("Aciklama");
        modal.toggleAddressMode();
        modal.fillNewAddress("Iptal Sokak", "No:3", "Iptal edilecek adres");

        ConfirmDialogComponent confirmation = modal.cancelExpectingConfirmation();

        assertThat(confirmation.isOpen())
                .as("ACC-006 — yeni adres vazgecildiginde uyari gosterilmelidir").isTrue();
    }
}
