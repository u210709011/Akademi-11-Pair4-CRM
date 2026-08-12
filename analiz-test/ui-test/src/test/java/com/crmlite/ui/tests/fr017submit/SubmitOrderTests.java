package com.crmlite.ui.tests.fr017submit;
import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.pages.customer.CustomerDetailPage;
import com.crmlite.ui.pages.newsale.ConfigurationStepPage;
import com.crmlite.ui.pages.newsale.OfferSelectionPage;
import com.crmlite.ui.pages.newsale.ReviewStepPage;
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
 * FR-017 — Siparisi Tamamlama (UC-EACRML-017).
 *
 * <p><b>DOKUMAN NOTU:</b> kabul kriterleri dokumanda UC-EACRML-016 basliginin ALTINDA yer
 * alir; bloklar ait olduklari basliktan once gelir.
 */
@Epic("FR-017 Siparisi Tamamlama")
@Feature("UC-EACRML-017")
public class SubmitOrderTests extends AuthenticatedTest {

    private static final String COMMON_NAME_FRAGMENT = "Home";

    @Test(groups = {"fr017", "regression"},
            description = "UI-FR017-01 | Previous, bilgiler korunarak Configuration'a doner")
    @Story("ACC-001 — Previous ile geri donus")
    @TmsLink("FR-017-ACC-001")
    @Severity(SeverityLevel.CRITICAL)
    public void previousReturnsToConfigurationKeepingData() {
        ReviewStepPage review = openReview();
        OfferSelectionPage wizard = new OfferSelectionPage(driver());

        wizard.clickPrevious();

        assertThat(wizard.activeStepLabel())
                .as("ACC-001 — Product Configuration adimina donulur")
                .isEqualTo(ExpectedMessages.get("newSale.stepConfiguration"));

        ConfigurationStepPage config = new ConfigurationStepPage(driver());
        assertThat(config.hasSelectedAddress())
                .as("ACC-001 — girilen servis adresi korunur").isTrue();
        assertThat(review.isDisplayed())
                .as("Review ekrani artik gosterilmez").isFalse();
    }

    @Test(groups = {"fr017", "regression"},
            description = "UI-FR017-02 | Submit onay mesaji gosterir")
    @Story("ACC-002 — Submit onayi")
    @TmsLink("FR-017-ACC-002")
    @Severity(SeverityLevel.CRITICAL)
    @Description("12.08.2026: onay diyalogu uygulandi (commit 779b22e) ve test acildi. "
            + "Sepetten sonraki tek geri donulemez adim burasi oldugu icin onay kritik.")
    public void submitAsksForConfirmation() {
        ReviewStepPage review = openReview();
        OfferSelectionPage wizard = new OfferSelectionPage(driver());

        assertThat(wizard.nextButtonLabel())
                .as("son adimda buton Submit etiketlidir")
                .isEqualTo(ExpectedMessages.get("newSale.submitBtn"));

        wizard.clickNext();

        assertThat(review.hasCancelConfirmDialog())
                .as("ACC-002 — Submit onay diyalogu gosterilmelidir").isTrue();
        assertThat(review.confirmDialogMessage())
                .as("ACC-002 — onay mesaji")
                .isEqualTo(ExpectedMessages.get("newSale.submitConfirmMessage"));
    }

    @Test(groups = {"smoke", "fr017"},
            description = "UI-FR017-03 | Siparis iletilir ve basari mesaji gosterilir")
    @Story("ACC-003, ACC-004 — Siparisin iletilmesi")
    @TmsLink("FR-017-ACC-004")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Dokuman onaydan sonra siparisin orta katmana iletilmesini ve basari "
            + "mesaji gosterilmesini sart kosar. Uygulamada onay adimi bulunmadigi icin "
            + "(bkz. FR-017-GAP-ACC002) Submit dogrudan siparisi gonderir.")
    public void submitSendsOrderAndShowsSuccessMessage() {
        ReviewStepPage review = openReview();
        OfferSelectionPage wizard = new OfferSelectionPage(driver());

        wizard.clickNext();
        // Submit artik once onay ister (ACC-002); siparis ancak onaylandiktan sonra gider.
        review.confirmSubmit();

        assertThat(review.hasSuccessModal())
                .as("ACC-003 — siparis iletilir").isTrue();
        assertThat(review.successTitle())
                .as("ACC-004 — basari basligi")
                .isEqualTo(ExpectedMessages.get("newSale.orderSuccessTitle"));
        assertThat(review.successMessage())
                .as("ACC-004 — basari mesaji")
                .isEqualTo(ExpectedMessages.get("newSale.orderSuccessMessage"));
    }

    @Test(groups = {"fr017", "regression"},
            description = "UI-FR017-04 | Basarili siparis sonrasi musteri ekranina donulur")
    @Story("ACC-005 — Musteri ekranina yonlendirme")
    @TmsLink("FR-017-ACC-005")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Dokuman basarili islem sonrasi kullanicinin Customer Information ekranina "
            + "YONLENDIRILMESINI sart kosar. Uygulama otomatik yonlendirme yerine basari "
            + "modali gosterip \"Go to Billing Account\" butonuyla donusu kullaniciya birakir; "
            + "test gozlemlenebilir sonucu (musteri ekranina varis) dogrular.")
    public void userReturnsToCustomerScreenAfterSubmit() {
        ReviewStepPage review = openReview();
        OfferSelectionPage wizard = new OfferSelectionPage(driver());

        wizard.clickNext();
        review.confirmSubmit();
        assertThat(review.hasSuccessModal()).as("on kosul: siparis iletildi").isTrue();

        review.goToBillingAccount();

        assertThat(review.currentUrl())
                .as("ACC-005 — musteri ekranina donulur")
                .contains("/detail-customer/");
        assertThat(review.currentUrl())
                .as("ACC-005 — satis sihirbazindan cikilir")
                .doesNotContain("/new-sale/");
    }

    /** Sepete urun ekler, servis adresi secer ve Review adimina gecer. */
    private ReviewStepPage openReview() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();
        detail.toggleAccountRow(0);

        OfferSelectionPage wizard = detail.startNewSale();
        wizard.enterOfferName(COMMON_NAME_FRAGMENT);
        wizard.searchUntilResults();
        wizard.addToBasket(0);
        wizard.clickNext();

        ConfigurationStepPage config = new ConfigurationStepPage(driver());
        config.waitUntilLoaded();
        if (!config.hasSelectedAddress()) {
            config.openChangeAddressModal();
            config.selectAddressOption(0);
        }
        // Next, isConfigurationComplete ile korunur: servis adresi TEK BASINA yetmez,
        // zorunlu karakteristiklerin de doldurulmus olmasi gerekir.
        config.fillAllConfigurationFields();

        // On kosul ASSERT edilir, atlanmaz: bkz. OrderSummaryTests'teki ayni not.
        assertThat(wizard.isNextEnabled())
                .as("on kosul: konfigurasyon tamamlandiginda Next aktiflesmelidir").isTrue();
        wizard.clickNext();
        wizard.waitForActiveStep(ExpectedMessages.get("newSale.stepReview"));

        ReviewStepPage review = new ReviewStepPage(driver());
        review.waitUntilLoaded();
        return review;
    }
}
