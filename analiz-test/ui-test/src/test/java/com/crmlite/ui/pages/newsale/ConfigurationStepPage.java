package com.crmlite.ui.pages.newsale;

import com.crmlite.ui.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

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

    /** Sonsuz donguye karsi ust sinir; sepette en fazla birkac urun ve alan olur. */
    private static final int MAX_FIELD_FILLS = 60;

    /** Konfigurasyonun tamamlanmasi icin verilen gecis sayisi. */
    private static final int CONFIG_COMPLETION_ATTEMPTS = 4;

    /** Sihirbaz cubugundaki ileri butonu — konfigurasyonun tamamlandiginin gostergesi. */
    private static final By WIZARD_NEXT_BUTTON = By.cssSelector(".wizard-actions .next-button");

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
    private static final By MODAL_SAVE_BUTTON = By.cssSelector(".modal-actions .app-button-primary");
    private static final By MODAL_CANCEL_BUTTON = By.cssSelector(".modal-actions .app-button-secondary");
    private static final By MODAL_CARD = By.cssSelector(".modal-card");

    // --- Vazgecme onayi (ACC-005) ---
    private static final By DISCARD_MESSAGE = By.cssSelector(".delete-confirm-message");
    private static final By DISCARD_CONFIRM_BUTTON = By.cssSelector(".modal-actions .app-button-danger");

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

    /**
     * Tum karakteristik alanlarini doldurur.
     *
     * <p>Next butonu {@code isConfigurationComplete} ile korunur: servis adresi SECILMIS ve
     * zorunlu karakteristiklerin TAMAMI doldurulmus olmalidir. Bu metot olmadan Review
     * adimina hic gecilemez - FR-016/FR-017 testleri bu yuzden topluca atlaniyordu.
     *
     * <p>Select alanlarinda ilk BOS OLMAYAN secenek secilir (ilk secenek yer tutucudur);
     * metin alanlarina sabit bir deger yazilir.
     */
    /**
     * Konfigurasyonu Next aktiflesene kadar tamamlar: servis adresini secer, alanlari
     * doldurur ve gerekirse TEKRARLAR.
     *
     * <p>Tek gecis yetmiyor: karakteristik semasi urun basina asenkron yuklenir, dolayisiyla
     * ilk gecisten sonra yeni alanlar belirebilir. Sema beklemesi tek basina da yeterli
     * olmadi - fr016 iki turluk kararlilik kontrolunun ikinci turunda yine dustu.
     *
     * <p>Bu bir hata gizleme DEGILDIR: konfigurasyon gercekten tamamlanamiyorsa Next pasif
     * kalir ve cagiran testteki assert acikca patlar. Burada yalnizca kurulumun bitmesine
     * sans taniniyor.
     */
    public ConfigurationStepPage completeConfiguration() {
        for (int attempt = 0; attempt < CONFIG_COMPLETION_ATTEMPTS; attempt++) {
            if (!hasSelectedAddress()) {
                openChangeAddressModal();
                selectAddressOption(0);
            }
            fillAllConfigurationFields();

            if (isEnabled(WIZARD_NEXT_BUTTON)) {
                return this;
            }
            sleepBriefly();
        }
        return this;
    }

    private void sleepBriefly() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public ConfigurationStepPage fillAllConfigurationFields() {
        // Alanlar TEK TEK ve her seferinde YENIDEN SORGULANARAK doldurulur. Bir urunun
        // zorunlu alanlari tamamlandiginda kart blur'da otomatik KAPANIR
        // (checkAutoCollapse) ve alanlari DOM'dan silinir; onceden toplanmis bir liste
        // uzerinde donmek StaleElementReferenceException uretir.
        waitForConfigurationSchema();

        int guard = 0;
        while (guard++ < MAX_FIELD_FILLS && fillFirstEmptyField()) {
            // devam
        }
        return this;
    }

    /**
     * Her urun kartinin karakteristik SEMASI cozulene kadar bekler.
     *
     * <p>Sema urun basina ASENKRON yuklenir. Yuklenmeden once kartta ne alan izgarasi ne
     * de bilgilendirme notu vardir; doldurucu ortada alan bulamayip sessizce pes eder,
     * sema sonradan gelince zorunlu alanlar bos kalir ve Next hic aktiflesmez.
     *
     * <p>Uygulamanin kendi kurali da bunu soyler: {@code isConfigured()} sema yuklenmemisse
     * {@code false} doner ("sema henuz yuklenmedi").
     *
     * <p>Bir kartin cozulmus sayilmasi icin ya alanlarinin ya da notunun gorunmesi yeterlidir;
     * karakteristigi olmayan urunler not gosterir ve bu bir hata degildir.
     */
    private void waitForConfigurationSchema() {
        try {
            wait.until(driver -> {
                int cards = findAll(PRODUCT_CARDS).size();
                return cards > 0
                        && findAll(CONFIG_FIELDS).size() + findAll(PENDING_NOTES).size() >= cards;
            });
        } catch (org.openqa.selenium.TimeoutException e) {
            // Cozulmedi; doldurucu elindekiyle devam eder ve eksik kalan alan
            // cagiran taraftaki Next assert'inde acikca ortaya cikar.
        }
    }

    /** Ilk bos alani doldurur; dolduracak alan kalmadiysa {@code false} doner. */
    private boolean fillFirstEmptyField() {
        try {
            for (WebElement field : findAll(CONFIG_FIELDS)) {
                List<WebElement> selects = field.findElements(By.tagName("select"));
                if (!selects.isEmpty()) {
                    Select select = new Select(selects.get(0));
                    String current = select.getFirstSelectedOption().getDomProperty("value");
                    if (current != null && !current.isBlank()) {
                        continue;
                    }
                    // Secenekler de asenkron gelebilir; yer tutucu disinda secenek yoksa
                    // ATLAMAK yerine kisa bir sure beklenir. Atlamak, alani kalici olarak
                    // bos birakip Next'i hic aktiflestirmemeye yol aciyordu.
                    WebElement selectElement = selects.get(0);
                    wait.until(driver -> new Select(selectElement).getOptions().stream()
                            .anyMatch(o -> {
                                String v = o.getDomProperty("value");
                                return v != null && !v.isBlank();
                            }));
                    for (WebElement option : new Select(selectElement).getOptions()) {
                        String value = option.getDomProperty("value");
                        if (value != null && !value.isBlank()) {
                            new Select(selectElement).selectByValue(value);
                            return true;
                        }
                    }
                    continue;
                }
                List<WebElement> inputs = field.findElements(By.tagName("input"));
                if (!inputs.isEmpty()) {
                    WebElement input = inputs.get(0);
                    String value = input.getDomProperty("value");
                    if (value == null || value.isBlank()) {
                        input.sendKeys("Test");
                        return true;
                    }
                }
            }
            return false;
        } catch (StaleElementReferenceException e) {
            // Kart tam bu sirada kapandi; bir sonraki turda guncel liste ile devam edilir.
            return true;
        }
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
