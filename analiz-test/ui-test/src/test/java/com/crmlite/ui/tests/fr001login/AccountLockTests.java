package com.crmlite.ui.tests.fr001login;

import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.pages.auth.LoginPage;
import com.crmlite.ui.tests.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.testng.SkipException;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FR-001 ACC-008 — Hesap kilitleme. <b>KARANTINA</b>.
 *
 * <p>Bu test Keycloak'in brute-force korumasini gercekten tetikler
 * ({@code failureFactor=5}, {@code waitIncrementSeconds=900}) ve kullandigi hesabi
 * <b>15 dakika kilitler</b>. Paylasilan {@code salesperson} hesabiyla calistirilirsa
 * sonraki tum testler bloke olur.
 *
 * <p>Bu yuzden yalnizca {@code quarantine} suite'inde, adanmis bir kullanici ile ve
 * acikca izin verildiginde calisir:
 * <pre>
 * ./mvnw test -Dsuite=quarantine -DlockoutTestEnabled=true \
 *             -Dui.username=&lt;adanmis&gt; -Dui.password=&lt;sifre&gt;
 * </pre>
 * Izin verilmediginde test <b>atlanir</b> — yanlislikla calistirilamaz.
 */
@Epic("FR-001 Sistem Girisi")
@Feature("UC-EACRML-001")
public class AccountLockTests extends BaseTest {

    private static final int FAILURE_FACTOR = 5;

    @Test(groups = {"fr001", "quarantine"},
            description = "UI-FR001-Q1 | 5 hatali deneme sonrasi hesap 15 dakika kilitlenir")
    @Story("ACC-008 — 5 hatali deneme -> 15 dk kilit")
    @TmsLink("FR-001-ACC-008")
    @Severity(SeverityLevel.CRITICAL)
    @Description("DIKKAT: Bu test kullanilan hesabi 15 dakika kilitler. CI'da ASLA calismamalidir. "
            + "-DlockoutTestEnabled=true verilmedikce atlanir.")
    public void fiveFailedAttemptsLockTheAccount() {
        if (!Boolean.getBoolean("lockoutTestEnabled")) {
            throw new SkipException(
                    "ACC-008 karantinada. Calistirmak icin -DlockoutTestEnabled=true verin. "
                            + "UYARI: kullanilan hesap 15 dakika kilitlenir; adanmis bir test "
                            + "kullanicisi ile calistirin (-Dui.username=... -Dui.password=...).");
        }

        LoginPage loginPage = new LoginPage(driver());
        loginPage.waitUntilLoaded();

        for (int attempt = 1; attempt <= FAILURE_FACTOR; attempt++) {
            loginPage.loginExpectingFailure(config.username(), "yanlis-sifre-" + attempt);
            assertThat(loginPage.hasErrorMessage())
                    .as("%d. denemede hata mesaji gorunur", attempt).isTrue();
        }

        assertThat(loginPage.errorMessage())
                .as("ACC-008 — kilitlenme mesaji")
                .isEqualTo(ExpectedMessages.get("login.accountLocked"));

        assertThat(loginPage.isSubmitDisabled())
                .as("ACC-008 — kilit suresince Login butonu pasif kalir")
                .isTrue();
    }
}
