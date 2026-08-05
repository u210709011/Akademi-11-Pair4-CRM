package com.crmlite.ui.pages.customer;

import com.crmlite.ui.pages.BasePage;
import com.crmlite.ui.pages.components.AddressCardComponent;
import com.crmlite.ui.pages.components.AddressModalComponent;
import com.crmlite.ui.pages.components.ConfirmDialogComponent;
import com.crmlite.ui.pages.components.ContactModalComponent;
import com.crmlite.ui.pages.components.NavbarComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Customer Info ekrani — FR-004'un giris noktasi ve FR-005'in (Adres Yonetimi) tamami.
 * Rota: {@code /detail-customer/{custId}}
 *
 * <p>Dort sekme vardir: Information, Accounts, Address, Contact.
 */
public class CustomerDetailPage extends BasePage {

    public static final int MAX_ADDRESSES = 5;

    private static final By ROOT = By.cssSelector(".customer-detail-panel");
    private static final By TABS = By.cssSelector(".detail-tabs .detail-tab");
    private static final By ACTIVE_TAB = By.cssSelector(".detail-tabs .detail-tab.active");

    // Information sekmesi
    private static final By EDIT_BUTTON = By.cssSelector(".info-panel-actions .icon-button:not(.icon-button-danger)");
    private static final By DELETE_CUSTOMER_BUTTON = By.cssSelector(".info-panel-actions .icon-button-danger");
    private static final By INFO_ROWS = By.cssSelector(".info-grid .info-row");

    // Ozet kutulari.
    // Ozet seridinde bes ayni yapida kutu var (Customer ID, Accounts, Status, Addresses,
    // Primary City). Yalnizca "summary-stat + stat-value" ile eslesen bir locator
    // ILK kutuyu, yani musteri kodunu ("CUST-77") secer. Bu yuzden etiket metnine
    // sabitlenir. UI dili 'en' olarak pinlendigi icin metin deterministiktir.
    private static final By ADDRESS_COUNT_STAT = By.xpath(
            "//div[contains(@class,'summary-stat')]"
                    + "[normalize-space(.//span[contains(@class,'stat-label')])='Addresses']"
                    + "//span[contains(@class,'stat-value')]");

    // Address sekmesi
    private static final By ADD_ADDRESS_BUTTON = By.cssSelector(".info-panel-actions .add-address-button");
    private static final By ADDRESS_TILES = By.cssSelector(".address-list > .address-tile");
    private static final By ADDRESS_ACTION_ERROR = By.cssSelector(".address-save-error");
    private static final By ADDRESS_EMPTY_STATE = By.cssSelector(".address-empty-state");

    // Accounts sekmesi
    private static final By ACCOUNT_ROWS = By.cssSelector("table.accounts-table tbody tr");

    // Contact Medium sekmesi (FR-006)
    private static final By CONTACT_GRID = By.cssSelector(".contact-info-grid");
    private static final By CONTACT_ITEMS = By.cssSelector(".contact-info-grid .contact-info-item");
    private static final By EDIT_CONTACT_BUTTON = By.cssSelector(".info-panel-actions .icon-button");
    private static final By SUCCESS_TOAST = By.cssSelector(".success-toast");
    private static final By SUCCESS_TOAST_MESSAGE = By.cssSelector(".success-toast .success-toast-message");

    /** Sekmeler, render sirasina gore. */
    public enum Tab {
        INFORMATION(0),
        ACCOUNTS(1),
        ADDRESS(2),
        CONTACT(3);

        private final int index;

        Tab(int index) {
            this.index = index;
        }

        public int index() {
            return index;
        }
    }

    /** Information sekmesindeki alanlar, {@code .info-grid} icindeki sira ile. */
    public enum InfoField {
        FIRST_NAME(0),
        MIDDLE_NAME(1),
        LAST_NAME(2),
        BIRTH_DATE(3),
        GENDER(4),
        FATHER_NAME(5),
        MOTHER_NAME(6),
        NATIONAL_ID(7);

        private final int index;

        InfoField(int index) {
            this.index = index;
        }

        public int index() {
            return index;
        }
    }

    public CustomerDetailPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By pageReadyLocator() {
        return ROOT;
    }

    @Override
    protected String pageName() {
        return "Customer Info";
    }

    public NavbarComponent navbar() {
        return new NavbarComponent(driver);
    }

    // --- Sekmeler ---

    public CustomerDetailPage selectTab(Tab tab) {
        click(By.cssSelector(String.format(".detail-tabs .detail-tab:nth-of-type(%d)", tab.index() + 1)));
        return this;
    }

