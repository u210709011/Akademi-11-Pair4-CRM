package com.crmlite.ui.tests.fr011accountdelete;

import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.api.AddressApi;
import com.crmlite.ui.data.api.BillingAccountApi;
import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.BillingAccountResponse;
import com.crmlite.ui.data.model.CreatedCustomer;
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
 * FR-011 — Billing Account Silme (UC-EACRML-011).
 *
 * <p>Silme kurallari <b>backend'de</b> uygulanir
 * ({@code BillingAccountBusinessRules.ensureBillingAccountNotActive} /
 * {@code ensureNoLinkedProducts}), bu yuzden arayuzun urun tablosunu gosterememesi
 * (FR-009 bulgusu) bu testleri etkilemez.
 *
 * <p>Arayuzde hesabi pasiflestiren bir kontrol yoktur; ACC-004/ACC-005 on kosullari
 * {@code BillingAccountApi.deactivate()} ile API uzerinden kurulur.
 */
@Epic("FR-011 Billing Account Silme")
@Feature("UC-EACRML-011")
public class DeleteBillingAccountTests extends AuthenticatedTest {

    @Test(groups = {"smoke", "fr011"},
            description = "UI-FR011-01 | Delete butonu onay diyalogu acar")
    @Story("ACC-001 — Silme onayi istenir")
    @TmsLink("FR-011-ACC-001")
    @Severity(SeverityLevel.BLOCKER)
    public void deleteButtonOpensConfirmDialog() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        ConfirmDialogComponent dialog = detail.deleteAccount(0);

