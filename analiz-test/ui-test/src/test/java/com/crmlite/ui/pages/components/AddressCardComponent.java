package com.crmlite.ui.pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * FR-005: musteri detayindaki tek bir adres karti ve uc-nokta menusu.
 *
 * <p>Kart, {@code WebElement} yerine <b>indeksten uretilen locator</b> ile temsil edilir;
 * Angular listeyi yeniden render ettiginde tutulan bir referans stale olurdu.
 *
 * <p>Menu icindeki butonlarin sinifi yok (yalnizca "Delete" {@code menu-item-danger});
 * bu yuzden "Set as Primary" ve "Edit" butonlari <b>sira</b> ile secilir:
 * 1) Set as Primary, 2) Edit, 3) Delete.
 */
public class AddressCardComponent extends BaseComponent {

    private final int index;

    public AddressCardComponent(WebDriver driver, int index) {
        super(driver, rootLocator(index));
        this.index = index;
    }

    private static By rootLocator(int index) {
        return By.cssSelector(String.format(".address-list > .address-tile:nth-child(%d)", index + 1));
    }

    @Override
    protected String pageName() {
        return "Adres Karti #" + index;
    }

    // --- Icerik ---

    public String cityName() {
        return getText(child(".address-card-city"));
    }

    /** "{street} {houseNo}" satiri. */
    public String addressLine() {
        return getText(child(".address-card-line"));
    }

    /** Aciklama etiketi; birincil adreste " · Primary" eki bulunur. */
    public String tagText() {
        return getText(child(".address-card-tag"));
    }

    /** ACC-006/007: kart birincil adres olarak isaretli mi. */
    public boolean isPrimary() {
        return isDisplayed(child(".address-card.address-card-primary"));
    }

    /** ACC-011: karta bagli fatura hesabi bilgisi gosteriliyor mu. */
    public boolean hasLinkedAccount() {
        return isDisplayed(child(".address-linked-line"));
    }

    public String linkedAccountText() {
        return getText(child(".address-linked-line"));
    }

    // --- Uc-nokta menusu ---

    public AddressCardComponent openMenu() {
        if (!isDisplayed(child(".menu-dropdown"))) {
            click(child(".menu-trigger"));
        }
        return this;
    }

    public boolean isMenuOpen() {
        return isDisplayed(child(".menu-dropdown"));
    }

    /**
     * ACC-006: adresi birincil yapar.
     * ACC-007: tek adres varsa (zaten birincil) bu buton pasiftir.
     *
     * <p><b>Bu metot islemin sunucuda yerlestigini GARANTI ETMEZ.</b> Eski adresin
     * birincil bayragi aninda kalkmiyor; bu araliktaki bir sayfa yuklemesi iki karti
     * da "Primary" gosterir. Sonucu okuyacak testler once
     * {@link com.crmlite.ui.data.api.AddressApi#awaitSinglePrimary(long)} ile
     * senkronize olmalidir.
     *
     * <p>Burada {@code noPendingHttpRequests()} denendi ve <b>yetmedi</b>: kosul,
     * PUT istegi daha baslamadan "bekleyen istek yok" diyip aninda gecebiliyor.
     * Ise yaramayan bir bekleme, olmayan bekleme'den daha kotudur — kaldirildi.
     */
    public void setAsPrimary() {
        openMenu();
        click(menuButton(1));
    }

    public boolean isSetAsPrimaryEnabled() {
        openMenu();
        return isEnabled(menuButton(1));
    }

    /** ACC-013: adres duzenleme modal'ini acar. */
    public void edit() {
        openMenu();
        click(menuButton(2));
    }

    /** ACC-008/010: silme onay diyalogunu acar. */
    public void delete() {
        openMenu();
        click(child(".menu-dropdown .menu-item-danger"));
    }

    /**
     * ACC-009/011: birincil ya da faturaya bagli adreste Delete pasif olmalidir.
     */
    public boolean isDeleteEnabled() {
        openMenu();
        return isEnabled(child(".menu-dropdown .menu-item-danger"));
    }

    /**
     * Delete butonunun {@code title} ipucu.
     * ACC-009 -> "Primary address cannot be deleted."
     * ACC-011 -> "This address is linked to a billing account and cannot be deleted."
     */
    public String deleteTooltip() {
        openMenu();
        return getAttribute(child(".menu-dropdown .menu-item-danger"), "title");
    }

    private By menuButton(int position) {
        return By.cssSelector(String.format(
                ".address-list > .address-tile:nth-child(%d) .menu-dropdown button:nth-of-type(%d)",
                index + 1, position));
    }

    private By child(String cssSuffix) {
        return By.cssSelector(String.format(
                ".address-list > .address-tile:nth-child(%d) %s", index + 1, cssSuffix));
    }
}
