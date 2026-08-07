package com.crmlite.ui.pages.components;

import com.crmlite.ui.core.utils.JsUtil;
import com.crmlite.ui.core.waits.AppConditions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * FR-008 — "Create Billing Account" modal'i (musteri detayi > Accounts sekmesi).
 *
 * <p><b>Dokumandan yapisal fark:</b> ACC-006/ACC-007 ayri bir "yeni adres ekrani"ndan
 * ve o ekranin kendi Save/Cancel butonlarindan bahseder. Uygulamada boyle ayri bir ekran
 * yoktur: "+ Add New Address" baglantisi ayni modal icinde satir ici bir form acar
 * ({@code toggleAddNewAddressForAccount}) ve adres, hesapla <b>birlikte</b> tek istekte
 * ({@code newAddress} alani) kaydedilir. Bu yuzden burada ayri bir adres-kaydet eylemi yoktur.
 *
 * <p>Modal iki modda calisir:
 * <ul>
 *   <li><b>Mevcut adres</b> (varsayilan): {@code #account-address} select'inden secim</li>
 *   <li><b>Yeni adres</b>: {@code #account-new-*} alanlari doldurulur</li>
 * </ul>
 * Ikisinden tam olarak biri gecerli olmalidir — backend de ayni kurali uygular.
 */
public class BillingAccountModalComponent extends BaseComponent {

    private static final By ROOT = By.cssSelector(".modal-backdrop .modal-card");

    private static final By TITLE = By.cssSelector(".modal-card .modal-title");
    private static final By CLOSE_BUTTON = By.cssSelector(".modal-card .modal-close");
    private static final By CANCEL_BUTTON = By.cssSelector(".modal-card .modal-actions .cancel-button");
    private static final By CREATE_BUTTON = By.cssSelector(".modal-card .modal-actions .save-button");
    private static final By SAVE_ERROR = By.cssSelector(".modal-card .address-save-error");
    private static final By TOGGLE_ADDRESS_MODE = By.cssSelector(".modal-card .add-new-address-link");

    /** Ankara — deger yerine etikete gore secilir (bkz. AddressModalComponent). */
    public static final String CITY_ANKARA_LABEL = "Ankara";

    /** Modal alanlari; id'ler {@code detail-customer.component.html} ile birebir. */
    public enum Field {
        ACCOUNT_NAME("account-name"),
        ACCOUNT_DESC("account-desc"),
        SERVICE_ADDRESS("account-address"),
        NEW_CITY("account-new-city"),
        NEW_STREET("account-new-street"),
        NEW_HOUSE_NUMBER("account-new-houseNumber"),
        NEW_DESCRIPTION("account-new-description");

        private final String id;

        Field(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }

        public By locator() {
            return By.id(id);
        }
    }

    public BillingAccountModalComponent(WebDriver driver) {
        super(driver, ROOT);
    }

    @Override
    protected String pageName() {
        return "Create Billing Account Modal";
    }

    // --- Hesap alanlari (ACC-003) ---

    public BillingAccountModalComponent enterAccountName(String name) {
        type(Field.ACCOUNT_NAME.locator(), name);
        return this;
    }

    public BillingAccountModalComponent enterAccountDescription(String description) {
        type(Field.ACCOUNT_DESC.locator(), description);
        return this;
    }

    /** ACC-003: iki alanin da ekranda bulunmasi. */
    public boolean hasAccountNameField() {
        return isDisplayed(Field.ACCOUNT_NAME.locator());
    }

    public boolean hasAccountDescriptionField() {
        return isDisplayed(Field.ACCOUNT_DESC.locator());
    }

    // --- Adres secimi (ACC-004, ACC-008) ---

    /** ACC-004: musterinin mevcut adreslerinden birini secer. */
    public BillingAccountModalComponent selectExistingAddress(String addressId) {
        selectByValue(Field.SERVICE_ADDRESS.locator(), addressId);
        return this;
    }

    /** Select'teki ilk gercek adres secenegini secer (placeholder haric). */
    public BillingAccountModalComponent selectFirstExistingAddress() {
        String value = getAttribute(
                By.cssSelector("#account-address option:not([disabled])"), "value");
        return selectExistingAddress(value);
    }

    /** ACC-008: secili adresin select'te gorunen metni. */
    public String selectedAddressText() {
        return getText(By.cssSelector("#account-address option:checked"));
    }

    public boolean hasServiceAddressField() {
        return isDisplayed(Field.SERVICE_ADDRESS.locator());
    }

    /** "+ Add New Address" / "‹ Use Existing Address" baglantisi — iki mod arasinda gecis. */
    public BillingAccountModalComponent toggleAddressMode() {
        click(TOGGLE_ADDRESS_MODE);
        return this;
    }

    /** ACC-005: yeni adres alanlarinin ekranda bulunmasi. */
    public boolean hasNewAddressFields() {
        return isDisplayed(Field.NEW_CITY.locator())
                && isDisplayed(Field.NEW_STREET.locator())
                && isDisplayed(Field.NEW_HOUSE_NUMBER.locator())
                && isDisplayed(Field.NEW_DESCRIPTION.locator());
    }

    /** Sehri ekranda gorunen etikete gore secer (secenek degerleri lookup id'sidir, kayabilir). */
    public BillingAccountModalComponent selectNewAddressCity(String cityLabel) {
        selectByVisibleText(Field.NEW_CITY.locator(), cityLabel);
        return this;
    }

    public BillingAccountModalComponent enterNewStreet(String street) {
        type(Field.NEW_STREET.locator(), street);
        return this;
    }

    public BillingAccountModalComponent enterNewHouseNumber(String houseNumber) {
        type(Field.NEW_HOUSE_NUMBER.locator(), houseNumber);
        return this;
    }

    public BillingAccountModalComponent enterNewAddressDescription(String description) {
        type(Field.NEW_DESCRIPTION.locator(), description);
        return this;
    }

    /** Yeni adres formunu tumuyle doldurur (City varsayilan Ankara). */
    public BillingAccountModalComponent fillNewAddress(String street, String houseNumber, String description) {
        return selectNewAddressCity(CITY_ANKARA_LABEL)
                .enterNewStreet(street)
                .enterNewHouseNumber(houseNumber)
                .enterNewAddressDescription(description);
    }

    // --- Durum ---

    /** ACC-009: zorunlu alanlar eksikken "Create" pasif olmalidir. */
    public boolean isCreateDisabled() {
        return remainsDisabled(CREATE_BUTTON);
    }

    public boolean isCreateEnabled() {
        return becomesEnabled(CREATE_BUTTON);
    }

    public String title() {
        return getText(TITLE);
    }

    /**
     * Eylem butonlari goruntulenen alanin icinde mi.
     *
     * <p>{@code .modal-backdrop} {@code position: fixed} ve kaydirilamaz,
     * {@code .modal-card} icinse {@code max-height}/{@code overflow} tanimli degil
     * (bkz. {@code styles.scss}). Modal viewport'tan uzun oldugunda alt kismi tasar ve
     * butonlara <b>hicbir sekilde</b> ulasilamaz — kaydirma da ise yaramaz.
     */
    public boolean areActionsInViewport() {
        WebElement createButton = wait.until(ExpectedConditions.presenceOfElementLocated(CREATE_BUTTON));
        return JsUtil.isInViewport(driver, createButton);
    }

    public boolean hasSaveError() {
        return isDisplayedAfterWait(SAVE_ERROR);
    }

    public String saveErrorText() {
        return getText(SAVE_ERROR);
    }

    public boolean hasFieldError(Field field) {
        return hasFieldError(field.id());
    }

    public String fieldError(Field field) {
        return fieldErrorText(field.id());
    }

    /**
     * Alandan odagi kaldirir; hata mesajlari {@code invalid() && touched()} sartina bagli
     * oldugu icin yazdiktan sonra dogrulama yapan testler bunu cagirmalidir.
     */
    public BillingAccountModalComponent blurField(Field field) {
        blur(field.locator());
        return this;
    }

    // --- Eylemler ---

    /** ACC-011: "Create" butonu. */
    public void create() {
        clickAction(CREATE_BUTTON);
    }

    public void cancel() {
        clickAction(CANCEL_BUTTON);
    }

    /**
     * Modal'in alt eylem butonlarina tiklar.
     *
     * <p><b>Neden ayri bir yol:</b> yeni adres modunda modal icerigi uzuyor ve
     * {@code .modal-actions} goruntulenen alanin ALTINDA kaliyor (modal kendi icinde
     * kayan bir kap; sayfa kaydirmasi butonu gorunur yapmiyor). {@link #click} once
     * "tiklanabilir" olmayi bekleyip <i>sonra</i> kaydirdigi icin bu durumda zaman
     * asimina ugruyor. Burada sira tersine cevrilir: once elemana kaydirilir, sonra
     * tiklanabilirlik beklenir.
     *
     * <p>Pasif buton bilincli olarak JS ile zorlanmaz — aksi halde "Create pasif olmali"
     * senaryolari sessizce yanlis gecerdi.
     */
    private void clickAction(By locator) {
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        JsUtil.scrollIntoView(driver, button);
        click(locator);
    }

    /**
     * ACC-010: Cancel'a basar ve gosterilmesi <b>beklenen</b> uyari diyalogunu dondurur.
     *
     * <p>Dokuman Cancel icin bir onay adimi sart kosar; uygulamada
     * {@code closeCreateAccountModal()} modal'i dogrudan kapatir.
     */
    public ConfirmDialogComponent cancelExpectingConfirmation() {
        cancel();
        return new ConfirmDialogComponent(driver);
    }

    public void closeWithX() {
        click(CLOSE_BUTTON);
    }

    public void waitUntilClosed() {
        wait.until(AppConditions.absentFromDom(ROOT));
    }

    public boolean isOpen() {
        return isDisplayed(ROOT);
    }
}