    public String activeTabLabel() {
        return getText(ACTIVE_TAB);
    }

    public int tabCount() {
        return findAll(TABS).size();
    }

    /** URL'den musteri numarasini okur ({@code /detail-customer/{custId}}). */
    public String customerId() {
        String url = currentUrl();
        String path = url.replaceAll("[?#].*$", "");
        String[] segments = path.split("/");
        for (int i = 0; i < segments.length - 1; i++) {
            if ("detail-customer".equals(segments[i])) {
                return segments[i + 1];
            }
        }
        return null;
    }

    // --- Information sekmesi (FR-004 girisi) ---

    /** {@code .info-grid} icindeki bir alanin degeri. */
    public String infoValue(InfoField field) {
        return getText(By.cssSelector(String.format(
                ".info-grid .info-row:nth-child(%d) .info-value", field.index() + 1)));
    }

    public int infoRowCount() {
        return findAll(INFO_ROWS).size();
    }

    /** FR-004 ACC-001: kalem ikonu → Customer Info Update ekrani. */
    public UpdateCustomerPage clickEdit() {
        selectTab(Tab.INFORMATION);
        click(EDIT_BUTTON);
        UpdateCustomerPage page = new UpdateCustomerPage(driver);
        page.waitUntilLoaded();
        return page;
    }

    /** Musteri silme onay diyalogunu acar (FR-007 kapsami; burada yalnizca erisim saglanir). */
    public ConfirmDialogComponent clickDeleteCustomer() {
        selectTab(Tab.INFORMATION);
        click(DELETE_CUSTOMER_BUTTON);
        return new ConfirmDialogComponent(driver);
    }

    // --- Accounts sekmesi (FR-003 ACC-017 kismi dogrulama) ---

    public int accountRowCount() {
        selectTab(Tab.ACCOUNTS);
        return findAll(ACCOUNT_ROWS).size();
    }

    /** Hesap tablosu kolonlari: Number, Name, Type, Status. */
    public String accountCell(int rowIndex, int columnIndex) {
        selectTab(Tab.ACCOUNTS);
        return getText(By.cssSelector(String.format(
                "table.accounts-table tbody tr:nth-child(%d) td:nth-child(%d)",
                rowIndex + 1, columnIndex + 1)));
    }

    // --- Address sekmesi (FR-005) ---

