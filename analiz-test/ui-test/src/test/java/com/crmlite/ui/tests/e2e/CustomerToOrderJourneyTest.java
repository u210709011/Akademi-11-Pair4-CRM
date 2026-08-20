package com.crmlite.ui.tests.e2e;

import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.builder.CustomerBuilder;
import com.crmlite.ui.data.model.CustomerData;
import com.crmlite.ui.pages.auth.LoginPage;
import com.crmlite.ui.pages.components.BillingAccountModalComponent;
import com.crmlite.ui.pages.customer.CustomerDetailPage;
import com.crmlite.ui.pages.customer.SearchCustomerPage;
import com.crmlite.ui.pages.customer.create.CreateCustomerPage;
import com.crmlite.ui.pages.newsale.ConfigurationStepPage;
import com.crmlite.ui.pages.newsale.OfferSelectionPage;
import com.crmlite.ui.pages.newsale.ReviewStepPage;
import com.crmlite.ui.tests.BaseTest;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("E2E — Uctan uca kullanici yolculugu")
@Feature("Giris → Musteri → Hesap → Satis → Siparis")
public class CustomerToOrderJourneyTest extends BaseTest {

    private static final String OFFER_NAME_FRAGMENT = "Home";

    @Test(groups = {"e2e"},
            description = "UI-E2E-01 | Giristen siparise kadar tum yolculuk arayuzden yurutulur")
    @Story("Kritik yol — bir satis temsilcisinin bastan sona yaptigi is")
    @TmsLink("UI-E2E-01")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Bir satis temsilcisinin gunluk isinin tamami tek akista yurutulur: sisteme "
            + "girer, yeni musteri olusturur (adres ve iletisim bilgisiyle), musteriye fatura "
            + "hesabi acar, o hesap uzerinden satis baslatir, katalogdan urun secip konfigure "
            + "eder ve siparisi onaylar. Son adimda siparisin fatura hesabinda GORUNDUGU "
            + "dogrulanir: basari modali bir sey KANITLAMAZ, siparisin dogru hesaba baglanip "
            + "kullanicinin gordugu ekrandan geri okunabilmesi kanitlar.")
    public void salesRepCompletesFullJourneyFromLoginToOrder() {
        CustomerData data = CustomerBuilder.aValidCustomer().build();
        Allure.parameter("Nationality ID", data.individual().nationalId());

        
        Allure.step("1. Sisteme giris yapilir");
        LoginPage loginPage = new LoginPage(driver());
        loginPage.waitUntilLoaded();
        SearchCustomerPage searchPage = loginPage.loginAs(config.username(), config.password());

        assertThat(searchPage.isAt())
                .as("giris sonrasi Customer Search ekrani acilir").isTrue();

       
        Allure.step("2. Yeni musteri olusturulur (demografik → adres → iletisim)");
        CreateCustomerPage wizard = searchPage.goToCreateCustomer();

        wizard.demographicStep().fillRequired(
                data.individual().firstName(),
                data.individual().lastName(),
                data.individual().birthDate(),
                String.valueOf(data.individual().genderId()),
                data.individual().nationalId());
        assertThat(wizard.isNextEnabled())
                .as("zorunlu demografik alanlar dolunca Next aktiflesir").isTrue();
        wizard.clickNext();

        wizard.addressStep().addAddress(
                data.addresses().get(0).streetName(),
                data.addresses().get(0).buildingName(),
                data.addresses().get(0).addressDesc());
        wizard.clickNext();

        wizard.contactStep().fillRequired(data.contact().email(), data.contact().mobilePhone());
        CustomerDetailPage detail = wizard.clickCreate();

        String custId = detail.customerId();
        Allure.parameter("Customer ID", custId);
        assertThat(custId).as("musteri olusturuldu ve detay ekranina gecildi").isNotBlank();
        assertThat(detail.infoValue(CustomerDetailPage.InfoField.NATIONAL_ID))
                .as("olusturulan musteri girilen kimlik numarasini tasir")
                .isEqualTo(data.individual().nationalId());

        Allure.step("3. Musteriye fatura hesabi acilir (sihirbazdaki adres secilerek)");
        detail.openAccountsTab();
        assertThat(detail.accountCount())
                .as("yeni musteride henuz fatura hesabi yoktur").isZero();

        String accountName = "E2E Hesabi " + custId;
        BillingAccountModalComponent accountModal = detail.openCreateAccountModal();
        accountModal.enterAccountName(accountName)
                .enterAccountDescription("Uctan uca yolculuk testi")
                .selectFirstExistingAddress();
        assertThat(accountModal.selectedAddressText())
                .as("sihirbazda girilen adres hesap ekraninda secilebilir olmalidir")
                .isNotBlank();
        accountModal.create();
        accountModal.waitUntilClosed();

        assertThat(detail.waitForAccountNamed(accountName))
                .as("fatura hesabi olusturuldu ve tabloda listelenir").contains(accountName);

        Allure.step("4. Fatura hesabi uzerinden yeni satis baslatilir");
        detail.toggleAccountRow(0);
        assertThat(detail.hasStartNewSaleButton())
                .as("genisletilen hesap satirinda Start New Sale butonu bulunur").isTrue();

        OfferSelectionPage sale = detail.startNewSale();
        sale.enterOfferName(OFFER_NAME_FRAGMENT);
        sale.searchUntilResults();
        assertThat(sale.resultRowCount())
                .as("katalog aramasi sonuc dondurur").isPositive();

        sale.addToBasket(0);
        List<String> basketNames = sale.basketItemNames();
        assertThat(basketNames).as("secilen teklif sepete eklenir").isNotEmpty();

        String orderedProduct = basketNames.stream()
                .filter(name -> !name.isBlank())
                .findFirst()
                .orElseThrow(() -> new AssertionError("sepetteki hicbir satirin adi okunamadi"));
        Allure.parameter("Siparis edilen urun", orderedProduct);

        assertThat(orderedProduct)
                .as("sepetteki ilk satir, aradigimiz teklif olmalidir (zorunlu ek urun degil)")
                .containsIgnoringCase(OFFER_NAME_FRAGMENT);
        sale.clickNext();

        Allure.step("5. Urun konfigurasyonu tamamlanir");
        ConfigurationStepPage configuration = new ConfigurationStepPage(driver());
        configuration.waitUntilLoaded();
        configuration.completeConfiguration();

        assertThat(sale.waitForNextEnabled())
                .as("konfigurasyon tamamlandiginda Next aktiflesir").isTrue();
        sale.clickNext();
        sale.waitForActiveStep(ExpectedMessages.get("newSale.stepReview"));

        Allure.step("6. Siparis ozeti onaylanir ve siparis gonderilir");
        ReviewStepPage review = new ReviewStepPage(driver());
        review.waitUntilLoaded();
        assertThat(review.productRowCount())
                .as("ozet ekraninda en az bir siparis kalemi listelenir").isPositive();
        assertThat(review.hasServiceAddress())
                .as("siparisin servis adresi ozette gosterilir").isTrue();

        sale.clickNext();
        review.confirmSubmit();

        assertThat(review.hasSuccessModal())
                .as("siparis iletilir ve basari modali gosterilir").isTrue();
        assertThat(review.successTitle())
                .as("basari basligi")
                .isEqualTo(ExpectedMessages.get("newSale.orderSuccessTitle"));

        Allure.step("7. Siparis edilen urun fatura hesabinda gorunur");
        review.goToBillingAccount();

        CustomerDetailPage afterOrder = new CustomerDetailPage(driver());
        afterOrder.waitUntilLoaded();
        assertThat(afterOrder.currentUrl())
                .as("basari modalindan musteri ekranina donulur").contains("/detail-customer/");

        afterOrder.openAccountsTab();
        afterOrder.toggleAccountRow(0);

        assertThat(afterOrder.waitForProductNamed(orderedProduct))
                .as("siparis edilen urun fatura hesabinin urun listesinde gorunur")
                .contains(orderedProduct);
    }
}
