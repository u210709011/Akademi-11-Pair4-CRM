package com.crmlite.ui.pages.customer.create;

import com.crmlite.ui.core.utils.DateUtil;
import com.crmlite.ui.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * FR-003 1. adim — Demografik bilgiler.
 *
 * <p>Zorunlu alanlar: First Name, Last Name, Birth Date, Gender, Nationality ID.
 * Opsiyonel: Middle Name, Father Name, Mother Name.
 */
public class DemographicStep extends BasePage {

    public static final String GENDER_MALE = "1";
    public static final String GENDER_FEMALE = "2";

    private static final By FORM = By.cssSelector("form.create-form");

    private static final By FIRST_NAME = By.id("firstName");
    private static final By MIDDLE_NAME = By.id("middleName");
    private static final By LAST_NAME = By.id("lastName");
    private static final By BIRTH_DATE = By.id("birthDate");
    private static final By GENDER = By.id("gender");
    private static final By FATHER_NAME = By.id("fatherName");
    private static final By MOTHER_NAME = By.id("motherName");
    private static final By NATIONAL_ID = By.id("nationalId");

    private static final By IDENTITY_ERROR = By.cssSelector("form.create-form .identity-error-banner");

    public DemographicStep(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By pageReadyLocator() {
        return FORM;
    }

    @Override
    protected String pageName() {
        return "Create Customer - Demografik adim";
    }

    // --- Alan doldurma ---

    public DemographicStep enterFirstName(String value) {
        type(FIRST_NAME, value);
        return this;
    }

    public DemographicStep enterMiddleName(String value) {
        type(MIDDLE_NAME, value);
        return this;
    }

    public DemographicStep enterLastName(String value) {
        type(LAST_NAME, value);
        return this;
    }

    /**
     * Dogum tarihi girer. Parametre gereksinimdeki gibi {@code DD/MM/YYYY} formatindadir;
     * donusum {@link DateUtil#toDatepickerInput} icinde yapilir (bkz. oradaki urun bulgusu notu).
     */
    public DemographicStep enterBirthDate(String ddMMyyyy) {
        type(BIRTH_DATE, DateUtil.toDatepickerInput(ddMMyyyy));
        return this;
    }

    public DemographicStep selectGender(String genderValue) {
        selectByValue(GENDER, genderValue);
        return this;
    }

    public DemographicStep enterFatherName(String value) {
        type(FATHER_NAME, value);
        return this;
    }

    public DemographicStep enterMotherName(String value) {
        type(MOTHER_NAME, value);
        return this;
    }

    public DemographicStep enterNationalId(String value) {
        type(NATIONAL_ID, value);
        return this;
    }

    /** Yalnizca zorunlu alanlari doldurur. */
    public DemographicStep fillRequired(String firstName, String lastName,
                                        String birthDate, String genderValue, String nationalId) {
        return enterFirstName(firstName)
                .enterLastName(lastName)
                .enterBirthDate(birthDate)
                .selectGender(genderValue)
                .enterNationalId(nationalId);
    }

    // --- Okuma ---

    public String firstNameValue() {
        return getValue(FIRST_NAME);
    }

    public String lastNameValue() {
        return getValue(LAST_NAME);
    }

    public String nationalIdValue() {
        return getValue(NATIONAL_ID);
    }

    public String birthDateValue() {
        return getValue(BIRTH_DATE);
    }

    public String genderValue() {
        return getValue(GENDER);
    }

    /**
     * Alandan odagi kaldirir.
     *
     * <p>Alan hata mesajlari {@code touched()} kosuluna bagli gosteriliyor
     * (or. {@code nationalId().touched() && value().length < 11}); odak kaldirilmadan
     * alan "touched" sayilmaz ve mesaj DOM'a hic eklenmez.
     */
    public DemographicStep blurField(Field field) {
        blur(By.id(field.fieldId()));
        return this;
    }

    // --- Hatalar ---

    /** ACC-005/007: tekillik veya KPS dogrulama hatasi banner'i. */
    public boolean hasIdentityError() {
        return isDisplayedAfterWait(IDENTITY_ERROR);
    }

    public String identityErrorText() {
        return getText(IDENTITY_ERROR);
    }

    /** Alan bazli hata mesaji ("This field is required.", "National ID must contain only 11 digits." vb.). */
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
