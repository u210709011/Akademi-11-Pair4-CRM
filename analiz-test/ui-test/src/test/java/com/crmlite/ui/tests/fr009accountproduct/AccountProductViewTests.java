package com.crmlite.ui.tests.fr009accountproduct;

import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.pages.components.ProductDetailModalComponent;
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
 * FR-009 — Fatura Hesabini ve Bagli Urun Detaylarini Goruntuleme (UC-EACRML-009).
 *
 * <p>Urun gerektiren senaryolar {@code TestDataFactory.customerWithAccountProduct()} kullanir;
 * bu on kosul dort adimli siparis akisini isletir ve digerlerinden belirgin sekilde yavastir.
 * Urun gerektirmeyen senaryolar daha hizli olan {@code customerWithBillingAccount()} ile kurulur.
 */
@Epic("FR-009 Fatura Hesabi ve Bagli Urun Goruntuleme")
@Feature("UC-EACRML-009")
public class AccountProductViewTests extends AuthenticatedTest {

    @Test(groups = {"smoke", "fr009"},
            description = "UI-FR009-01 | Customer Account tabinda fatura hesaplari listelenir")
    @Story("ACC-001 — Hesaplar tablo halinde listelenir")
    @TmsLink("FR-009-ACC-001")
    @Severity(SeverityLevel.BLOCKER)
    public void accountsAreListedInTable() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        assertThat(detail.accountCount())
                .as("ACC-001 — musterinin fatura hesabi tabloda listelenir").isEqualTo(1);
    }

    @Test(groups = {"fr009", "regression"},
            description = "UI-FR009-02 | Hesap tablosunda dokumandaki bes kolon bulunur")
    @Story("ACC-002 — Tablo kolonlari")
    @TmsLink("FR-009-ACC-002")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Dokuman Account Status, Account Number, Account Name, Account Type ve Action "
            + "kolonlarini sart kosar. Basliklar CSS ile buyuk harfe cevrildigi icin "
            + "karsilastirma buyuk/kucuk harf duyarsiz yapilir.")
    public void accountTableHasDocumentedColumns() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        assertThat(detail.accountColumnHeaders())
                .extracting(String::toUpperCase)
                .as("ACC-002 — dokumandaki kolonlar")
                .contains(ExpectedMessages.get("detail.accountStatus").toUpperCase(),
                        ExpectedMessages.get("detail.accountNumber").toUpperCase(),
                        ExpectedMessages.get("detail.accountName").toUpperCase(),
                        ExpectedMessages.get("detail.accountType").toUpperCase(),
                        ExpectedMessages.get("detail.action").toUpperCase());
    }

    @Test(groups = {"fr009", "regression"},
            description = "UI-FR009-03 | Her hesap satirinin solunda genislet/daralt oku bulunur")
    @Story("ACC-003 — Genislet/daralt oku")
    @TmsLink("FR-009-ACC-003")
    @Severity(SeverityLevel.NORMAL)
    public void eachAccountRowHasExpandToggle() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        assertThat(detail.hasExpandToggle(0))
                .as("ACC-003 — satirin solunda ok bulunur").isTrue();
        assertThat(detail.isAccountRowExpanded(0))
                .as("ACC-003 — satir baslangicta kapalidir").isFalse();
    }

    @Test(groups = {"smoke", "fr009"},
            description = "UI-FR009-04 | Oka tiklandiginda bagli urunler acilir, tekrar tiklandiginda gizlenir")
    @Story("ACC-004 — Urunleri goster/gizle")
    @TmsLink("FR-009-ACC-004")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Dokumanin ana senaryosu Adim 3-4: kullanici oka tiklar, sistem urunleri "
            + "tablo icinde gosterir; tekrar tiklandiginda gizlenir.")
    public void expandTogglesProductTable() {
        TestDataFactory.CustomerWithProduct data = TestDataFactory.customerWithAccountProduct();
        CustomerDetailPage detail = openCustomerDetail(data.customer().custId()).openAccountsTab();

        detail.toggleAccountRow(0);

        assertThat(detail.isAccountRowExpanded(0))
                .as("ACC-004 — satir genisledi").isTrue();
        assertThat(detail.isProductTableDisplayed())
                .as("ACC-004 — bagli urunler tablo icinde gosterilir").isTrue();

        detail.toggleAccountRow(0);
        detail.waitUntilProductTableHidden();

        assertThat(detail.isAccountRowExpanded(0))
                .as("ACC-004 — tekrar tiklaninca satir daraldi").isFalse();
    }

    @Test(groups = {"fr009", "regression"},
            description = "UI-FR009-05 | Urun tablosunda dokumandaki bes kolon bulunur")
    @Story("ACC-005 — Urun tablosu kolonlari")
    @TmsLink("FR-009-ACC-005")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Dokuman Product ID, Product Name, Campaign Name, Campaign ID ve Action "
            + "kolonlarini sart kosar.")
    public void productTableHasDocumentedColumns() {
        TestDataFactory.CustomerWithProduct data = TestDataFactory.customerWithAccountProduct();
        CustomerDetailPage detail = openCustomerDetail(data.customer().custId()).openAccountsTab();

        detail.toggleAccountRow(0);

        assertThat(detail.productColumnHeaders())
                .extracting(String::toUpperCase)
                .as("ACC-005 — urun tablosu kolonlari")
                .contains(ExpectedMessages.get("detail.productId").toUpperCase(),
                        ExpectedMessages.get("detail.productName").toUpperCase(),
                        ExpectedMessages.get("detail.campaignName").toUpperCase(),
                        ExpectedMessages.get("detail.campaignId").toUpperCase(),
                        ExpectedMessages.get("detail.action").toUpperCase());
    }

    @Test(groups = {"fr009", "regression"},
            description = "UI-FR009-06 | Hesaba bagli urun, urun tablosunda adiyla listelenir")
    @Story("ACC-004 — Bagli urunler gosterilir")
    @TmsLink("FR-009-ACC-004")
    @Severity(SeverityLevel.CRITICAL)
    public void linkedProductIsListedByName() {
        TestDataFactory.CustomerWithProduct data = TestDataFactory.customerWithAccountProduct();
        CustomerDetailPage detail = openCustomerDetail(data.customer().custId()).openAccountsTab();

        detail.toggleAccountRow(0);

        assertThat(detail.productNames())
                .as("ACC-004 — siparisle olusan urun tabloda gorunur")
                .contains(data.productName());
    }

    @Test(groups = {"fr009", "regression"},
            description = "UI-FR009-07 | Urunu olmayan hesapta bilgilendirme mesaji gosterilir")
    @Story("ACC-004 — Urun bulunmamasi")
    @TmsLink("FR-009-ACC-004")
    @Severity(SeverityLevel.NORMAL)
    @Description("Dokuman bu durumu acikca tanimlamaz; uygulama tablo yerine "
            + "'No products on this account yet.' gosterir. Davranis kayit altina alinir.")
    public void accountWithoutProductsShowsMessage() {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        detail.toggleAccountRow(0);

        assertThat(detail.hasNoProductsMessage())
                .as("urunu olmayan hesapta bilgilendirme mesaji gosterilir").isTrue();
        assertThat(detail.noProductsMessage())
                .isEqualTo(ExpectedMessages.get("detail.noProducts"));
    }

    @Test(groups = {"smoke", "fr009"},
            description = "UI-FR009-08 | Goz ikonu urun detay modalini acar")
    @Story("ACC-006 — Urun detay modali")
    @TmsLink("FR-009-ACC-006")
    @Severity(SeverityLevel.BLOCKER)
    public void eyeIconOpensProductDetailModal() {
        TestDataFactory.CustomerWithProduct data = TestDataFactory.customerWithAccountProduct();
        CustomerDetailPage detail = openCustomerDetail(data.customer().custId()).openAccountsTab();

        detail.toggleAccountRow(0);
        ProductDetailModalComponent modal = detail.openProductDetail(0);

        assertThat(modal.isOpen())
                .as("ACC-006 — goz ikonu modali acar").isTrue();
        assertThat(modal.title())
                .as("ACC-006 — modal basligi")
                .isEqualTo(ExpectedMessages.get("detail.productOfferDetailsTitle"));
    }

    @Test(groups = {"fr009", "regression"},
            description = "UI-FR009-09 | Modalda dokumandaki alti alan bulunur")
    @Story("ACC-007 — Modal alanlari")
    @TmsLink("FR-009-ACC-007")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Dokuman Product Offer Name, Product Offer ID, Product Spec ID, "
            + "Service Start Date, Prod Chars ve Service Address alanlarini sart kosar. "
            + "DEGERLER dogrulanmaz: modal su an mock veriyle calisiyor "
            + "(PRODUCT_DETAIL_MOCK_MODE=true), mock degere assertion yazmak anlamsiz olur.")
    public void modalShowsDocumentedFields() {
        TestDataFactory.CustomerWithProduct data = TestDataFactory.customerWithAccountProduct();
        CustomerDetailPage detail = openCustomerDetail(data.customer().custId()).openAccountsTab();

        detail.toggleAccountRow(0);
        ProductDetailModalComponent modal = detail.openProductDetail(0);

        assertThat(modal.fieldLabels())
                .as("ACC-007 — dokumandaki alanlar")
                .contains(ExpectedMessages.get("detail.productOfferNameLabel"),
                        ExpectedMessages.get("detail.productOfferIdLabel"),
                        ExpectedMessages.get("detail.productSpecIdLabel"),
                        ExpectedMessages.get("detail.serviceStartDateLabel"),
                        ExpectedMessages.get("detail.productCharacteristicsTitle"));
        assertThat(modal.hasServiceAddressSection())
                .as("ACC-007 — Service Address bolumu bulunur").isTrue();
    }

    @Test(groups = {"fr009", "regression"},
            description = "UI-FR009-10 | Carpi ikonu modali kapatir ve hesap ekranina donulur")
    @Story("ACC-008 — Modali kapatma")
    @TmsLink("FR-009-ACC-008")
    @Severity(SeverityLevel.NORMAL)
    public void closeIconReturnsToAccountsScreen() {
        TestDataFactory.CustomerWithProduct data = TestDataFactory.customerWithAccountProduct();
        CustomerDetailPage detail = openCustomerDetail(data.customer().custId()).openAccountsTab();

        detail.toggleAccountRow(0);
        ProductDetailModalComponent modal = detail.openProductDetail(0);
        modal.close();
        modal.waitUntilClosed();

        assertThat(detail.accountCount())
                .as("ACC-008 — modal kapandi, fatura hesaplari ekranina donuldu").isEqualTo(1);
    }
}
