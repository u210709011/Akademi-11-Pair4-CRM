package com.crmlite.ui.pages.newsale;

import com.crmlite.ui.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * FR-015 — "Product Configuration" ekrani (Yeni Satis sihirbazinin ikinci adimi).
 *
 * <p>Sihirbaz tek kabuk bilesende yasadigi icin ROTA DEGISMEZ; hangi adimda oldugumuz
 * stepper'daki aktif isaretten okunur (bkz. {@link OfferSelectionPage#activeStepLabel()}).
 *
 * <p>Sepetteki her urun icin bir {@code .config-product-card} render edilir. Urunun
 * karakteristik alani yoksa kart, alan izgarasi yerine bir bilgilendirme notu gosterir -
 * bu bir hata degildir, seed verisinde her urunun karakteristigi yoktur.
 */
public class ConfigurationStepPage extends BasePage {

    private static final By ROOT = By.cssSelector(".config-content-wrap");

    private static final By PRODUCT_CARDS = By.cssSelector(".config-product-card");
    private static final By PRODUCT_NAMES = By.cssSelector(".config-product-card .config-product-name");
    private static final By CONFIG_FIELDS = By.cssSelector(".config-product-card .config-field");
    private static final By PENDING_NOTES = By.cssSelector(".config-product-card .config-product-note");

    // --- Servis adresi ---
    private static final By ADDRESS_CARD = By.cssSelector(".config-address-card");
    private static final By ADDRESS_VALUE = By.cssSelector(".config-address-body .config-address-value");
    private static final By ADDRESS_EMPTY = By.cssSelector(".config-address-empty");
    // Iki buton da .secondary-button; sira ile ayrilirlar (once Change, sonra Add New).
    private static final By CHANGE_ADDRESS_BUTTON =
            By.cssSelector(".config-address-actions .secondary-button:nth-of-type(1)");
    private static final By ADD_ADDRESS_BUTTON =
            By.cssSelector(".config-address-actions .secondary-button:nth-of-type(2)");

    // --- Mevcut adresi secme modali ---
    private static final By ADDRESS_OPTIONS = By.cssSelector(".address-option-list .address-option");

    // --- Yeni adres modali ---
    private static final By NEW_ADDRESS_CITY = By.id("new-addr-city");
    private static final By NEW_ADDRESS_STREET = By.id("new-addr-street");
    private static final By NEW_ADDRESS_HOUSE_NO = By.id("new-addr-houseNumber");
    private static final By NEW_ADDRESS_DESC = By.id("new-addr-description");
    private static final By MODAL_SAVE_BUTTON = By.cssSelector(".modal-actions .save-button");
    private static final By MODAL_CANCEL_BUTTON = By.cssSelector(".modal-actions .cancel-button");
    private static final By MODAL_CARD = By.cssSelector(".modal-card");

    // --- Vazgecme onayi (ACC-005) ---
    private static final By DISCARD_MESSAGE = By.cssSelector(".delete-confirm-message");
    private static final By DISCARD_CONFIRM_BUTTON = By.cssSelector(".modal-actions .delete-confirm-button");

    private static final By SUCCESS_TOAST = By.cssSelector(".success-toast-message");

    public ConfigurationStepPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By pageReadyLocator() {
        return ROOT;
    }

    @Override
    protected String pageName() {
        return "Product Configuration";
    }

    public boolean isDisplayed() {
        return isDisplayed(ROOT);
    }

    // --- ACC-002: urun basina konfigurasyon bolumu ---

    public int productCardCount() {
        return findAll(PRODUCT_CARDS).size();
    }

    public List<String> productNames() {
        List<String> names = new ArrayList<>();
        for (WebElement el : findAll(PRODUCT_NAMES)) {
            names.add(el.getText().trim());
        }
        return names;
    }

    /** Karakteristik alani olan kartlardaki toplam alan sayisi. */
    public int configFieldCount() {
        return findAll(CONFIG_FIELDS).size();
    }

    /** Karakteristigi olmayan urunler icin gosterilen bilgilendirme notu sayisi. */
    public int pendingNoteCount() {
        return findAll(PENDING_NOTES).size();
    }

    // --- ACC-003, ACC-007: servis adresi ---

    public boolean hasServiceAddressSection() {
        return isDisplayed(ADDRESS_CARD);
    }

    public boolean hasSelectedAddress() {
        return isDisplayed(ADDRESS_VALUE);
    }

    public String selectedAddressText() {
        return getText(ADDRESS_VALUE);
    }

    public String noAddressText() {
        return getText(ADDRESS_EMPTY);
    }

    public boolean hasChangeAddressButton() {
        return isDisplayed(CHANGE_ADDRESS_BUTTON);
    }

    public boolean hasAddAddressButton() {
        return isDisplayed(ADD_ADDRESS_BUTTON);
    }

    // --- Mevcut adresten secme ---

    public ConfigurationStepPage openChangeAddressModal() {
        click(CHANGE_ADDRESS_BUTTON);
        wait.until(driver -> !driver.findElements(ADDRESS_OPTIONS).isEmpty());
        return this;
    }

    public int addressOptionCount() {
        return findAll(ADDRESS_OPTIONS).size();
    }

    public ConfigurationStepPage selectAddressOption(int index) {
        findAll(ADDRESS_OPTIONS).get(index).click();
        return this;
    }

    // --- ACC-004, ACC-005, ACC-006: yeni adres ---

    public ConfigurationStepPage openAddAddressModal() {
        click(ADD_ADDRESS_BUTTON);
        wait.until(driver -> !driver.findElements(NEW_ADDRESS_STREET).isEmpty());
        return this;
    }

    /** ACC-004: dokumanin sart kostugu dort alan modalda bulunmalidir. */
    public boolean hasAllNewAddressFields() {
        return isDisplayed(NEW_ADDRESS_CITY)
                && isDisplayed(NEW_ADDRESS_STREET)
                && isDisplayed(NEW_ADDRESS_HOUSE_NO)
                && isDisplayed(NEW_ADDRESS_DESC);
    }

    public ConfigurationStepPage fillNewAddress(String city, String street,
                                                String houseNumber, String description) {
        selectByVisibleText(NEW_ADDRESS_CITY, city);
        type(NEW_ADDRESS_STREET, street);
        type(NEW_ADDRESS_HOUSE_NO, houseNumber);
        type(NEW_ADDRESS_DESC, description);
        return this;
    }

    public boolean isSaveDisabled() {
        return remainsDisabled(MODAL_SAVE_BUTTON);
    }

    public ConfigurationStepPage saveNewAddress() {
        click(MODAL_SAVE_BUTTON);
        return this;
    }

    public ConfigurationStepPage cancelNewAddress() {
        click(MODAL_CANCEL_BUTTON);
        return this;
    }

    /** ACC-005: Cancel'da gosterilen uyari metni. */
    public String discardConfirmMessage() {
        return getText(DISCARD_MESSAGE);
    }

    public ConfigurationStepPage confirmDiscard() {
        click(DISCARD_CONFIRM_BUTTON);
        wait.until(driver -> driver.findElements(MODAL_CARD).isEmpty());
        return this;
    }

    public boolean isModalClosed() {
        return findAll(MODAL_CARD).isEmpty();
    }

    /** ACC-006: adres kaydedildiginde gosterilen basari mesaji. */
    public String successMessage() {
        return getText(SUCCESS_TOAST);
    }

    public boolean hasSuccessMessage() {
        return isDisplayedAfterWait(SUCCESS_TOAST);
    }
}
