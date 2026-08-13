package com.crmlite.ui.tests;

import com.crmlite.ui.data.api.OrderApi;
import com.crmlite.ui.data.api.TestDataFactory;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FR-009 on kosulunun kurulabildigini dogrular — testler yazilmadan once calistirilir.
 *
 * <p>Urun olusturma dort adimli bir siparis akisina bagli ve bu akis 07.08.2026'ya kadar
 * tamamen kirikti (order-service'in Feign yolunda yazim hatasi). On kosul kurulamazsa
 * FR-009'un urun tablosu ve urun detay senaryolari hicbir sey dogrulayamaz; bu self-check
 * sorunu <b>test hatasi olarak degil, kurulum hatasi olarak</b> ve erken gorunur kilar.
 */
@Epic("Cerceve Self-Check")
public class OrderApiSelfCheckTest extends AuthenticatedTest {

    @Test(groups = {"selfcheck", "fr009"},
            description = "Fatura hesabina bagli urun on kosulu API ile kurulabiliyor")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Musteri -> fatura hesabi -> siparis -> yapilandirma -> tamamlama zinciri "
            + "islenir ve urunun arayuzun kullandigi uctan geri okunabildigi dogrulanir.")
    public void accountProductPreconditionCanBeBuilt() {
        TestDataFactory.CustomerWithProduct data = TestDataFactory.customerWithAccountProduct();

        assertThat(data.productName())
                .as("siparis tamamlaninca urun adi olusmalidir").isNotBlank();

        List<Map<String, Object>> products = OrderApi.productsOnAccount(data.account().custAcctId());

        assertThat(products)
                .as("urun, arayuzun urun tablosunu doldurdugu uctan donmelidir").hasSize(1);
        assertThat(products.get(0).get("prodName"))
                .as("urun adi dolu gelmelidir (siparis tamamlanmadan null kalir)")
                .isEqualTo(data.productName());
        assertThat(products.get(0).get("prodId"))
                .as("prodId ancak siparis tamamlaninca olusur").isNotNull();
    }
}
