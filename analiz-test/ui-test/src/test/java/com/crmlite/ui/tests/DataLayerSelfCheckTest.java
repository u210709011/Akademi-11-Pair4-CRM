package com.crmlite.ui.tests;

import com.crmlite.ui.core.utils.DateUtil;
import com.crmlite.ui.core.utils.JsonReader;
import com.crmlite.ui.core.utils.NationalIdGenerator;
import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.api.ApiClient;
import com.crmlite.ui.data.builder.AddressBuilder;
import com.crmlite.ui.data.builder.CustomerBuilder;
import com.crmlite.ui.data.builder.TestRunId;
import com.crmlite.ui.data.model.CustomerData;
import com.crmlite.ui.data.model.Gender;
import com.crmlite.ui.data.model.SearchCriteria;
import com.crmlite.ui.data.model.ValidationCase;
import com.crmlite.ui.data.provider.ValidationDataProvider;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Faz 3 kabul testi — <b>uygulama ve API gerektirmez.</b>
 *
 * <p>Veri katmanini cevrimdisi dogrular: model serilestirme, builder varyantlari,
 * NAT ID uretimi, JSON fixture'lar, beklenen mesaj anahtarlari ve REST Assured'in
 * bu JDK uzerinde yuklenebilmesi.
 *
 * <p>Gercek API cagrilari ortam ayaga kalktiginda Faz 4'te dogrulanir.
 */
@Epic("Cerceve Self-Check")
@Feature("Faz 3 — Test verisi katmani")
public class DataLayerSelfCheckTest extends BaseTest {

    /**
     * Bu testler tamamen cevrimdisidir: ne tarayici ne de ag erisimi kullanirlar.
     * Tarayici acilmadigi icin sekiz test saniyeler yerine milisaniyeler surer.
     */
    @Override
    protected boolean requiresBrowser() {
        return false;
    }

    @Test(description = "Uretilen Nationality ID benzersiz ve algoritmik olarak gecerlidir")
    @Story("NationalIdGenerator")
    @Severity(SeverityLevel.CRITICAL)
    @Description("1000 kimlik uretilir; hepsi 11 haneli, rakamlardan olusan, benzersiz ve "
            + "TCKN checksum kurallarina uygun olmalidir.")
    public void nationalIdsAreUniqueAndValid() {
        Set<String> generated = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String id = NationalIdGenerator.next();
            assertThat(id).as("uzunluk").hasSize(11);
            assertThat(id).as("yalnizca rakam").matches("\\d{11}");
            assertThat(id.charAt(0)).as("ilk hane 0 olamaz").isNotEqualTo('0');
            assertThat(isValidChecksum(id)).as("checksum gecerli: %s", id).isTrue();
            generated.add(id);
        }
        assertThat(generated).as("benzersizlik").hasSize(1000);

