package com.crmlite.ui.pages.components;

import com.crmlite.ui.core.waits.AppConditions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Adres ekleme/duzenleme modal'i. FR-003 (sihirbaz) ve FR-005 (musteri detayi)
 * ekranlarinda ayni davranisi gosterir.
 *
 * <p><b>Onemli:</b> iki ekran ayni modal'i kullanir ama <b>alan id'leri farklidir</b>:
 * <ul>
 *   <li>Create sihirbazi : {@code city}, {@code street}, {@code houseNumber}, {@code addressDescription}</li>
 *   <li>Musteri detayi   : {@code addr-city}, {@code addr-street}, {@code addr-houseNumber}, {@code addr-description}</li>
 * </ul>
 * Bu yuzden bilesen bir {@link Variant} ile olusturulur; davranis kodu tek yerde kalir.
 */
public class AddressModalComponent extends BaseComponent {

    private static final By ROOT = By.cssSelector(".modal-backdrop .modal-card");

    private static final By TITLE = By.cssSelector(".modal-card .modal-title");
    private static final By CLOSE_BUTTON = By.cssSelector(".modal-card .modal-close");
    private static final By CANCEL_BUTTON = By.cssSelector(".modal-card .modal-actions .app-button-secondary");
    private static final By SAVE_BUTTON = By.cssSelector(".modal-card .modal-actions .app-button-primary");
    private static final By SAVE_ERROR = By.cssSelector(".modal-card .address-save-error");

    /** Sihirbazdaki ve detaydaki modal'in alan id'leri farkli oldugu icin. */
    public enum Variant {
        CREATE_WIZARD("city", "street", "houseNumber", "addressDescription"),
        CUSTOMER_DETAIL("addr-city", "addr-street", "addr-houseNumber", "addr-description");

        private final String cityId;
        private final String streetId;
        private final String houseNumberId;
        private final String descriptionId;

        Variant(String cityId, String streetId, String houseNumberId, String descriptionId) {
            this.cityId = cityId;
            this.streetId = streetId;
            this.houseNumberId = houseNumberId;
            this.descriptionId = descriptionId;
        }
    }

    /**
     * Sehir listesindeki tek secenek. Secim <b>deger yerine etikete</b> gore yapilir:
     * secenek degerleri lookup id'sidir ve seed degistiginde kayar (201 -> 5), etiket kaymaz.
     */
    public static final String CITY_ANKARA_LABEL = "Ankara";

    private final Variant variant;

    public AddressModalComponent(WebDriver driver, Variant variant) {
        super(driver, ROOT);
        this.variant = variant;
    }

    @Override
    protected String pageName() {
        return "Adres Modal (" + variant + ")";
    }

    // --- Alan doldurma ---

    public AddressModalComponent selectCity(String cityValue) {
        selectByValue(By.id(variant.cityId), cityValue);
        return this;
    }

    public AddressModalComponent selectAnkara() {
        selectByVisibleText(By.id(variant.cityId), CITY_ANKARA_LABEL);
        return this;
    }

    public AddressModalComponent enterStreet(String street) {
        type(By.id(variant.streetId), street);
        return this;
    }

    public AddressModalComponent enterHouseNumber(String houseNumber) {
        type(By.id(variant.houseNumberId), houseNumber);
        return this;
    }

    public AddressModalComponent enterDescription(String description) {
        type(By.id(variant.descriptionId), description);
        return this;
    }

    /** Tum zorunlu alanlari doldurur (City varsayilan olarak Ankara). */
    public AddressModalComponent fill(String street, String houseNumber, String description) {
        return selectAnkara().enterStreet(street).enterHouseNumber(houseNumber).enterDescription(description);
    }

    // --- Okuma (ACC: "mevcut bilgiler dolu gelir") ---

    public String streetValue() {
        return getValue(By.id(variant.streetId));
    }

    public String houseNumberValue() {
        return getValue(By.id(variant.houseNumberId));
    }

    public String descriptionValue() {
        return getValue(By.id(variant.descriptionId));
    }

    public String cityValue() {
        return getValue(By.id(variant.cityId));
    }

    public String title() {
        return getText(TITLE);
    }

    // --- Durum ---

    /** FR-003 ACC-002 / FR-005 ACC-003, ACC-014: zorunlu alan eksikken Save pasif olmalidir. */
    public boolean isSaveDisabled() {
        return remainsDisabled(SAVE_BUTTON);
    }

    public boolean isSaveEnabled() {
        return becomesEnabled(SAVE_BUTTON);
    }

    public boolean hasSaveError() {
        return isDisplayedAfterWait(SAVE_ERROR);
    }

    public String saveErrorText() {
        return getText(SAVE_ERROR);
    }

    /** Alan bazli hata mesaji — {@code city | street | houseNumber | description}. */
    public String fieldError(Field field) {
        return fieldErrorText(fieldId(field));
    }

    public boolean hasFieldError(Field field) {
        return hasFieldError(fieldId(field));
    }

    public enum Field {
        CITY, STREET, HOUSE_NUMBER, DESCRIPTION
    }

    // --- Eylemler ---

    public void save() {
        click(SAVE_BUTTON);
    }

    public void cancel() {
        click(CANCEL_BUTTON);
    }

    public void closeWithX() {
        click(CLOSE_BUTTON);
    }

    /** Modal DOM'dan tamamen kalkana kadar bekler (Angular {@code @if} blogu elementi siliyor). */
    public void waitUntilClosed() {
        wait.until(AppConditions.absentFromDom(ROOT));
    }

    private String fieldId(Field field) {
        return switch (field) {
            case CITY -> variant.cityId;
            case STREET -> variant.streetId;
            case HOUSE_NUMBER -> variant.houseNumberId;
            case DESCRIPTION -> variant.descriptionId;
        };
    }
}
