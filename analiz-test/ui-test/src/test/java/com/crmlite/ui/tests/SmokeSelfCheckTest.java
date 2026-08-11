package com.crmlite.ui.tests;

import com.crmlite.ui.core.report.AllureAttachment;
import com.crmlite.ui.core.utils.BrowserStorageUtil;
import com.crmlite.ui.core.waits.WaitFactory;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.SkipException;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Faz 1 kabul testi: <b>hicbir gereksinimi test etmez</b>, yalnizca cercevenin
 * ayakta oldugunu kanitlar.
 *
 * <p>Ikiye ayrilmistir:
 * <ul>
 *   <li>{@link #driverBootstrapWorks()} — uygulamaya ihtiyac duymaz, her yerde calisir</li>
 *   <li>{@link #applicationIsReachable()} — uygulama kapaliysa HATA vermez, SKIP eder</li>
 * </ul>
 */
@Epic("Cerceve Self-Check")
@Feature("Faz 1 — Altyapi")
public class SmokeSelfCheckTest extends BaseTest {

    private static final By LOGIN_USERNAME = By.id("username");
    private static final By LOGIN_PASSWORD = By.id("password");
    private static final By LOGIN_SUBMIT = By.cssSelector("form.login-form button.submit-btn");

    /** Uygulamaya gidilmesin: asagidaki testler navigasyonu kendileri yonetiyor. */
    @Override
    protected boolean shouldOpenApplication() {
        return false;
    }

    @Test(description = "Driver, konfigurasyon ve bekleme altyapisi calisir (uygulama gerektirmez)")
    @Story("WebDriver ve konfigurasyon bootstrap")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Selenium Manager driver'i cozer, DriverManager ThreadLocal'e yazar, "
            + "JavaScript calistirilabilir ve konfigurasyon dogrulanmis olarak yuklenir.")
    public void driverBootstrapWorks() {
        Allure.step("Konfigurasyonun yuklendigi dogrulanir");
        assertThat(config.baseUrl()).as("base.url").isNotBlank();
        assertThat(config.apiBaseUrl()).as("api.base.url").isNotBlank();
        assertThat(config.uiLanguage()).as("ui.language").isEqualTo("en");

        Allure.step("WebDriver ayakta ve JavaScript calistirabiliyor");
        WebDriver driver = driver();
        driver.get("data:text/html;charset=utf-8,<html><title>self-check</title><body>ok</body></html>");

        assertThat(driver.getTitle()).as("tarayici sayfa basligi").isEqualTo("self-check");

        Allure.step("Bekleme altyapisi calisiyor");
        WaitFactory.shortWait(driver).until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        AllureAttachment.text("Cozulen konfigurasyon", String.format(
                "env=%s%nbaseUrl=%s%napiBaseUrl=%s%nbrowser=%s%nheadless=%s%ndil=%s",
                config.env(), config.baseUrl(), config.apiBaseUrl(),
                config.browser(), config.headless(), config.uiLanguage()));
    }

    @Test(description = "Uygulama ayaktaysa login ekrani acilir ve dil sabitlenir")
    @Story("Uygulama erisilebilirligi")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Uygulama kapaliysa test BASARISIZ olmaz, ATLANIR (SkipException): "
            + "bu bir urun hatasi degil, ortam durumudur.")
    public void applicationIsReachable() {
        WebDriver driver = driver();

        try {
            openApplication();
        } catch (RuntimeException e) {
            throw new SkipException(
                    "Uygulama " + config.baseUrl() + " adresinde ayakta degil. "
                            + "front-end icin 'ng serve', back-end icin api-gateway calistirilmalidir.");
        }

        Allure.step("Login ekrani yuklendi mi");
        try {
            WaitFactory.defaultWait(driver)
                    .until(ExpectedConditions.visibilityOfElementLocated(LOGIN_USERNAME));
        } catch (TimeoutException e) {
            throw new SkipException(
                    "Uygulamaya baglanildi ancak login ekrani gelmedi (" + driver.getCurrentUrl() + "). "
                            + "Front-end derlenmis ve /login rotasi erisilebilir olmalidir.");
        }

        Allure.step("Login formunun temel alanlari mevcut");
        assertThat(driver.findElements(LOGIN_USERNAME)).as("kullanici adi alani").hasSize(1);
        assertThat(driver.findElements(LOGIN_PASSWORD)).as("sifre alani").hasSize(1);
        assertThat(driver.findElements(LOGIN_SUBMIT)).as("Login butonu").hasSize(1);

        Allure.step("UI dili beklenen degere sabitlendi");
        assertThat(BrowserStorageUtil.getLocalStorage(driver, BrowserStorageUtil.LANG_KEY))
                .as("localStorage['%s']", BrowserStorageUtil.LANG_KEY)
                .isEqualTo(config.uiLanguage());

        AllureAttachment.screenshot("Login ekrani",
                com.crmlite.ui.core.utils.ScreenshotUtil.capture(driver));
    }
}