        assertThat(NationalIdGenerator.tooShort()).as("negatif varyant").hasSize(10);
        assertThat(NationalIdGenerator.tooLong()).as("negatif varyant").hasSize(12);
    }

    @Test(description = "CustomerBuilder gecerli ve benzersiz musteri uretir")
    @Story("CustomerBuilder")
    @Severity(SeverityLevel.CRITICAL)
    public void builderProducesValidUniqueCustomers() {
        CustomerData first = CustomerBuilder.aValidCustomer().build();
        CustomerData second = CustomerBuilder.aValidCustomer().build();

        Allure.step("Zorunlu alanlar dolu");
        assertThat(first.individual().firstName()).isNotBlank();
        assertThat(first.individual().lastName()).isNotBlank();
        assertThat(first.individual().nationalId()).matches("\\d{11}");
        assertThat(first.individual().birthDate()).matches("\\d{2}/\\d{2}/\\d{4}");
        assertThat(first.individual().genderId()).isEqualTo(Gender.MALE.id());
        assertThat(first.contact().email()).contains("@");
        assertThat(first.contact().mobilePhone()).as("10 hane, 5 ile baslar").matches("5\\d{9}");
        assertThat(first.addresses()).hasSize(1);

        Allure.step("Ornekler birbirinden benzersiz");
        assertThat(first.individual().nationalId()).isNotEqualTo(second.individual().nationalId());
        assertThat(first.individual().firstName()).isNotEqualTo(second.individual().firstName());
        assertThat(first.contact().email()).isNotEqualTo(second.contact().email());

        Allure.step("Kosum kimligi verilere islenmis");
        assertThat(first.individual().firstName()).contains(TestRunId.value());
    }

    @Test(description = "Builder negatif ve sinir varyantlari dogru uretir")
    @Story("CustomerBuilder — negatif varyantlar")
    @Severity(SeverityLevel.NORMAL)
    public void builderProducesNegativeVariants() {
        assertThat(CustomerBuilder.aValidCustomer().withoutLastName().build()
                .individual().lastName()).isEmpty();
        assertThat(CustomerBuilder.aValidCustomer().withoutGender().build()
                .individual().genderId()).isNull();
        assertThat(CustomerBuilder.aValidCustomer().withoutAddress().build()
                .addresses()).isEmpty();
        assertThat(CustomerBuilder.aValidCustomer().withAddressCount(5).build()
                .addresses()).hasSize(5);
        assertThat(CustomerBuilder.aValidCustomer().withFirstNameOfLength(51).build()
                .individual().firstName()).hasSize(51);

        Allure.step("Dogum tarihi sinir degerleri");
        assertThat(CustomerBuilder.aValidCustomer().withEarliestValidBirthDate().build()
                .individual().birthDate()).isEqualTo("01/01/1900");
        assertThat(CustomerBuilder.aValidCustomer().withTooOldBirthDate().build()
                .individual().birthDate()).isEqualTo("31/12/1899");
        assertThat(CustomerBuilder.aValidCustomer().withFutureBirthDate().build()
                .individual().birthDate()).isEqualTo(DateUtil.tomorrow());

        Allure.step("Adres varyantlari");
        assertThat(AddressBuilder.aValidAddress().withoutStreet().build().streetName()).isEmpty();
        assertThat(AddressBuilder.aValidAddress().withoutCity().build().cityId()).isNull();
        assertThat(AddressBuilder.aValidAddress().withStreetOfLength(200).build()
                .streetName()).hasSize(200);
    }

    @Test(description = "Onboarding govdesi API sozlesmesiyle birebir eslesir")
    @Story("Model serilestirme")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Postman koleksiyonundaki onboarding govdesi ile alan adlari karsilastirilir. "
            + "Adreslerde 'primary' alani onboarding govdesinde gonderilmemelidir.")
    public void onboardingPayloadMatchesApiContract() {
        String json = JsonReader.toJson(CustomerBuilder.aValidCustomer().build().forOnboarding());
        Allure.addAttachment("Onboarding govdesi", "application/json", json, ".json");

        assertThat(json).contains("\"individual\"", "\"addresses\"", "\"contact\"");
        assertThat(json).contains("\"firstName\"", "\"middleName\"", "\"lastName\"",
                "\"birthDate\"", "\"genderId\"", "\"motherName\"", "\"fatherName\"", "\"nationalId\"");
        assertThat(json).contains("\"cityId\"", "\"streetName\"", "\"buildingName\"", "\"addressDesc\"");
        assertThat(json).contains("\"email\"", "\"mobilePhone\"", "\"homePhone\"", "\"fax\"");

        assertThat(json)
                .as("onboarding govdesinde adres 'primary' alani bulunmamalidir")
                .doesNotContain("\"primary\"");

        assertThat(json)
                .as("null alanlar gonderilmemelidir")
                .doesNotContain(":null");
    }

    @Test(description = "Arama kriterleri dogru query parametrelerine cevrilir")
    @Story("SearchCriteria")
    @Severity(SeverityLevel.NORMAL)
    @Description("UI alan adlari ile API parametre adlari farklidir (NAT ID -> tcNo, "
            + "Account Number -> acctNo, GSM -> gsm); esleme dogrulanir.")
    public void searchCriteriaMapsToApiParameters() {
        assertThat(SearchCriteria.byNationalId("12345678901").toQueryParams())
                .containsExactly(java.util.Map.entry("tcNo", "12345678901"));
        assertThat(SearchCriteria.byAccountNumber("ACC-1").toQueryParams())
                .containsExactly(java.util.Map.entry("acctNo", "ACC-1"));
        assertThat(SearchCriteria.byGsm("5321234567").toQueryParams())
                .containsExactly(java.util.Map.entry("gsm", "5321234567"));

        assertThat(SearchCriteria.byFullName("Ali", "Veli").toQueryParams())
                .containsOnlyKeys("firstName", "lastName");

        assertThat(new SearchCriteria(null, null, null, null, "", null).toQueryParams())
                .as("bos degerler parametre olarak gonderilmez")
                .isEmpty();
    }

    @Test(description = "Tum JSON fixture'lari okunabilir ve mesaj anahtarlari mevcuttur")
    @Story("Fixture ve beklenen mesajlar")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Her fixture parse edilir; icindeki her expectedMessageKey, "
            + "expected/messages_en.properties dosyasinda tanimli olmalidir. "
            + "Bir anahtarin eksik olmasi Faz 4'te calisma aninda hataya donusurdu.")
    public void fixturesParseAndMessageKeysExist() {
        int totalCases = 0;
        List<String> missingKeys = new java.util.ArrayList<>();

        for (String fixture : ValidationDataProvider.allFixtures()) {
            List<ValidationCase> cases = ValidationDataProvider.read(fixture);
            assertThat(cases).as("fixture bos olmamali: %s", fixture).isNotEmpty();
            totalCases += cases.size();

            for (ValidationCase testCase : cases) {
                assertThat(testCase.description()).as("her satirda 'case' aciklamasi olmali").isNotBlank();
                String key = testCase.expectedMessageKey();
                if (key != null && !ExpectedMessages.contains(key)) {
                    missingKeys.add(fixture + " -> " + key);
                }
            }
        }

        Allure.step("Toplam validasyon satiri: " + totalCases);
        assertThat(missingKeys).as("expected/messages_en.properties icinde eksik anahtarlar").isEmpty();
        assertThat(totalCases).as("toplam validasyon satiri").isGreaterThanOrEqualTo(25);
    }

    @Test(description = "Beklenen mesajlar dokumandaki metinlerle uyumludur")
    @Story("Fixture ve beklenen mesajlar")
    @Severity(SeverityLevel.NORMAL)
    public void expectedMessagesMatchRequirements() {
        assertThat(ExpectedMessages.get("login.wrongCredentials"))
                .isEqualTo("Wrong user name or password. Please try again.");
        assertThat(ExpectedMessages.get("login.accountLocked"))
                .isEqualTo("Your account has been locked. Please try again after 15 minutes.");
        assertThat(ExpectedMessages.get("create.fieldRequired"))
                .isEqualTo("This field is required.");
        assertThat(ExpectedMessages.get("create.addressLimitReached"))
                .isEqualTo("You can add up to 5 addresses.");
        assertThat(ExpectedMessages.get("detail.primaryAddressCannotDelete"))
                .isEqualTo("Primary address cannot be deleted.");
    }

    @Test(description = "REST Assured bu JDK uzerinde yuklenip istek yapilandirmasi olusturabiliyor")
    @Story("API istemcisi")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Ag erisimi YAPILMAZ. Yalnizca REST Assured (ve gomulu Groovy calisma zamani) "
            + "sinif yukleme ve spec olusturma asamasini gecebiliyor mu dogrulanir; "
            + "JDK uyumsuzluklari Faz 4'te degil burada ortaya cikar.")
    public void restAssuredLoadsOnThisJdk() {
        assertThat(ApiClient.selfCheck()).isEqualTo("ok");
        Allure.step("REST Assured spec olusturuldu | Java " + System.getProperty("java.version"));
    }

    /** TCKN checksum dogrulamasi (uretecin dogrulugunu bagimsiz olarak kontrol eder). */
    private static boolean isValidChecksum(String id) {
        int[] d = new int[11];
        for (int i = 0; i < 11; i++) {
            d[i] = id.charAt(i) - '0';
        }
        int oddSum = d[0] + d[2] + d[4] + d[6] + d[8];
        int evenSum = d[1] + d[3] + d[5] + d[7];
        int tenth = Math.floorMod(oddSum * 7 - evenSum, 10);

        int firstTenSum = 0;
        for (int i = 0; i < 10; i++) {
            firstTenSum += d[i];
        }
        return d[9] == tenth && d[10] == firstTenSum % 10;
    }
}
