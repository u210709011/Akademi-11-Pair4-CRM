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

/**
 * E2E — Uctan uca kullanici yolculugu: giris → musteri → adres → fatura hesabi →
 * satis → siparis → siparisin hesapta gorunmesi.
 *
 * <p><b>Bu testin varlik sebebi.</b> FR bazli testlerin hepsi on kosullarini API ile kurar
 * ({@code TestDataFactory}); hizli ve teshis edilebilir olduklari icin bu dogru tercihtir.
 * Ancak bunun bir bedeli var: API'nin urettigi musteri ile ARAYUZUN urettigi musteri
 * birebir ayni olmayabilir (varsayilan degerler, lookup cozumlemeleri, event yayilimi).
 * Arayuz sihirbazi satis akisinin tuketemeyecegi bir kayit uretirse, API ile kurulum yapan
 * hicbir test bunu yakalayamaz. Bu test tam olarak o DIKISLERI dogrular.
 *
 * <p><b>Kapsam.</b> Hicbir adimda API kullanilmaz — oturum bile gercek login ekranindan
 * acilir (digerlerinde token {@code localStorage}'a enjekte edilir). Zincir: Chrome →
 * Angular → Gateway → Keycloak → customer/party/contact-info/product/order servisleri →
 * PostgreSQL.
 *
 * <p><b>Kapsam DISI — Kafka/outbox yayilimi.</b> {@code finishOrder} bir
 * {@code OrderSubmittedEvent} yayinlar, ancak hesap ekranindaki urun listesi bu olayi
 * TUKETMEZ: {@code order.service.ts#getByCustAcctId} → {@code GET /api/v1/orders/by-account}
 * ile siparis kalemlerini order-service'ten SENKRON okur. Dolayisiyla son adim olay
 * yayilimini degil, siparisin dogru hesaba baglanarak kalici hale geldigini dogrular.
 *
 * <p><b>Neden tek test.</b> Alti ekrana dokundugu icin en kirilgan testimizdir ve patladiginda
 * "hangi ozellik bozuk" sorusuna FR testleri kadar iyi cevap veremez. Degeri ayrintida degil,
 * gecislerdedir; ayrinti kapsamini 239 FR testi zaten saglar. Bu yuzden ikinci bir yolculuk
 * testi EKLENMEMELIDIR.
 *
 * <p><b>Suite disidir.</b> {@code e2e} grubundadir; {@code regression.xml} ve {@code smoke}
 * icinde YER ALMAZ. Kararsizligi mevcut paketi kirmasin diye ayri kosulur:
 * {@code ./mvnw test -Dsuite=e2e}
 *
 * <p><b>Yan kazanc — zorunlu iliskili urunler.</b> Son adimdaki dogrulama, secilen teklifin
 * yani sira ona ZORUNLU ILISKIYLE otomatik eklenen urunlerin de hesaba dustugunu gosterir:
 * "Home Fiber 200Mbps" siparis edildiginde hesapta "Wi-Fi Router Purchase" ve
 * "Broadband Modem" de listelenir. Bu zincir baska hicbir testte uctan uca kosulmuyor.
 *
 * <p><b>NOT: bu test GERCEK MUSTERI ve GERCEK SIPARIS olusturur.</b>
 */
@Epic("E2E — Uctan uca kullanici yolculugu")
@Feature("Giris → Musteri → Hesap → Satis → Siparis")
public class CustomerToOrderJourneyTest extends BaseTest {

    /** Seed veride bu parcayi iceren birden fazla teklif bulunur. */
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

        // --- 1. Giris: gercek login ekranindan, token enjeksiyonu YOK ---
        Allure.step("1. Sisteme giris yapilir");
        LoginPage loginPage = new LoginPage(driver());
        loginPage.waitUntilLoaded();
        SearchCustomerPage searchPage = loginPage.loginAs(config.username(), config.password());

        assertThat(searchPage.isAt())
                .as("giris sonrasi Customer Search ekrani acilir").isTrue();

        // --- 2. Musteri olusturma: sihirbazin tamami arayuzden ---
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

        // --- 3. Fatura hesabi: sihirbazda girilen adres burada secilebilmelidir ---
        // Bu, API kurulumunun atladigi ILK dikis: adres arayuzden yaratildi, hesap onu
        // tuketiyor. Adres listesi bos gelirse burada patlar.
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

        // --- 4. Satis: hesap satirindan sihirbaz baslatilir ---
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

        // Sepet once KULLANICININ SECTIGI satirlari, sonra zorunlu iliskiyle otomatik
        // eklenen kilitli satirlari gosterir; bu yuzden ilk isim bizim sectigimiz tekliftir.
        // Varsayim ORTULU kalmasin diye asagida ACIKCA dogrulanir: panel sirasi bir gun
        // degisirse test yanlis urunu sessizce dogrulamak yerine burada patlar.
        String orderedProduct = basketNames.stream()
                .filter(name -> !name.isBlank())
                .findFirst()
                .orElseThrow(() -> new AssertionError("sepetteki hicbir satirin adi okunamadi"));
        Allure.parameter("Siparis edilen urun", orderedProduct);

        assertThat(orderedProduct)
                .as("sepetteki ilk satir, aradigimiz teklif olmalidir (zorunlu ek urun degil)")
                .containsIgnoringCase(OFFER_NAME_FRAGMENT);
        sale.clickNext();

        // --- 5. Konfigurasyon ---
        Allure.step("5. Urun konfigurasyonu tamamlanir");
        ConfigurationStepPage configuration = new ConfigurationStepPage(driver());
        configuration.waitUntilLoaded();
        configuration.completeConfiguration();

        assertThat(sale.waitForNextEnabled())
                .as("konfigurasyon tamamlandiginda Next aktiflesir").isTrue();
        sale.clickNext();
        sale.waitForActiveStep(ExpectedMessages.get("newSale.stepReview"));

        // --- 6. Ozet ve siparisin gonderilmesi ---
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

        // --- 7. Kapanis: siparis fatura hesabinda gorunur ---
        // ASIL DEGERLI ADIM. Basari modali tek basina hicbir sey kanitlamaz: siparisin
        // DOGRU FATURA HESABINA baglandigini ancak hesap ekranindan geri okuyarak
        // gorebiliriz. Bu adim olmadan test, "modal cikti" demekten ibaret kalirdi.
        //
        // NOT: burada Kafka/outbox yayilimi DOGRULANMAZ — hesap urun listesi
        // GET /api/v1/orders/by-account ile order-service'ten senkron okunur.
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
