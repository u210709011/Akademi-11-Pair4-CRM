package com.crmlite.ui.pages.customer;

import com.crmlite.ui.data.model.Gender;
import com.crmlite.ui.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * FR-004 — Musteri Bilgilerini Guncelleme (UC-EACRML-004).
 * Rota: {@code /detail-customer/{custId}/update}
 *
 * <p><b>Dokuman notu:</b> ACC-005 "Cancel butonu" diyor; UC-004 Alt-4 ve uygulama
 * <b>Previous</b> kullaniyor ({@code .previous-button}). Islev aynidir: degisiklikleri
 * kaydetmeden Customer Info ekranina doner.
 */
public class UpdateCustomerPage extends BasePage {

    // Cinsiyet secenek degerleri lookup id'sidir ve seed degistiginde kayar (1/2 -> 3/4).
    // Sabit tutulmaz; deger gerektiginde Gender.X.value(), secim icin selectGender(Gender)
    // kullanilir (etikete gore secer, id'den bagimsizdir).

    private static final By FORM = By.cssSelector("form.update-form");
    private static final By PAGE_TITLE = By.cssSelector(".update-customer-page h1.page-title");

    private static final By FIRST_NAME = By.id("firstName");
    private static final By MIDDLE_NAME = By.id("middleName");
    private static final By LAST_NAME = By.id("lastName");
    private static final By BIRTH_DATE = By.id("birthDate");
    private static final By GENDER = By.id("gender");
    private static final By FATHER_NAME = By.id("fatherName");
    private static final By MOTHER_NAME = By.id("motherName");
    private static final By NATIONAL_ID = By.id("nationalId");

    private static final By SAVE_BUTTON = By.cssSelector(".update-actions .app-button-primary");
    private static final By PREVIOUS_BUTTON = By.cssSelector(".update-actions .previous-button");
    private static final By SAVE_ERROR = By.cssSelector(".update-card .identity-error-banner");
    private static final By STATUS_MESSAGE = By.cssSelector(".update-customer-page .status-message");

    public UpdateCustomerPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By pageReadyLocator() {
        return FORM;
    }

    @Override
    protected String pageName() {
        return "Customer Info Update";
    }

    public String pageTitle() {
        return getText(PAGE_TITLE);
    }

    public boolean isLoading() {
        return isDisplayed(STATUS_MESSAGE);
    }

    // --- Mevcut degerler (ACC-002: bilgiler dolu gelir) ---

    public String firstNameValue() {
        return getValue(FIRST_NAME);
    }

    public String middleNameValue() {
        return getValue(MIDDLE_NAME);
    }

    public String lastNameValue() {
        return getValue(LAST_NAME);
    }

    public String birthDateValue() {
        return getValue(BIRTH_DATE);
    }

    public String genderValue() {
        return getValue(GENDER);
    }

    public String fatherNameValue() {
        return getValue(FATHER_NAME);
    }

    public String motherNameValue() {
        return getValue(MOTHER_NAME);
    }

    public String nationalIdValue() {
        return getValue(NATIONAL_ID);
    }

    /** Karakter sayaci ("12/50") — 50 karakter sinirini dogrulamada kullanilir. */
    public String charCounter(Field field) {
        return getText(By.xpath(String.format(
                "//label[@for='%s']/following-sibling::span[contains(@class,'char-counter')]", field.id)));
    }

    // --- Guncelleme (ACC-003) ---

    public UpdateCustomerPage enterFirstName(String value) {
        type(FIRST_NAME, value);
        return this;
    }

    public UpdateCustomerPage enterMiddleName(String value) {
        type(MIDDLE_NAME, value);
        return this;
    }

    public UpdateCustomerPage enterLastName(String value) {
        type(LAST_NAME, value);
        return this;
    }

    /**
     * Parametre gereksinimdeki gibi {@code DD/MM/YYYY}; donusum
     * {@link com.crmlite.ui.core.utils.DateUtil#toDatepickerInput} icinde yapilir
     * (bkz. oradaki urun bulgusu notu — datepicker {@code parse()} ezilmemis).
     */
    public UpdateCustomerPage enterBirthDate(String ddMMyyyy) {
        type(BIRTH_DATE, com.crmlite.ui.core.utils.DateUtil.toDatepickerInput(ddMMyyyy));
        return this;
    }

