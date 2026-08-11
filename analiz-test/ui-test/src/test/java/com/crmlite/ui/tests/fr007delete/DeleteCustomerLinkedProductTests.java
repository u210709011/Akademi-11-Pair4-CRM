package com.crmlite.ui.tests.fr007delete;

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

/**
 * FR-007 ACC-004 — pasif fatura hesabina bagli urun bulunmasi.
 *
 * <p><b>BILINEN UYUMSUZLUK — bu senaryo uygulamada hic calistirilmiyor.</b>
 *
 * <p>Dokuman (ACC-004 ve UC-EACRML-007 Alternatif Senaryo 2) sunu sart kosar:
 * <blockquote>
 * "Musterinin fatura hesabi pasif olsa dahi, bu hesaba bagli bir urun bulunmasi
 * durumunda silme islemi gerceklestirilememeli ve kullaniciya bilgilendirme
 * mesaji gosterilmelidir."
 * </blockquote>
 *
 * <p>Uygulamada bu kural musteri silme akisinda <b>hic cagrilmiyor</b>. Kaynak koddaki
 * kendi yorumu bunu acikca soyluyor ({@code BillingAccountBusinessRules}):
 * <blockquote>
 * "Urun guard'i (ACC-004, pasif hesaba bagli urun) ayri bir kural olan
 * ensureNoLinkedProducts'tadir, musteri silme akisinda cagrilmaz - bu guard sadece
 * billing account'un kendisi silinirken calisir."
 * </blockquote>
 *
 * <p>Yani pasif fatura hesabi + bagli urunu olan bir musteri, dokumana aykiri olarak
 * <b>silinebilir</b> durumdadir.
 *
 * <h2>Neden otomatize edilmedi</h2>
 * Senaryoyu kurmak icin bir fatura hesabinin <b>pasiflestirilmesi</b> ve o hesaba
 * <b>urun baglanmasi</b> gerekir. Ikisi de mevcut UI ve test veri katmaniyla
 * kurulamiyor:
 * <ul>
 *   <li>Hesap pasiflestirme UI'da yok (FR-010 Billing Account Guncelleme henuz
 *       implemente edilmemis - buton var, {@code (click)} handler'i yok).</li>
 *   <li>Urun baglama akisi ({@code product-service}) UI'da hic yer almiyor.</li>
 * </ul>
 * On kosul ancak dogrudan veritabanina yazarak kurulabilirdi; bu, testi uygulamanin
 * disina cikaracagi ve kirilgan hale getirecegi icin bilincli olarak yapilmadi.
 *
 * <p>FR-010 implemente edildiginde on kosul UI/API ile kurulabilir hale gelir ve bu
 * sinif gercek bir teste donusturulmelidir.
 */
@Epic("FR-007 Musteri Silme")
@Feature("UC-EACRML-007")
public class DeleteCustomerLinkedProductTests extends AuthenticatedTest {

    @Test(enabled = false, groups = {"fr007", "blocked", "documented-gap"},
            description = "UI-FR007-05 | Pasif hesaba bagli urun varken musteri silinemez")
    @Story("ACC-004 — Bagli urun silmeyi engeller")
    @TmsLink("FR-007-ACC-004")
    @Issue("FR-007-GAP-ACC004")
    @Severity(SeverityLevel.CRITICAL)
    @Description("ENGELLENDI: kural silme akisinda cagrilmiyor; ayrica on kosul (pasif hesap + "
            + "bagli urun) mevcut UI ile kurulamiyor. FR-010 gelince aktiflestirilmelidir.")
    public void customerWithProductOnPassiveAccountCannotBeDeleted() {
        throw new UnsupportedOperationException(
                "FR-007 ACC-004 uygulamada uygulanmiyor ve on kosulu UI ile kurulamiyor. "
                        + "Ayrinti icin sinif javadoc'una bakiniz.");
    }
}
