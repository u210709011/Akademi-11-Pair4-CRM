package com.crmlite.ui.tests.fr012newsale;

import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.pages.customer.CustomerDetailPage;
import com.crmlite.ui.pages.newsale.OfferSelectionPage;
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
 * FR-012 — Yeni Satis Surecini Baslatma (UC-EACRML-012).
 *
 * <p>Giris noktasi fatura hesabi satirinin icindedir: hesap satiri genisletilmeden
 * "Start New Sale" butonu ekranda bulunmaz. On kosul bu yuzden fatura hesabi olan
 * bir musteridir.
 *
 * <p>Sihirbaz tek kabuk bilesende yasar; adim degistiginde rota degismez. "Hangi
 * adimdayiz" sorusu bu yuzden URL'den degil stepper'daki aktif isaretten okunur
 * (bkz. {@link OfferSelectionPage}).
 */
@Epic("FR-012 Yeni Satis Surecini Baslatma")
@Feature("UC-EACRML-012")
public class StartNewSaleTests extends AuthenticatedTest {

    @Test(groups = {"smoke", "fr012"},
            description = "UI-FR012-01 | New Sale butonu Offer Selection ekranini acar")
    @Story("ACC-001 — Offer Selection acilir")
    @TmsLink("FR-012-ACC-001")
    @Severity(SeverityLevel.BLOCKER)
    public void startNewSaleOpensOfferSelection() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();
        detail.toggleAccountRow(0);

        assertThat(detail.hasStartNewSaleButton())
                .as("ACC-001 — genisletilmis hesap satirinda New Sale butonu bulunur").isTrue();

        OfferSelectionPage offers = detail.startNewSale();

        assertThat(offers.isDisplayed())
                .as("ACC-001 — Offer Selection ekrani acilir").isTrue();
        assertThat(offers.currentUrl())
                .as("ACC-001 — yeni satis rotasina gidilir").contains("/new-sale/");
    }

    @Test(groups = {"fr012", "regression"},
            description = "UI-FR012-02 | Sol tarafta Catalog ve Campaigns sekmeleri bulunur")
    @Story("ACC-002 — Catalog / Campaign sekmeleri")
    @TmsLink("FR-012-ACC-002")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Dokuman iki ayri sekme sart kosar: Catalog ve Campaign bilgileri.")
    public void catalogAndCampaignTabsArePresent() {
        OfferSelectionPage offers = openOfferSelection();

        assertThat(offers.tabCount())
                .as("ACC-002 — iki sekme bulunur").isEqualTo(2);
        assertThat(offers.tabLabels())
                .as("ACC-002 — sekme etiketleri")
                .containsExactly(ExpectedMessages.get("newSale.catalogTab"),
                        ExpectedMessages.get("newSale.campaignsTab"));
    }

    @Test(groups = {"fr012", "regression"},
            description = "UI-FR012-03 | Sekmeler arasinda gecis yapilabilir")
    @Story("ACC-002 — Sekme gecisi")
    @TmsLink("FR-012-ACC-002")
    @Severity(SeverityLevel.NORMAL)
    public void tabsCanBeSwitched() {
        OfferSelectionPage offers = openOfferSelection();

        assertThat(offers.activeTabLabel())
                .as("ACC-002 — ekran Catalog sekmesiyle acilir")
                .isEqualTo(ExpectedMessages.get("newSale.catalogTab"));

        offers.selectTab(OfferSelectionPage.Tab.CAMPAIGNS);

        assertThat(offers.activeTabLabel())
                .as("ACC-002 — Campaigns sekmesine gecilir")
                .isEqualTo(ExpectedMessages.get("newSale.campaignsTab"));
    }

    @Test(groups = {"smoke", "fr012"},
            description = "UI-FR012-04 | Sag tarafta sepet alani bulunur ve baslangicta bostur")
    @Story("ACC-003, ACC-004 — Sepet alani")
    @TmsLink("FR-012-ACC-004")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Dokuman ekran ilk acildiginda sepetin bos olmasini ve bilgilendirme metni "
            + "gosterilmesini sart kosar. Metin dokuman ve uygulamada birebir aynidir.")
    public void basketIsPresentAndEmptyOnOpen() {
        OfferSelectionPage offers = openOfferSelection();

        assertThat(offers.isBasketDisplayed())
                .as("ACC-003 — sag tarafta sepet alani bulunur").isTrue();
        assertThat(offers.isBasketEmpty())
                .as("ACC-004 — sepet baslangicta bostur").isTrue();
        assertThat(offers.basketEmptyText())
                .as("ACC-004 — bos sepet mesaji")
                .isEqualTo(ExpectedMessages.get("newSale.basketEmpty"));
    }

    @Test(groups = {"fr012", "regression"},
            description = "UI-FR012-05 | Sag alt kosede Next butonu bulunur")
    @Story("ACC-005 — Next butonu")
    @TmsLink("FR-012-ACC-005")
    @Severity(SeverityLevel.CRITICAL)
    public void nextButtonIsPresent() {
        OfferSelectionPage offers = openOfferSelection();

        assertThat(offers.hasNextButton())
                .as("ACC-005 — Next butonu bulunur").isTrue();
        assertThat(offers.nextButtonLabel())
                .as("ACC-005 — buton etiketi")
                .isEqualTo(ExpectedMessages.get("newSale.nextBtn"));
    }

    @Test(groups = {"fr012", "regression"},
            description = "UI-FR012-06 | Sepet bosken Next ile ilerlenemez")
    @Story("ACC-004, ACC-005 — Bos sepetle ilerleme")
    @TmsLink("FR-012-ACC-005")
    @Severity(SeverityLevel.NORMAL)
    @Description("Dokuman bu kurali acikca yazmaz; sepet bosken bir sonraki adima gecmenin "
            + "anlami olmadigi icin uygulamanin davranisi kayit altina alinir.")
    public void nextIsDisabledWhileBasketIsEmpty() {
        OfferSelectionPage offers = openOfferSelection();

        assertThat(offers.isBasketEmpty())
                .as("on kosul: sepet bos").isTrue();
        assertThat(offers.isNextDisabled())
                .as("sepet bosken Next pasif kalir").isTrue();
    }

    /** Fatura hesabi olan bir musteri kurup Offer Selection ekranini acar. */
    private OfferSelectionPage openOfferSelection() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();
        detail.toggleAccountRow(0);
        return detail.startNewSale();
    }
}