    /**
     * ACC-001: Address tabini acar ve <b>listenin render edilmesini bekler</b>.
     *
     * <p>Bekleme burada olmali: {@link #addressCount()} beklemeyen bir sayim yapar
     * (bilincli — "adres yok" durumunu da dogru raporlamalidir). Sekmeye tiklandiktan
     * hemen sonra sayilirsa Angular henuz render etmemis olur ve sayac <b>0</b> doner;
     * bu, "silmeden onceki adet" gibi olcumleri sessizce bozar.
     *
     * <p>Hazir olma kosulu: <b>en az bir adres karti</b> ya da <b>bos liste mesaji</b>.
     * Ikisinden biri gorunene kadar beklenir; boylece adres sayisi 0 olan musteriler
     * gereksiz yere zaman asimina ugramaz.
     */
    public CustomerDetailPage openAddressTab() {
        selectTab(Tab.ADDRESS);
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(ADDRESS_TILES),
                ExpectedConditions.visibilityOfElementLocated(ADDRESS_EMPTY_STATE)));
        return this;
    }

    /**
     * Listelenen adres sayisi (anlik, beklemez).
     *
     * <p>Beklemeyi {@link #openAddressTab()} yapar; burada beklenirse "adres yok"
     * senaryolari her seferinde zaman asimi suresi kadar yavaslar.
     */
    public int addressCount() {
        return findAll(ADDRESS_TILES).size();
    }

    public AddressCardComponent addressCard(int index) {
        return new AddressCardComponent(driver, index);
    }

    public List<AddressCardComponent> addressCards() {
        List<AddressCardComponent> cards = new ArrayList<>();
        for (int i = 0; i < addressCount(); i++) {
            cards.add(new AddressCardComponent(driver, i));
        }
        return cards;
    }

    /** Birincil adresin indeksi; yoksa -1. */
    public int primaryAddressIndex() {
        for (int i = 0; i < addressCount(); i++) {
            if (addressCard(i).isPrimary()) {
                return i;
            }
        }
        return -1;
    }

    public boolean isAddressListEmpty() {
        return isDisplayed(ADDRESS_EMPTY_STATE);
    }

    /** Ozet kutusundaki "n / 5" adres sayaci. */
    public String addressCountStat() {
        return getText(ADDRESS_COUNT_STAT);
    }

    // --- Adres ekleme (ACC-002, ACC-004, ACC-005) ---

    public AddressModalComponent openAddAddressModal() {
        click(ADD_ADDRESS_BUTTON);
        AddressModalComponent modal =
                new AddressModalComponent(driver, AddressModalComponent.Variant.CUSTOMER_DETAIL);
        modal.waitUntilLoaded();
        return modal;
    }

    /** Tek cagriyla adres ekler ve modal kapanana kadar bekler. */
    public CustomerDetailPage addAddress(String street, String houseNumber, String description) {
        int before = addressCount();
        AddressModalComponent modal = openAddAddressModal();
        modal.fill(street, houseNumber, description);
        modal.save();
        modal.waitUntilClosed();
        // Modal'in kapanmasi kaydin listeye yansidigi anlamina gelmez: liste backend
        // round-trip'inden sonra asenkron yenileniyor. Kart sayisi artana kadar
        // beklenmezse hemen ardindan yapilan addressCount() bayat degeri okur.
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(ADDRESS_TILES, before));
        return this;
    }

    /** ACC-005: 5 adres varken "Add New Address" pasiflesir. */
    public boolean isAddAddressDisabled() {
        return remainsDisabled(ADD_ADDRESS_BUTTON);
    }

    public boolean isAddAddressEnabled() {
        return becomesEnabled(ADD_ADDRESS_BUTTON);
    }

    // --- Adres duzenleme / silme (ACC-008..ACC-015) ---

    /** ACC-013: secili adresin duzenleme modal'ini acar. */
    public AddressModalComponent editAddress(int index) {
        addressCard(index).edit();
        AddressModalComponent modal =
                new AddressModalComponent(driver, AddressModalComponent.Variant.CUSTOMER_DETAIL);
        modal.waitUntilLoaded();
        return modal;
    }

    /** ACC-008/010: silme onay diyalogunu acar. */
    public ConfirmDialogComponent deleteAddress(int index) {
        addressCard(index).delete();
        return new ConfirmDialogComponent(driver);
    }

    /** ACC-011: silme reddedildiginde liste ustunde gosterilen hata. */
    public boolean hasAddressActionError() {
        return isDisplayedAfterWait(ADDRESS_ACTION_ERROR);
    }

    public String addressActionError() {
        return getText(ADDRESS_ACTION_ERROR);
    }

    // --- Contact Medium sekmesi (FR-006) ---

    /**
     * Contact sekmesindeki alanlar, {@code .contact-info-grid} icindeki render sirasi ile.
     *
     * <p>Adres sekmesinden farkli olarak burada {@code .info-grid/.info-row} degil
     * {@code .contact-info-grid/.contact-info-item} kullaniliyor; locator'lar buna gore.
     */
    public enum ContactField {
        EMAIL(0),
        MOBILE_PHONE(1),
        HOME_PHONE(2),
        FAX(3);

        private final int index;

        ContactField(int index) {
            this.index = index;
        }

        public int index() {
            return index;
        }
    }

    /**
     * ACC-001: Contact Medium sekmesini acar ve bilgilerin render edilmesini bekler.
     *
     * <p>Bekleme burada olmali: {@link #contactValue} beklemeyen bir okuma yapar ve
     * sekmeye tiklandiktan hemen sonra cagirilirsa Angular henuz render etmemis olur.
     */
    public CustomerDetailPage openContactTab() {
        selectTab(Tab.CONTACT);
        wait.until(ExpectedConditions.visibilityOfElementLocated(CONTACT_GRID));
        return this;
    }

    /** ACC-001: sekmede gosterilen iletisim bilgisi degeri. */
    public String contactValue(ContactField field) {
        return getText(By.cssSelector(String.format(
                ".contact-info-grid .contact-info-item:nth-child(%d) .contact-info-value",
                field.index() + 1)));
    }

    public int contactRowCount() {
        return findAll(CONTACT_ITEMS).size();
    }

    /** ACC-002: kalem ikonu → "Edit Contact Information" modal'i. */
    public ContactModalComponent openEditContactModal() {
        click(EDIT_CONTACT_BUTTON);
        ContactModalComponent modal = new ContactModalComponent(driver);
        modal.waitUntilLoaded();
        return modal;
    }

    /** ACC-007: kaydetme sonrasi gosterilen basari bildirimi (toast). */
    public boolean hasSuccessToast() {
        return isDisplayedAfterWait(SUCCESS_TOAST);
    }

    public String successToastText() {
        return getText(SUCCESS_TOAST_MESSAGE);
    }
}
