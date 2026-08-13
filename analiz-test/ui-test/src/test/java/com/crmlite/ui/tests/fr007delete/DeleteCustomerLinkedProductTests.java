package com.crmlite.ui.tests.fr007delete;

import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.api.BillingAccountApi;
import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.BillingAccountResponse;
import com.crmlite.ui.pages.components.ConfirmDialogComponent;
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
 * FR-007 ACC-004 — pasif fatura hesabina bagli urun bulunmasi.
 *
 * <p>Dokuman (ACC-004) sunu sart kosar:
 * <blockquote>
 * "Musterinin fatura hesabi pasif olsa dahi, bu hesaba bagli bir urun bulunmasi
 * durumunda silme islemi gerceklestirilememeli ve kullaniciya bilgilendirme
 * mesaji gosterilmelidir."
 * </blockquote>
 *
 * <p>12.08.2026: test <b>gercek govdesiyle yazildi, acildi ve GECTI</b>. Daha once
 * {@code enabled = false} bir taslakti; on kosul (hesabi pasiflestirme) o donemde
 * kurulamiyordu, artik {@link BillingAccountApi#deactivate} ile kurulabiliyor.
 *
 * <p><b>Kural uygulanmis durumda.</b> {@code BillingAccountBusinessRules} icindeki
 * "urun guard'i musteri silme akisinda cagrilmaz" yorumu YANILTICIDIR - kosum bunun
 * aksini gosterdi. Kaynak yorumuna guvenip bulguyu acik saymak yerine testin kendisi
 * otorite kabul edilmistir.
 *
 * <p>Test yanlis sebeple yesil vermesin diye on kosulunu KANITLAR: hesabin gercekten
 * pasif oldugunu dogrular ve engelin ACC-003'ten (aktif hesap) gelmedigini ayrica
 * assert eder. Aksi halde hesap aktif kalsaydi silme yine engellenir ve test dogru
 * davranisi yanlis gerekceyle onaylamis olurdu.
 */
@Epic("FR-007 Musteri Silme")
@Feature("UC-EACRML-007")
public class DeleteCustomerLinkedProductTests extends AuthenticatedTest {

    @Test(groups = {"fr007", "regression"},
            description = "UI-FR007-05 | Pasif hesaba bagli urun varken musteri silinemez")
    @Story("ACC-004 — Bagli urun silmeyi engeller")
    @TmsLink("FR-007-ACC-004")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Mesajin METNI dokumanda belirtilmedigi icin assert edilmez; dokumanin sart "
            + "kostugu DAVRANIS dogrulanir: silme gerceklesmemeli ve bir bilgilendirme "
            + "gosterilmelidir. Engelin dogru kuraldan geldigi ayrica dogrulanir.")
    public void customerWithProductOnPassiveAccountCannotBeDeleted() {
        // On kosul: musteri + fatura hesabi + o hesaba bagli urun.
        TestDataFactory.CustomerWithProduct data = TestDataFactory.customerWithAccountProduct();
        long custId = data.customer().custId();

        // Hesap PASIFLESTIRILIR - ACC-004 tam olarak bu durumu tarif eder: hesap pasif
        // olsa bile bagli urun varsa silme engellenmelidir.
        BillingAccountResponse passiveAccount =
                BillingAccountApi.deactivate(custId, data.account().custAcctId());

        // ON KOSUL KANITLANIR. Hesap hala AKTIF kalsaydi silmeyi ACC-003 kurali
        // engellerdi ve bu test DOGRU SEBEPLE degil, yanlis sebeple yesil verirdi.
        assertThat(passiveAccount.active())
                .as("on kosul: hesap gercekten pasiflestirildi").isFalse();

        CustomerDetailPage detail = openCustomerDetail(custId);
        ConfirmDialogComponent dialog = detail.clickDeleteCustomer();
        dialog.confirm();

        assertThat(dialog.hasError())
                .as("ACC-004 — bilgilendirme mesaji gosterilmelidir").isTrue();
        // Engel ACC-003'ten (aktif hesap) DEGIL, bagli urunden gelmelidir.
        assertThat(dialog.errorText())
                .as("ACC-004 — engel aktif hesaptan degil, bagli urunden kaynaklanmali")
                .isNotEqualTo(ExpectedMessages.get("detail.customerHasActiveBillingAccount"));
        assertThat(detail.customerId())
                .as("ACC-004 — silme gerceklesmemeli, musteri detayinda kalinmali")
                .isEqualTo(String.valueOf(custId));
    }
}
