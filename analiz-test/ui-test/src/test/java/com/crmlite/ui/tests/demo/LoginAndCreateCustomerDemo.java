package com.crmlite.ui.tests.demo;

import com.crmlite.ui.data.builder.CustomerBuilder;
import com.crmlite.ui.data.model.CustomerData;
import com.crmlite.ui.pages.auth.LoginPage;
import com.crmlite.ui.pages.customer.CustomerDetailPage;
import com.crmlite.ui.pages.customer.SearchCustomerPage;
import com.crmlite.ui.pages.customer.create.CreateCustomerPage;
import com.crmlite.ui.tests.BaseTest;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SUNUM DEMOSU — gercek giris + yeni musteri olusturma.
 *
 * <p><b>Bu bir SUNUM ARACIDIR, test paketinin parcasi DEGILDIR.</b>
 * {@code demo} grubundadir; {@code regression.xml} ve {@code smoke} icinde YER ALMAZ,
 * bu yuzden Allure raporundaki test sayisini degistirmez.
 *
 * <p>Kapsadigi davranislarin tamami zaten mevcut testlerde dogrulanmaktadir
 * (FR-001 giris, FR-003 musteri olusturma). Buradaki tek amac, sunumda kisa surede
 * gorsel olarak anlamli bir akis gostermektir.
 *
 * <p><b>Neden gercek login.</b> {@code BaseTest}'ten turer, {@code AuthenticatedTest}'ten
 * degil: oturum token'i enjekte EDILMEZ, kullanici adi ve parola ekranda YAZILIR. Token
 * enjekte edilseydi login sayfasi bir sure bos gorunur ve izleyiciye "bekliyor" izlenimi
 * verirdi.
 *
 * <p>Kosum: {@code ./demo.sh}  (testng/single.xml bu sinifi gosterir)
 *
 * <p><b>NOT: bu demo GERCEK MUSTERI olusturur.</b>
 */
@Epic("Sunum Demosu")
@Feature("Giris → Musteri Olusturma")
public class LoginAndCreateCustomerDemo extends BaseTest {

    @Test(groups = {"demo"},
            description = "DEMO | Giris yapilir ve yeni musteri olusturulur")
    @Story("Sunum akisi — gercek giris, ardindan musteri olusturma sihirbazi")
    @Severity(SeverityLevel.NORMAL)
    @Description("Sunumda gosterilmek uzere hazirlanmis kisa akis: login ekranindan gercek "
            + "giris yapilir, ardindan musteri olusturma sihirbazi (demografik → adres → "
            + "iletisim) doldurulur ve musteri olusturulur. Olusan musterinin kimlik "
            + "numarasi ekranda dogrulanir.")
    public void loginAndCreateCustomer() {
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

        Allure.step("3. Musterinin olustugu dogrulanir");
        String custId = detail.customerId();
        Allure.parameter("Customer ID", custId);

        assertThat(custId)
                .as("musteri olusturuldu ve detay ekranina gecildi").isNotBlank();
        assertThat(detail.infoValue(CustomerDetailPage.InfoField.NATIONAL_ID))
                .as("olusturulan musteri girilen kimlik numarasini tasir")
                .isEqualTo(data.individual().nationalId());
    }
}