        assertThat(dialog.isOpen())
                .as("ACC-001 — silme icin onay diyalogu gosterilir").isTrue();
    }

    @Test(groups = {"fr011", "documented-gap"},
            description = "UI-FR011-02 | Onay diyalogunda dokumandaki metin gosterilir")
    @Story("ACC-001 — Onay metni")
    @TmsLink("FR-011-ACC-001")
    @Issue("FR-011-GAP-ACC001-TEXT")
    @Severity(SeverityLevel.MINOR)
    @Description("BILINEN UYUMSUZLUK — kirmizi kalmasi beklenir. Dokuman "
            + "\"Are you sure to delete this billing account?\" metnini sart kosar; uygulama "
            + "hesap adini ve geri alinamazlik uyarisini iceren daha uzun bir metin gosteriyor. "
            + "Kural ayni, farkli olan yalnizca metin.")
    public void confirmDialogShowsDocumentedText() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        ConfirmDialogComponent dialog = detail.deleteAccount(0);

        assertThat(dialog.message())
                .as("ACC-001 — dokumandaki onay metni")
                .isEqualTo(ExpectedMessages.get("detail.deleteAccountConfirm"));
    }

    @Test(groups = {"smoke", "fr011"},
            description = "UI-FR011-03 | Aktif hesap silinemez, bilgilendirme mesaji gosterilir")
    @Story("ACC-002, ACC-003 — Aktif hesap engeli")
    @TmsLink("FR-011-ACC-003")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Yeni acilan fatura hesabi aktiftir. Dokuman silmenin engellenmesini ve "
            + "\"This billing account is active and cannot be deleted.\" mesajini sart kosar.")
    public void activeAccountCannotBeDeleted() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        int before = detail.accountCount();
        ConfirmDialogComponent dialog = detail.deleteAccount(0);
        dialog.confirm();

        assertThat(detail.hasCannotDeleteAccountDialog())
                .as("ACC-003 — silinemez uyarisi gosterilir").isTrue();
        assertThat(detail.cannotDeleteAccountMessage())
                .as("ACC-003 — dokumandaki mesaj")
                .isEqualTo(ExpectedMessages.get("detail.billingAccountActiveCannotDelete"));

        detail.closeCannotDeleteAccountDialog();

        assertThat(detail.accountCount())
                .as("ACC-003 — hesap silinmedi").isEqualTo(before);
    }

    @Test(groups = {"smoke", "fr011"},
            description = "UI-FR011-04 | Pasif ve urunsuz hesap silinir, basari mesaji gosterilir")
    @Story("ACC-005 — Soft delete")
    @TmsLink("FR-011-ACC-005")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Arayuzde hesabi pasiflestiren kontrol olmadigi icin on kosul API ile kurulur.")
    public void passiveAccountWithoutProductsIsDeleted() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        long addressId = AddressApi.primaryAddress(customer.custId()).id();
        BillingAccountResponse account =
                BillingAccountApi.create(customer.custId(), addressId, "Silinecek Hesap");
        BillingAccountApi.deactivate(customer.custId(), account.custAcctId());

        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        int before = detail.accountCount();
        ConfirmDialogComponent dialog = detail.deleteAccount(0);
        dialog.confirm();
        dialog.waitUntilClosed();
        detail.waitUntilAccountCountIsLessThan(before);

        assertThat(detail.hasSuccessToast())
                .as("ACC-005 — basari mesaji gosterilir").isTrue();
        assertThat(detail.accountCount())
                .as("ACC-005 — hesap listeden kalkti").isEqualTo(before - 1);
    }

    @Test(groups = {"fr011", "documented-gap"},
            description = "UI-FR011-05 | Pasif olsa da bagli urunu olan hesap silinemez")
    @Story("ACC-004 — Bagli urun engeli")
    @TmsLink("FR-011-ACC-004")
    @Issue("FR-011-GAP-ACC004")
    @Severity(SeverityLevel.CRITICAL)
    @Description("BILINEN UYUMSUZLUK — kirmizi kalmasi beklenir. Dokuman ACC-004, pasif olsa "
            + "dahi bagli urunu bulunan hesabin silinmesini engeller. Uygulamada bu kontrol "
            + "YOK: guard bagli ancak NoOpBillingAccountProductGuard her zaman false doner "
            + "(BillingAccountServiceImpl'deki yorum ve DELETE ucunun Swagger aciklamasi bunu "
            + "acikca soyluyor: 'order-service'i bekliyor, henuz uygulanmadi - TODO'). "
            + "Sonuc: urunu olan hesap sessizce siliniyor.")
    public void passiveAccountWithProductCannotBeDeleted() {
        TestDataFactory.CustomerWithProduct data = TestDataFactory.customerWithAccountProduct();
        BillingAccountApi.deactivate(data.customer().custId(), data.account().custAcctId());

        CustomerDetailPage detail =
                openCustomerDetail(data.customer().custId()).openAccountsTab();

        int before = detail.accountCount();
        ConfirmDialogComponent dialog = detail.deleteAccount(0);
        dialog.confirm();

        assertThat(detail.hasCannotDeleteAccountDialog())
                .as("ACC-004 — bagli urun nedeniyle silme engellenmelidir").isTrue();
        assertThat(detail.cannotDeleteAccountMessage())
                .as("ACC-004 — dokumandaki mesaj")
                .isEqualTo(ExpectedMessages.get("detail.billingAccountHasProductsCannotDelete"));
        assertThat(detail.accountCount())
                .as("ACC-004 — hesap silinmemelidir").isEqualTo(before);
    }

    @Test(groups = {"fr011", "regression"},
            description = "UI-FR011-06 | Onay diyalogunda iptal edilince hesap silinmez")
    @Story("ACC-001 — Onay reddi")
    @TmsLink("FR-011-ACC-001")
    @Severity(SeverityLevel.NORMAL)
    public void cancellingConfirmationKeepsAccount() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        long addressId = AddressApi.primaryAddress(customer.custId()).id();
        BillingAccountResponse account =
                BillingAccountApi.create(customer.custId(), addressId, "Korunacak Hesap");
        BillingAccountApi.deactivate(customer.custId(), account.custAcctId());

        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        int before = detail.accountCount();
        ConfirmDialogComponent dialog = detail.deleteAccount(0);
        dialog.cancel();
        dialog.waitUntilClosed();

        assertThat(detail.accountCount())
                .as("iptal edilince hesap silinmez").isEqualTo(before);
    }
}
