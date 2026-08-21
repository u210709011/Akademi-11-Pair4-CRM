package com.crmlite.ui.tests.fr001login;

import com.crmlite.ui.data.model.ValidationCase;
import com.crmlite.ui.data.provider.ValidationDataProvider;
import com.crmlite.ui.pages.auth.LoginPage;
import com.crmlite.ui.tests.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FR-001 ACC-002 — Login butonunun aktiflik matrisi.
 *
 * <p>Vakalar {@code testdata/login-validation.json} dosyasindan gelir; yeni bir
 * kombinasyon eklemek icin kod degistirmek gerekmez.
 */
@Epic("FR-001 Sistem Girisi")
@Feature("UC-EACRML-001")
public class LoginValidationTests extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void openLoginPage() {
        loginPage = new LoginPage(driver());
        loginPage.waitUntilLoaded();
    }

    @Test(groups = {"fr001", "regression"},
            dataProvider = "loginValidation", dataProviderClass = ValidationDataProvider.class,
            description = "UI-FR001-04 | Zorunlu alan kombinasyonlarina gore Login butonu durumu")
    @Story("ACC-002 — Zorunlu alanlar dolmadan Login pasif")
    @TmsLink("FR-001-ACC-002")
    @Severity(SeverityLevel.CRITICAL)
    public void submitButtonStateMatchesRequiredFields(ValidationCase testCase) {
        loginPage.enterUsername(testCase.username());
        loginPage.enterPassword(testCase.password());

        if (testCase.expectsActionEnabled()) {
            assertThat(loginPage.isSubmitEnabled())
                    .as("%s -> Login butonu AKTIF olmalidir", testCase)
                    .isTrue();
        } else {
            assertThat(loginPage.isSubmitDisabled())
                    .as("%s -> Login butonu PASIF kalmalidir", testCase)
                    .isTrue();
        }
    }
}
