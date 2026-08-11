package com.crmlite.ui.pages.auth;

import com.crmlite.ui.core.waits.AppConditions;
import com.crmlite.ui.pages.BasePage;
import com.crmlite.ui.pages.customer.SearchCustomerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * FR-001 — Sistem Girisi (UC-EACRML-001).
 */
public class LoginPage extends BasePage {

    private static final By USERNAME = By.id("username");
    private static final By PASSWORD = By.id("password");
    private static final By SUBMIT = By.cssSelector("form.login-form button.submit-btn");
    private static final By TOGGLE_PASSWORD = By.cssSelector(".toggle-password");

    private static final By ERROR_WRAPPER = By.cssSelector("form.login-form .error-wrapper");
    private static final By ERROR_MESSAGE = By.cssSelector("form.login-form .error-message");

    private static final By TITLE = By.cssSelector(".login-card h2");
    private static final By BADGE = By.cssSelector(".login-card .badge");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By pageReadyLocator() {
        return USERNAME;
    }

    @Override
    protected String pageName() {
        return "Login";
    }

    // --- Alanlar (ACC-001) ---

    public LoginPage enterUsername(String username) {
        type(USERNAME, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(PASSWORD, password);
        return this;
    }

    public String title() {
        return getText(TITLE);
    }

    public String badge() {
        return getText(BADGE);
    }

    public boolean isUsernameFieldDisplayed() {
        return isDisplayed(USERNAME);
    }

    /** Alandaki etkin deger — uzunluk sinirinin gercekten uygulandigini dogrulamak icin. */
    public String usernameValue() {
        String value = getValue(USERNAME);
        return value == null ? "" : value;
    }

    public boolean isPasswordFieldDisplayed() {
        return isDisplayed(PASSWORD);
    }

    // --- Login butonu (ACC-002) ---

    /** ACC-002: iki alan da dolunca Login butonu aktiflesmelidir. */
    public boolean isSubmitEnabled() {
        return becomesEnabled(SUBMIT);
    }

    /** ACC-002 negatif: alanlardan biri bossa Login butonu pasif KALMALIDIR. */
    public boolean isSubmitDisabled() {
        return remainsDisabled(SUBMIT);
    }

    // --- Sifre gorunurlugu (ACC-003) ---

    public LoginPage togglePasswordVisibility() {
        click(TOGGLE_PASSWORD);
        return this;
    }

    /** ACC-003: goz ikonu ile {@code type} alani password &lt;-&gt; text arasinda degisir. */
    public boolean isPasswordMasked() {
        return "password".equals(getAttribute(PASSWORD, "type"));
    }

    public void waitUntilPasswordVisible() {
        wait.until(AppConditions.attributeToBe(PASSWORD, "type", "text"));
    }

    // --- Hata mesaji (ACC-005, ACC-006, ACC-008) ---

    public boolean hasErrorMessage() {
        return isDisplayedAfterWait(ERROR_WRAPPER);
    }

    /**
     * ACC-005 -> "Wrong user name or password. Please try again."
     * ACC-008 -> "Your account has been locked. Please try again after 15 minutes."
     */
    public String errorMessage() {
        return getText(ERROR_MESSAGE);
    }

    /** ACC-005: mesajin kirmizi gosterildigi dogrulanir (konum gorsel kriterdir, otomatize edilmez). */
    public String errorMessageColor() {
        return find(ERROR_MESSAGE).getCssValue("color");
    }

    /** Hata mesaji kaybolana kadar bekler (ACC-006). */
    public boolean errorMessageDisappears() {
        try {
            wait.until(AppConditions.absentFromDom(ERROR_WRAPPER));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    // --- Eylemler ---

    /** Kimlik bilgilerini girer ve Login'e tiklar; sayfa gecisi dogrulanmaz. */
    public LoginPage submitCredentials(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        click(SUBMIT);
        return this;
    }

    /**
     * ACC-004, ACC-009, ACC-010: basarili giris — Customer Search ekranina gecilir.
     * Yanlis sayfaya gidilirse hata derleme degil, {@code PageNotLoadedException} ile ortaya cikar.
     */
    public SearchCustomerPage loginAs(String username, String password) {
        submitCredentials(username, password);
        SearchCustomerPage searchPage = new SearchCustomerPage(driver);
        searchPage.waitUntilLoaded();
        return searchPage;
    }

    /** ACC-007: hatali giris — Login ekraninda kalinir. */
    public LoginPage loginExpectingFailure(String username, String password) {
        submitCredentials(username, password);
        return this;
    }
}
