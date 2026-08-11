package com.crmlite.ui.pages.customer.create;

import com.crmlite.ui.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * FR-003 3. adim — Iletisim bilgileri.
 *
 * <p>Zorunlu: E-mail, Mobile Phone. Opsiyonel: Home Phone, Fax.
 *
 * <p><b>Dokuman notu:</b> Home Phone kurali FR-003 tablosunda "10 hane, 2 ile baslar",
 * FR-006 tablosunda "10–11 hane" olarak geciyor. Uygulama FR-003 surumunu uyguluyor
 * ("Home phone must start with 2 and be 10 digits.").
 */
public class ContactStep extends BasePage {

    private static final By FORM = By.cssSelector("form.contact-form");

    private static final By EMAIL = By.id("email");
    private static final By HOME_PHONE = By.id("homePhone");
    private static final By MOBILE_PHONE = By.id("mobilePhone");
    private static final By FAX = By.id("fax");

    public ContactStep(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By pageReadyLocator() {
        return FORM;
    }

    @Override
    protected String pageName() {
        return "Create Customer - Kontakt adimi";
    }

    // --- Alan doldurma (ACC-013) ---

    public ContactStep enterEmail(String value) {
        type(EMAIL, value);
        return this;
    }

    public ContactStep enterHomePhone(String value) {
        type(HOME_PHONE, value);
        return this;
    }

    public ContactStep enterMobilePhone(String value) {
        type(MOBILE_PHONE, value);
        return this;
    }

    public ContactStep enterFax(String value) {
        type(FAX, value);
        return this;
    }

    /** Yalnizca zorunlu alanlar. */
    public ContactStep fillRequired(String email, String mobilePhone) {
        return enterEmail(email).enterMobilePhone(mobilePhone);
    }

    public ContactStep fillAll(String email, String mobilePhone, String homePhone, String fax) {
        return fillRequired(email, mobilePhone).enterHomePhone(homePhone).enterFax(fax);
    }

    // --- Okuma ---

    public String emailValue() {
        return getValue(EMAIL);
    }

    public String mobilePhoneValue() {
        return getValue(MOBILE_PHONE);
    }

    public String homePhoneValue() {
        return getValue(HOME_PHONE);
    }

    public String faxValue() {
        return getValue(FAX);
    }

    /**
     * Alandan odagi kaldirir — hata mesajlari {@code touched()} kosuluna bagli
     * gosterildigi icin mesaj assertion'larindan once cagrilmalidir.
     */
    public ContactStep blurField(Field field) {
        blur(By.id(field.fieldId()));
        return this;
    }

    // --- Hatalar (ACC-014) ---

    public String fieldError(Field field) {
        return fieldErrorText(field.id);
    }

    public boolean hasFieldError(Field field) {
        return hasFieldError(field.id);
    }

    public enum Field {
        EMAIL("email"),
        HOME_PHONE("homePhone"),
        MOBILE_PHONE("mobilePhone"),
        FAX("fax");

        private final String id;

        Field(String id) {
            this.id = id;
        }

        public String fieldId() {
            return id;
        }
    }
}
