package com.crmlite.ui.pages.customer.create;

import com.crmlite.ui.pages.BasePage;
import com.crmlite.ui.pages.components.AddressModalComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * FR-003 2. adim — Adres bilgileri.
 *
 * <p>Sihirbazdaki adres listesi, musteri detayindaki listeden farklidir:
 * burada yalnizca <b>Remove</b> islemi vardir (Set as Primary / Edit yoktur) ve
 * <b>ilk adres</b> ({@code $index === 0}) otomatik birincil sayilir.
 */
public class AddressStep extends BasePage {

    public static final int MAX_ADDRESSES = 5;

    private static final By PANEL = By.cssSelector("section.address-panel");
    private static final By ADDRESS_COUNT = By.cssSelector(".address-panel .address-count");
    private static final By ADD_ADDRESS_BUTTON = By.cssSelector(".address-panel .add-address-button");
    private static final By LIMIT_MESSAGE = By.cssSelector(".address-panel .address-limit-message");
    private static final By EMPTY_STATE = By.cssSelector(".address-panel .address-empty-state");
    private static final By EMPTY_TITLE = By.cssSelector(".address-panel .address-empty-title");
    private static final By ADDRESS_TILES = By.cssSelector(".address-list > .address-tile");

    public AddressStep(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By pageReadyLocator() {
        return PANEL;
    }

    @Override
    protected String pageName() {
        return "Create Customer - Adres adimi";
    }

    // --- Liste durumu ---

    public int addressCount() {
        return findAll(ADDRESS_TILES).size();
    }

    /** "My Addresses (2/5)" benzeri baslik. */
    public String addressCountLabel() {
        return getText(ADDRESS_COUNT);
    }

    /**
     * ACC-011: hic adres yokken bos durum gosterilir.
     *
     * <p>Beklemeli kontrol ZORUNLU: demografik adimdan gecis artik asenkron (Next butonu
     * dogrulama sirasinda spinner gosteriyor), bu yuzden anlik {@code isDisplayed} panel
     * henuz render edilmeden false doner.
     */
    public boolean isEmptyStateDisplayed() {
        return isDisplayedAfterWait(EMPTY_STATE);
    }

    public String emptyStateTitle() {
        return getText(EMPTY_TITLE);
    }

    // --- Adres ekleme (ACC-009, ACC-010) ---

    public AddressModalComponent openAddAddressModal() {
        click(ADD_ADDRESS_BUTTON);
        AddressModalComponent modal =
                new AddressModalComponent(driver, AddressModalComponent.Variant.CREATE_WIZARD);
        modal.waitUntilLoaded();
        return modal;
    }

    /** Tek cagriyla adres ekler: modal'i acar, doldurur, kaydeder ve kapanmasini bekler. */
    public AddressStep addAddress(String street, String houseNumber, String description) {
        AddressModalComponent modal = openAddAddressModal();
        modal.fill(street, houseNumber, description);
        modal.save();
        modal.waitUntilClosed();
        return this;
    }

    /** ACC-010: 5 adres eklendiginde "Add address" butonu pasiflesir. */
    public boolean isAddAddressDisabled() {
        return remainsDisabled(ADD_ADDRESS_BUTTON);
    }

    public boolean isAddAddressEnabled() {
        return becomesEnabled(ADD_ADDRESS_BUTTON);
    }

    /** ACC-010: "You can add up to 5 addresses." mesaji. */
    public boolean hasLimitMessage() {
        return isDisplayedAfterWait(LIMIT_MESSAGE);
    }

    public String limitMessage() {
        return getText(LIMIT_MESSAGE);
    }

    // --- Kart icerigi ---

    public String cityNameAt(int index) {
        return getText(tileChild(index, ".address-card-city"));
    }

    public String addressLineAt(int index) {
        return getText(tileChild(index, ".address-card-line"));
    }

    public String tagTextAt(int index) {
        return getText(tileChild(index, ".address-card-tag"));
    }

    /** Sihirbazda birincil adres, listenin ilk elemanidir. */
    public boolean isPrimaryAt(int index) {
        return isDisplayed(tileChild(index, ".address-card.address-card-primary"));
    }

    /** Karti listeden cikarir (sihirbazdaki tek menu islemi). */
    public AddressStep removeAddressAt(int index) {
        click(tileChild(index, ".menu-trigger"));
        click(tileChild(index, ".menu-dropdown .menu-item-danger"));
        return this;
    }

    private By tileChild(int index, String cssSuffix) {
        return By.cssSelector(String.format(
                ".address-list > .address-tile:nth-child(%d) %s", index + 1, cssSuffix));
    }
}
