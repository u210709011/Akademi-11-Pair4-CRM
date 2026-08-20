package com.crmlite.ui.pages.customer;

import com.crmlite.ui.pages.BasePage;
import com.crmlite.ui.pages.components.AddressCardComponent;
import com.crmlite.ui.pages.components.AddressModalComponent;
import com.crmlite.ui.pages.components.BillingAccountModalComponent;
import com.crmlite.ui.pages.components.ProductDetailModalComponent;
import com.crmlite.ui.pages.newsale.OfferSelectionPage;
import com.crmlite.ui.pages.components.ConfirmDialogComponent;
import com.crmlite.ui.pages.components.ContactModalComponent;
import com.crmlite.ui.pages.components.NavbarComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
    // Dogrudan cocuk (>) sart: satir genisletildiginde icine URUN TABLOSU render ediliyor ve
    // torun secici onun satirlarini da sayardi. :not(.account-detail-row) ise genisletilmis
    // satirin kendisini disarida birakir - o bir hesap degil, acilan panelin kabidir.
    private static final By ACCOUNT_ROWS =
            By.cssSelector("table.accounts-table > tbody > tr:not(.account-detail-row)");
    private static final By ACCOUNTS_TABLE = By.cssSelector("table.accounts-table");
    // Hesap yoksa tablo hic render edilmez, yerine bu metin gosterilir (FR-009 ACC-001).
    private static final By ACCOUNTS_PANEL = By.cssSelector(".accounts-table-wrapper");
    private static final By ACCOUNT_HEADERS = By.cssSelector("table.accounts-table thead th");
    // Accounts sekmesindeki "+ Create New Account" butonu; adres sekmesindeki ekleme
    // butonuyla ayni sinifi paylasir, sekme bazinda ayrisir.
    private static final By CREATE_ACCOUNT_BUTTON = By.cssSelector(".info-panel-actions .add-address-button");

    // FR-011: "silinemez" diyalogu. Silme onay diyaloguyla ayni .delete-confirm-card sinifini
    // paylasir; ayirt edici isaret uyari ikonu ve OK butonudur (.app-button-primary, variant
    // verilmemis - varsayilan primary) — Close butonu ayni diyalogda .app-button-secondary'dir,
    // onay diyalogunda ise .app-button-danger vardir, .app-button-primary yoktur.
    private static final By CANNOT_DELETE_DIALOG =
            By.cssSelector(".modal-card.delete-confirm-card .delete-confirm-icon-warning");
    private static final By CANNOT_DELETE_MESSAGE =
            By.cssSelector(".modal-card.delete-confirm-card .delete-confirm-message");
    private static final By CANNOT_DELETE_CLOSE =
            By.cssSelector(".modal-card.delete-confirm-card .modal-actions .app-button-primary");

    // FR-009: genisletilmis hesap satirindaki urun tablosu ve sayfalama.
    private static final By PRODUCTS_TABLE = By.cssSelector("table.products-table");
    private static final By PRODUCT_HEADERS = By.cssSelector("table.products-table thead th");
    private static final By NO_PRODUCTS_MESSAGE = By.cssSelector(".account-no-products");
    private static final By START_NEW_SALE_BUTTON = By.cssSelector(".start-new-sale-button");
    private static final By ACCOUNTS_PAGINATION = By.cssSelector(".accounts-table-wrapper .pagination");
    private static final By ACCOUNTS_PAGINATION_RANGE =
            By.cssSelector(".accounts-table-wrapper .pagination .pagination-range");

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

    /**
     * Silme onaylandiktan sonra listenin gercekten kisalmasini bekler.
     *
     * <p>{@link #addAddress} ile ayni tuzak: onay diyaloginin kapanmasi kaydin listeden
     * dustugu anlamina gelmez, liste backend round-trip'inden sonra asenkron yenileniyor.
     * Beklemeden yapilan {@link #addressCount()} bayat degeri okur.
     */
    public CustomerDetailPage waitUntilAddressCountIsLessThan(int previousCount) {
        wait.until(ExpectedConditions.numberOfElementsToBeLessThan(ADDRESS_TILES, previousCount));
        return this;
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

    // --- Accounts sekmesi / Fatura hesabi olusturma (FR-008) ---

    /** Accounts tablosunun kolonlari, render sirasi ile (ilk kolon genisletme oku). */
    public enum AccountColumn {
        EXPAND(0),
        STATUS(1),
        NUMBER(2),
        NAME(3),
        TYPE(4),
        ACTION(5);

        private final int index;

        AccountColumn(int index) {
            this.index = index;
        }

        public int index() {
            return index;
        }
    }

    /**
     * ACC-001: Accounts (Customer Account) sekmesini acar ve tablonun render edilmesini bekler.
     *
     * <p>Hazir olma kosulu tablonun kendisidir; onboarding her musteriye varsayilan bir hesap
     * actigi icin tablo hicbir zaman bos degildir.
     */
    /**
     * Accounts sekmesini acar.
     *
     * <p>Tablonun degil <b>panelin</b> gorunmesi beklenir: musterinin hic fatura hesabi yoksa
     * tablo render edilmez, yerine "There are no billing accounts yet." metni gosterilir
     * (FR-009 ACC-001). Tabloyu beklemek bu durumda sonsuza kadar takilirdi.
     */
    public CustomerDetailPage openAccountsTab() {
        selectTab(Tab.ACCOUNTS);
        wait.until(ExpectedConditions.visibilityOfElementLocated(ACCOUNTS_PANEL));
        return this;
    }

    /** ACC-002: "Create New Account" → "Create Billing Account" modal'i. */
    public BillingAccountModalComponent openCreateAccountModal() {
        click(CREATE_ACCOUNT_BUTTON);
        BillingAccountModalComponent modal = new BillingAccountModalComponent(driver);
        modal.waitUntilLoaded();
        return modal;
    }

    /**
     * Hesap sayisi beklenen degere ulasana kadar bekler ve o degeri dondurur.
     *
     * <p>Hesap olusturma modali BASARIDA kapanir, tablo tazelemesi ise kapanmanin
     * ARDINDAN asenkron baslar (bkz. detail-customer.component.ts: modal kapatilir,
     * sonra refreshAccountsAndAddresses cagrilir). Modal kapandi diye hemen saymak
     * eski listeyi okumak demektir; test hesap olusmus olmasina ragmen kirmizi duser.
     */
    public int waitForAccountCount(int expected) {
        try {
            wait.until(driver -> driver.findElements(ACCOUNT_ROWS).size() == expected);
        } catch (org.openqa.selenium.TimeoutException e) {
            // Beklenen sayiya ulasilmadi; cagiran assert gercek degeri raporlasin.
        }
        return accountCount();
    }

    /**
     * Verilen adli hesap tabloda gorunene kadar bekler ve guncel ad listesini dondurur.
     *
     * <p>{@link #waitForAccountCount(int)} ile ayni yaris icin: modal basarida kapanir,
     * tablo tazelemesi kapanmanin ARDINDAN asenkron baslar. Sayi yerine ADI dogrulayan
     * testler bu surumu kullanir.
     */
    public List<String> waitForAccountNamed(String accountName) {
        try {
            wait.until(driver -> accountNames().contains(accountName));
        } catch (org.openqa.selenium.TimeoutException e) {
            // Gorunmedi; cagiran assert gercek listeyi raporlasin.
        }
        return accountNames();
    }

    /** ACC-014: tablodaki hesap satiri sayisi (anlik, beklemez). */
    public int accountCount() {
        return findAll(ACCOUNT_ROWS).size();
    }

    /** ACC-014: bir satirdaki kolon degeri. */
    public String accountValue(int rowIndex, AccountColumn column) {
        return getText(By.cssSelector(String.format(
                "table.accounts-table tbody tr:nth-of-type(%d) td:nth-child(%d)",
                rowIndex + 1, column.index() + 1)));
    }

    /** Tablodaki tum hesap adlari — yeni hesabin listelendigini dogrulamak icin. */
    public List<String> accountNames() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < accountCount(); i++) {
            names.add(accountValue(i, AccountColumn.NAME));
        }
        return names;
    }

    /** ACC-001: tablo kolon basliklari. */
    public List<String> accountColumnHeaders() {
        List<String> headers = new ArrayList<>();
        for (WebElement header : findAll(ACCOUNT_HEADERS)) {
            headers.add(header.getText().trim());
        }
        return headers;
    }

    // --- FR-009: hesap satirini genisletme ve bagli urunler ---

    /** FR-009 ACC-003: satiri genisletip daraltan ok, her hesap satirinda bulunur. */
    public boolean hasExpandToggle(int rowIndex) {
        return isDisplayed(expandToggle(rowIndex));
    }

    /** FR-009 ACC-004: oka tiklar. Ayni oka tekrar tiklamak satiri daraltir. */
    public CustomerDetailPage toggleAccountRow(int rowIndex) {
        click(expandToggle(rowIndex));
        return this;
    }

    /** Satirin acik olup olmadigi — uygulama acik satirdaki oka {@code expanded} sinifi ekler. */
    public boolean isAccountRowExpanded(int rowIndex) {
        return find(expandToggle(rowIndex)).getAttribute("class").contains("expanded");
    }

    /** FR-009 ACC-004: genisletilmis satirdaki urun tablosu goruntuleniyor mu. */
    public boolean isProductTableDisplayed() {
        return isDisplayedAfterWait(PRODUCTS_TABLE);
    }

    /** Satir daraltildiktan sonra urun tablosunun DOM'dan kalktigini bekler. */
    public CustomerDetailPage waitUntilProductTableHidden() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(PRODUCTS_TABLE));
        return this;
    }

    /** FR-009 ACC-005: urun tablosu kolon basliklari. */
    public List<String> productColumnHeaders() {
        List<String> headers = new ArrayList<>();
        for (WebElement header : findAll(PRODUCT_HEADERS)) {
            headers.add(header.getText().trim());
        }
        return headers;
    }

    /**
     * Verilen adli urun tabloda gorunene kadar bekler ve guncel ad listesini dondurur.
     *
     * <p>Hesap satiri genisletildiginde urun tablosu API'den ASENKRON yuklenir; anlik
     * okuma tablo dolmadan bos liste dondurur.
     */
    public List<String> waitForProductNamed(String productName) {
        try {
            wait.until(driver -> productNames().contains(productName));
        } catch (org.openqa.selenium.TimeoutException e) {
            // Gorunmedi; cagiran assert gercek listeyi raporlasin.
        }
        return productNames();
    }

    /** Urun tablosundaki urun adlari (2. kolon). */
    public List<String> productNames() {
        List<String> names = new ArrayList<>();
        for (WebElement cell : findAll(By.cssSelector("table.products-table tbody tr td:nth-child(2)"))) {
            names.add(cell.getText().trim());
        }
        return names;
    }

    /** Hesabin hic urunu yoksa tablo yerine gosterilen metin. */
    public boolean hasNoProductsMessage() {
        return isDisplayedAfterWait(NO_PRODUCTS_MESSAGE);
    }

    public String noProductsMessage() {
        return getText(NO_PRODUCTS_MESSAGE);
    }

    /** FR-009 ACC-006: urun satirindaki goz ikonu → Product Offer Details modal'i. */
    public ProductDetailModalComponent openProductDetail(int productRowIndex) {
        click(By.cssSelector(String.format(
                "table.products-table tbody tr:nth-of-type(%d) .icon-button", productRowIndex + 1)));
        ProductDetailModalComponent modal = new ProductDetailModalComponent(driver);
        modal.waitUntilLoaded();
        return modal;
    }

    // --- FR-010 / FR-011: hesap satiri aksiyonlari ---

    /**
     * FR-010 ACC-001: kalem ikonu → "Update Billing Account" ekrani.
     *
     * <p>Uygulama <b>ayni modal'i</b> kullanir; baslik {@code editingAccount()} degerine gore
     * degisir. Bu yuzden alan id'leri, kaydet butonu ve FR-008'de bulunan modal tasmasi
     * (UI-01) guncelleme akisi icin de gecerlidir.
     */
    public BillingAccountModalComponent openEditAccountModal(int rowIndex) {
        click(By.cssSelector(String.format(
                "table.accounts-table tbody tr:nth-of-type(%d) .account-row-actions "
                        + ".icon-button:not(.icon-button-danger)", rowIndex + 1)));
        BillingAccountModalComponent modal = new BillingAccountModalComponent(driver);
        modal.waitUntilLoaded();
        return modal;
    }

    /** FR-011 ACC-001: cop ikonu → silme onay diyalogu. */
    public ConfirmDialogComponent deleteAccount(int rowIndex) {
        click(By.cssSelector(String.format(
                "table.accounts-table tbody tr:nth-of-type(%d) .account-row-actions "
                        + ".icon-button-danger", rowIndex + 1)));
        return new ConfirmDialogComponent(driver);
    }

    /**
     * FR-011 ACC-003/ACC-004: silme reddedildiginde acilan "silinemez" diyalogu.
     *
     * <p>Onay diyalogundan <b>ayri</b> bir modaldir: silme istegi 409 dondugunde onay
     * diyalogu kapanir ve backend'in mesajini tasiyan bu diyalog acilir.
     */
    public boolean hasCannotDeleteAccountDialog() {
        return isDisplayedAfterWait(CANNOT_DELETE_DIALOG);
    }

    public String cannotDeleteAccountMessage() {
        return getText(CANNOT_DELETE_MESSAGE);
    }

    public CustomerDetailPage closeCannotDeleteAccountDialog() {
        click(CANNOT_DELETE_CLOSE);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(CANNOT_DELETE_DIALOG));
        return this;
    }

    /** Silme sonrasi hesap sayisinin gercekten azalmasini bekler (liste asenkron yenilenir). */
    public CustomerDetailPage waitUntilAccountCountIsLessThan(int previousCount) {
        wait.until(ExpectedConditions.numberOfElementsToBeLessThan(ACCOUNT_ROWS, previousCount));
        return this;
    }

    /**
     * FR-012 ACC-001: genisletilmis hesap satirindaki "Start New Sale" butonu.
     *
     * <p>Buton yalnizca satir genisletildiginde gorunur — once
     * {@link #toggleAccountRow(int)} cagrilmalidir.
     */
    public OfferSelectionPage startNewSale() {
        click(START_NEW_SALE_BUTTON);
        OfferSelectionPage page = new OfferSelectionPage(driver);
        page.waitUntilLoaded();
        return page;
    }

    /**
     * Beklemeli kontrol: buton, hesap satiri GENISLETILDIGINDE acilan panelin icinde
     * render edilir. Anlik kontrol, panel henuz cizilmeden false donebilir.
     */
    public boolean hasStartNewSaleButton() {
        return isDisplayedAfterWait(START_NEW_SALE_BUTTON);
    }

    // --- FR-009 ACC-009: sayfalama ---

    /** Sayfalama kontrolleri yalnizca birden fazla sayfa varsa render edilir. */
    public boolean hasAccountsPagination() {
        return isDisplayed(ACCOUNTS_PAGINATION);
    }

    /** "1-5 / 7" bicimindeki aralik etiketi. */
    public String accountsRangeLabel() {
        return getText(ACCOUNTS_PAGINATION_RANGE);
    }

    /**
     * Panelin gorunen metni.
     *
     * <p>Beklenen bir metnin ekranda <b>hic bulunmadigini</b> gostermek icin kullanilir:
     * metin implement edilmediginde ona ait bir locator da olmadigi icin normal yontemle
     * assert edilemez (FR-009 ACC-001 bos durum mesaji).
     */
    public String pageText() {
        return getText(ROOT);
    }

    private By expandToggle(int rowIndex) {
        return By.cssSelector(String.format(
                "table.accounts-table tbody tr:nth-of-type(%d) .account-expand-toggle", rowIndex + 1));
    }
}
