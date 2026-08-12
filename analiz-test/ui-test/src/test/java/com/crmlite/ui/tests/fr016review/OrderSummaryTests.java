package com.crmlite.ui.tests.fr016review;

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
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FR-016 — Siparis Ozeti Goruntuleme (UC-EACRML-016).
 *
 * <p><b>DOKUMAN NOTU:</b> kabul kriterleri dokumanda UC-EACRML-015 basliginin ALTINDA yer
 * alir; bloklar ait olduklari basliktan once gelir.
 *
 * <p>Review ekranina ulasmak icin sepette urun, secilmis servis adresi ve doldurulmus
 * zorunlu karakteristik alanlar gerekir. Bu on kosul ASSERT edilir, ATLANMAZ: saglanamamasi
 * gercek bir sorunun isaretidir ve gorunmesi gerekir.
 */
@Epic("FR-016 Siparis Ozeti Goruntuleme")
@Feature("UC-EACRML-016")
public class OrderSummaryTests extends AuthenticatedTest {

    private static final String COMMON_NAME_FRAGMENT = "Home";

    @Test(groups = {"smoke", "fr016"},
            description = "UI-FR016-01 | Configuration'da Next, Review & Submit ekranini acar")
    @Story("ACC-001 — Review ekrani acilir")
    @TmsLink("FR-016-ACC-001")
    @Severity(SeverityLevel.BLOCKER)
    public void nextOpensReviewAndSubmit() {
        ReviewStepPage review = openReview();

        assertThat(review.isDisplayed())
                .as("ACC-001 — Review & Submit ekrani acilir").isTrue();
    }

    @Test(groups = {"fr016", "regression"},
            description = "UI-FR016-02 | Siparise ait Order ID gosterilir")
    @Story("ACC-002 — Order ID")
    @TmsLink("FR-016-ACC-002")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Uygulama Order ID'yi \"ORD-{id}\" bicimiyle gosterir; dokuman bicim "
            + "belirtmedigi icin test yalnizca alanin VAR ve DOLU oldugunu dogrular.")
    public void orderIdIsShown() {
        ReviewStepPage review = openReview();
        String label = ExpectedMessages.get("newSale.orderId");

        assertThat(review.hasSummaryField(label))
                .as("ACC-002 — Order ID alani bulunur").isTrue();
        assertThat(review.summaryValueFor(label))
                .as("ACC-002 — Order ID degeri bos olmamalidir").isNotBlank();
    }

    @Test(groups = {"fr016", "regression"},
            description = "UI-FR016-03 | Order Items alaninda urunler ID ve ad ile listelenir")
    @Story("ACC-003 — Siparis kalemleri")
    @TmsLink("FR-016-ACC-003")
    @Severity(SeverityLevel.CRITICAL)
    public void orderItemsAreListedWithIdAndName() {
        ReviewStepPage review = openReview();

        assertThat(review.hasProductsTable())
                .as("ACC-003 — siparis kalemleri tablosu bulunur").isTrue();
        assertThat(review.productRowCount())
                .as("ACC-003 — en az bir kalem listelenir").isPositive();
        assertThat(review.productIds())
                .as("ACC-003 — her kalemde Prod Offer ID gosterilir")
                .isNotEmpty()
                .allSatisfy(id -> assertThat(id).isNotBlank());
    }

    @Test(groups = {"fr016", "regression"},
            description = "UI-FR016-04 | Siparisin servis adresi gosterilir")
    @Story("ACC-004 — Servis adresi")
    @TmsLink("FR-016-ACC-004")
    @Severity(SeverityLevel.CRITICAL)
    public void serviceAddressIsShown() {
        ReviewStepPage review = openReview();

        assertThat(review.hasServiceAddress())
                .as("ACC-004 — servis adresi gosterilir").isTrue();
        assertThat(review.serviceAddressText())
                .as("ACC-004 — adres metni bos olmamalidir").isNotBlank();
    }

    @Test(groups = {"fr016", "regression"},
            description = "UI-FR016-05 | Siparisin toplam tutari Total Amount alaninda gosterilir")
    @Story("ACC-005 — Toplam tutar")
    @TmsLink("FR-016-ACC-005")
    @Severity(SeverityLevel.CRITICAL)
    public void totalAmountIsShown() {
        ReviewStepPage review = openReview();

        assertThat(review.hasPriceCard())
                .as("ACC-005 — fiyat ozeti karti bulunur").isTrue();
        assertThat(review.totalAmount())
                .as("ACC-005 — toplam tutar sifirdan buyuk").isPositive();
    }

    /**
     * Sepete urun ekler, servis adresi secer ve Review adimina gecer.
     *
     * <p>On kosul ASSERT edilir, atlanmaz: saglanamamasi gercek bir sorundur.
     */
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

        // On kosul ASSERT edilir, atlanmaz: burada pasif kalan bir Next gercek bir sorundur
        // (ya konfigurasyon eksik kalmistir ya da uygulamada bir hata vardir) ve gorunmesi
        // gerekir. Onceden burada SkipException vardi ve FR-016/FR-017'nin TAMAMI sessizce
        // atlanip hicbir dogrulama yapmiyordu.
        assertThat(wizard.isNextEnabled())
                .as("on kosul: konfigurasyon tamamlandiginda Next aktiflesmelidir").isTrue();
        wizard.clickNext();
        wizard.waitForActiveStep(ExpectedMessages.get("newSale.stepReview"));

        ReviewStepPage review = new ReviewStepPage(driver());
        review.waitUntilLoaded();
        return review;
    }
}