    /** Ham deger ile secim — gecersiz deger gerektiren negatif senaryolar icin. */
    public UpdateCustomerPage selectGender(String genderValue) {
        selectByValue(GENDER, genderValue);
        return this;
    }

    /** Etikete gore secim — pozitif senaryolarda tercih edilir, lookup id'sinden bagimsizdir. */
    public UpdateCustomerPage selectGender(Gender gender) {
        selectByVisibleText(GENDER, gender.label());
        return this;
    }

    public UpdateCustomerPage enterFatherName(String value) {
        type(FATHER_NAME, value);
        return this;
    }

    public UpdateCustomerPage enterMotherName(String value) {
        type(MOTHER_NAME, value);
        return this;
    }

    public UpdateCustomerPage enterNationalId(String value) {
        type(NATIONAL_ID, value);
        return this;
    }

    public UpdateCustomerPage clearField(Field field) {
        clear(By.id(field.id));
        return this;
    }

    /**
     * Alandan odagi kaldirir.
     *
     * <p>Hata mesajlari {@code touched()} kosuluna bagli
     * ({@code nationalId().touched() && value().length < 11}); odak kaldirilmadan
     * mesaj DOM'a hic eklenmez.
     */
    public UpdateCustomerPage blurField(Field field) {
        blur(By.id(field.id));
        return this;
    }

    /**
     * Gender listesindeki placeholder secenegi ({@code <option value="" disabled>}).
     *
     * <p>Secenek {@code disabled} oldugu icin gender bir kez secildikten sonra
     * <b>UI uzerinden bosaltilamaz</b>; "gender zorunlu" kurali bu sekilde uygulanir.
     */
    public boolean isGenderPlaceholderDisabled() {
        return !find(By.cssSelector("#gender option[value='']")).isEnabled();
    }

    // --- Buton durumu (ACC-004) ---

    /** ACC-004: zorunlu alanlardan biri bossa Save pasif KALMALIDIR. */
    public boolean isSaveDisabled() {
        return remainsDisabled(SAVE_BUTTON);
    }

    public boolean isSaveEnabled() {
        return becomesEnabled(SAVE_BUTTON);
    }

    // --- Eylemler ---

    /** ACC-010/011: kaydeder ve Customer Info ekranina doner. */
    public CustomerDetailPage save() {
        click(SAVE_BUTTON);
        CustomerDetailPage detail = new CustomerDetailPage(driver);
        detail.waitUntilLoaded();
        return detail;
    }

    /** ACC-006/007: tekillik cakismasi bekleniyorsa — sayfada kalinir. */
    public UpdateCustomerPage saveExpectingFailure() {
        click(SAVE_BUTTON);
        return this;
    }

    /** ACC-005: degisiklikleri kaydetmeden Customer Info'ya doner. */
    public CustomerDetailPage previous() {
        click(PREVIOUS_BUTTON);
        CustomerDetailPage detail = new CustomerDetailPage(driver);
        detail.waitUntilLoaded();
        return detail;
    }

    // --- Hatalar (ACC-007, ACC-009) ---

    /** ACC-007: Nationality ID cakismasi uyarisi. */
    public boolean hasSaveError() {
        return isDisplayedAfterWait(SAVE_ERROR);
    }

    public String saveErrorText() {
        return getText(SAVE_ERROR);
    }

    public String fieldError(Field field) {
        return fieldErrorText(field.id);
    }

    public boolean hasFieldError(Field field) {
        return hasFieldError(field.id);
    }

    public enum Field {
        FIRST_NAME("firstName"),
        MIDDLE_NAME("middleName"),
        LAST_NAME("lastName"),
        BIRTH_DATE("birthDate"),
        GENDER("gender"),
        FATHER_NAME("fatherName"),
        MOTHER_NAME("motherName"),
        NATIONAL_ID("nationalId");

        private final String id;

        Field(String id) {
            this.id = id;
        }

        public String fieldId() {
            return id;
        }
    }
}
