package com.crmlite.ui.pages.auth;
import com.crmlite.ui.core.waits.AppConditions;
import com.crmlite.ui.pages.BasePage;
import com.crmlite.ui.pages.customer.SearchCustomerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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

    public String usernameValue() {
        String value = getValue(USERNAME);
        return value == null ? "" : value;
    }

    public boolean isPasswordFieldDisplayed() {
        return isDisplayed(PASSWORD);
    }


    public boolean isSubmitEnabled() {
        return becomesEnabled(SUBMIT);
    }

    public boolean isSubmitDisabled() {
        return remainsDisabled(SUBMIT);
    }


    public LoginPage togglePasswordVisibility() {
        click(TOGGLE_PASSWORD);
        return this;
    }

    public boolean isPasswordMasked() {
        return "password".equals(getAttribute(PASSWORD, "type"));
    }

    public void waitUntilPasswordVisible() {
        wait.until(AppConditions.attributeToBe(PASSWORD, "type", "text"));
    }


    public boolean hasErrorMessage() {
        return isDisplayedAfterWait(ERROR_WRAPPER);
    }

    public String errorMessage() {
        return getText(ERROR_MESSAGE);
    }

    public String errorMessageColor() {
        return find(ERROR_MESSAGE).getCssValue("color");
    }

    public String usernameFieldError() {
        return fieldErrorText("username");
    }

    public boolean errorMessageDisappears() {
        try {
            wait.until(AppConditions.absentFromDom(ERROR_WRAPPER));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }


    public LoginPage submitCredentials(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        click(SUBMIT);
        return this;
    }

    public SearchCustomerPage loginAs(String username, String password) {
        submitCredentials(username, password);
        SearchCustomerPage searchPage = new SearchCustomerPage(driver);
        searchPage.waitUntilLoaded();
        return searchPage;
    }

    public LoginPage loginExpectingFailure(String username, String password) {
        submitCredentials(username, password);
        return this;
    }
}
