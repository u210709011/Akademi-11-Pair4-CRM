package com.crmlite.ui.pages.newsale;

import com.crmlite.ui.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * FR-012 — "Offer Selection" ekrani (Yeni Satis sihirbazinin ilk adimi).
 * Rota: {@code /new-sale/{custId}/{custAcctId}}
 *
 * <p>Sihirbaz uc adimdan olusur (Offer Selection → Configuration → Review) ve <b>tek bir
 * kabuk bilesen</b> icinde yasar: adim degistiginde rota degismez, {@code activeStep}
 * sinyali degisir. Bu yuzden "hangi adimdayiz" sorusu URL'den degil, stepper'daki
 * {@code .active} isaretinden okunur.
 *
 * <p>Sepet paneli sagda sabittir ve her adimda gorunur; ileri butonu son adimda
 * "Submit" etiketine doner ({@code nextButtonLabel}).
 */
public class OfferSelectionPage extends BasePage {

    private static final By ROOT = By.cssSelector(".new-sale-page");

    private static final By OFFER_TABS = By.cssSelector(".offer-tabs .offer-tab");
    private static final By ACTIVE_TAB = By.cssSelector(".offer-tabs .offer-tab.active");

    private static final By BASKET_PANEL = By.cssSelector("aside.basket-panel");
    private static final By BASKET_EMPTY_TEXT = By.cssSelector(".basket-panel .basket-empty-text");
    private static final By BASKET_QUANTITY = By.cssSelector(".basket-panel .basket-quantity-badge");

    private static final By NEXT_BUTTON = By.cssSelector(".next-button");

    private static final By STEPPER_ITEMS = By.cssSelector(".stepper .stepper-item");
    private static final By ACTIVE_STEP_LABEL = By.cssSelector(".stepper .stepper-item.active .stepper-label");

    // --- FR-013: arama formu ---
    // Iki sekmenin paneli @if ile kosullu render ediliyor; ayni anda yalnizca biri DOM'da
    // oldugu icin ortak .offer-search-button secicisi guvenlidir.
    private static final By SEARCH_BUTTON = By.cssSelector(".offer-search-button");
    private static final By SEARCH_FIELDS = By.cssSelector(".offer-search-form .offer-search-field");

    private static final By CATALOG_CATEGORY = By.id("catalogId");
    private static final By CATALOG_OFFER_ID = By.id("offerId");
    private static final By CATALOG_OFFER_NAME = By.id("offerName");

    private static final By CAMPAIGN_CATEGORY = By.id("campaignCategory");
    private static final By CAMPAIGN_REF = By.id("campaignRef");
    private static final By CAMPAIGN_NAME = By.id("campaignName");

    // --- FR-013: sonuc tablosu ---
    private static final By RESULTS_HEADERS = By.cssSelector(".offer-results-table thead th");
    // :not(.bundled-offers-row) SART: kampanya satiri genisletildiginde bagli urun tablosunu
    // tasiyan KARDES bir <tr> ekleniyor; onu haric tutmazsak kampanya sayisi sismis gorunur.
    private static final By RESULT_ROWS = By.cssSelector(
            ".offer-results-table > tbody > tr:not(.bundled-offers-row):not(.offer-results-empty-row)");
    private static final By EMPTY_ROW = By.cssSelector(".offer-results-table .offer-results-empty-row");

    private static final By BUNDLED_TOGGLE = By.cssSelector(".bundled-offers-toggle");
    private static final By BUNDLED_ROW = By.cssSelector(".offer-results-table tr.bundled-offers-row");
    private static final By BUNDLED_OFFER_ROWS = By.cssSelector(".bundled-offers-table > tbody > tr");

    private static final By PAGINATION = By.cssSelector(".pagination");
    private static final By PAGINATION_PAGES = By.cssSelector(".pagination .pagination-page");
    private static final By PAGINATION_RANGE = By.cssSelector(".pagination .pagination-range");

    // --- FR-014: sepete ekleme ---
    private static final By ADD_BUTTONS = By.cssSelector(".offer-results-table .offer-add-button");

    // --- FR-014: sepet icerigi ---
    // Kullanicinin sectigi satirlar ile otomatik eklenen ZORUNLU satirlar ayni .basket-item
    // sinifini paylasir; zorunlu olanlar ek olarak .basket-item-locked tasir. Kullanici
    // satirlarini sayarken :not(.basket-item-locked) SART, yoksa ACC-005'in ayirt etmesi
    // gereken iki kume birbirine karisir.
    private static final By BASKET_USER_ITEMS =
            By.cssSelector(".basket-panel .basket-item:not(.basket-item-locked)");
    private static final By BASKET_LOCKED_ITEMS =
            By.cssSelector(".basket-panel .basket-item.basket-item-locked");
    private static final By BASKET_CAMPAIGN_GROUPS = By.cssSelector(".basket-panel .basket-campaign-group");
    private static final By BASKET_ITEM_NAMES = By.cssSelector(".basket-panel .basket-item-name");
    private static final By BASKET_ITEM_PRICES = By.cssSelector(".basket-panel .basket-item-price");
    private static final By BASKET_REMOVE_BUTTONS = By.cssSelector(".basket-panel .basket-item-remove");
    private static final By BASKET_LOCK_ICONS = By.cssSelector(".basket-panel .basket-item-lock-icon");
    private static final By BASKET_TOTAL_VALUE =
            By.cssSelector(".basket-panel .basket-summary-row.basket-total-row span:last-child");
    private static final By CLEAR_BASKET_BUTTON = By.cssSelector(".basket-panel .clear-basket-button");

    // --- FR-014: catisma bildirimi (ACC-012) ---
    private static final By TOAST_MESSAGE = By.cssSelector(".app-toast .app-toast-message");

    // --- FR-014: Cancel akisi (ACC-016) ---
    // .cancel-button sinifi hem sihirbaz cubugunda hem onay modalinda kullaniliyor;
    // kapsamlanmazsa sessizce yanlis butona tiklanir.
    private static final By WIZARD_CANCEL_BUTTON = By.cssSelector(".wizard-actions .cancel-button");
    private static final By CANCEL_CONFIRM_MESSAGE = By.cssSelector(".delete-confirm-message");
    private static final By CANCEL_CONFIRM_ACCEPT = By.cssSelector(".modal-actions .delete-confirm-button");

    /** Sekmeler, render sirasina gore. */
    public enum Tab {
        CATALOG(0),
        CAMPAIGNS(1);

        private final int index;

        Tab(int index) {
            this.index = index;
        }

        public int index() {
            return index;
        }
    }

    public OfferSelectionPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By pageReadyLocator() {
        return ROOT;
    }

    @Override
    protected String pageName() {
        return "Offer Selection";
    }

    /**
     * Sihirbaz ekrani goruntuleniyor mu.
     *
     * <p>{@link #waitUntilLoaded()} zaten yuklenmeyi bekler ve yuklenmezse hata firlatir;
     * bu metot testin niyetini acik yazabilmesi icin vardir.
     */
    public boolean isDisplayed() {
        return isDisplayed(ROOT);
    }

    // --- ACC-002: Catalog / Campaign sekmeleri ---

    public int tabCount() {
        return findAll(OFFER_TABS).size();
    }

    /** Sekme etiketleri, soldan saga. */
    public List<String> tabLabels() {
        List<String> labels = new ArrayList<>();
        for (WebElement tab : findAll(OFFER_TABS)) {
            labels.add(tab.getText().trim());
        }
        return labels;
    }

    public String activeTabLabel() {
        return getText(ACTIVE_TAB);
    }

    /**
     * Sekmeye gecer ve sekmenin AKTIF olmasini bekler.
     *
     * <p>Beklemeden donmek, cagiranin bir onceki sekmenin etiketini okumasina yol acar;
     * tam regresyonda yuk altinda bu yarisa girilip testler rastgele kirmizi oluyordu.
     */
    public OfferSelectionPage selectTab(Tab tab) {
        click(By.cssSelector(String.format(".offer-tabs .offer-tab:nth-of-type(%d)", tab.index() + 1)));
        By activeTab = By.cssSelector(String.format(
                ".offer-tabs .offer-tab:nth-of-type(%d).active", tab.index() + 1));
        wait.until(driver -> !driver.findElements(activeTab).isEmpty());
        return this;
    }

    // --- ACC-003 / ACC-004: Sepet ---

    public boolean isBasketDisplayed() {
        return isDisplayed(BASKET_PANEL);
    }

    /** ACC-004: sepet bosken tablo yerine gosterilen metin. */
    public boolean isBasketEmpty() {
        return isDisplayed(BASKET_EMPTY_TEXT);
    }

    public String basketEmptyText() {
        return getText(BASKET_EMPTY_TEXT);
    }

    /** Sepet basligindaki adet rozeti. */
    public String basketQuantity() {
        return getText(BASKET_QUANTITY);
    }

    // --- ACC-005: Next ---

    public boolean hasNextButton() {
        return isDisplayed(NEXT_BUTTON);
    }

    public String nextButtonLabel() {
        return getText(NEXT_BUTTON);
    }

    /** Sepet bosken ileri gidilememelidir; buton pasif kalir. */
    public boolean isNextDisabled() {
        return remainsDisabled(NEXT_BUTTON);
    }

    // --- Sihirbaz adimlari ---

    public int stepCount() {
        return findAll(STEPPER_ITEMS).size();
    }

    /** Aktif adimin etiketi — rota degismedigi icin adim buradan okunur. */
    public String activeStepLabel() {
        return getText(ACTIVE_STEP_LABEL);
    }

    // --- FR-013: arama formu (ACC-003, ACC-005, ACC-007, ACC-009) ---

    /** Aktif sekmedeki arama alani sayisi (kategori + iki metin alani beklenir). */
    public int searchFieldCount() {
        return findAll(SEARCH_FIELDS).size();
    }

    public boolean hasSearchButton() {
        return isDisplayed(SEARCH_BUTTON);
    }

    /** ACC-005/ACC-009: hicbir kriter dolu degilken Search pasif kalmalidir. */
    public boolean isSearchDisabled() {
        return remainsDisabled(SEARCH_BUTTON);
    }

    public boolean isSearchEnabled() {
        return isEnabled(SEARCH_BUTTON);
    }

    public OfferSelectionPage search() {
        click(SEARCH_BUTTON);
        return this;
    }

    // Katalog sekmesi alanlari
    // Beklemeli kontrol: sekme paneli @if ile kosullu render edildigi icin sekme
    // degistikten hemen sonra alanlar henuz DOM'da olmayabilir.
    public boolean hasCatalogCategorySelect() {
        return isDisplayedAfterWait(CATALOG_CATEGORY);
    }

    public OfferSelectionPage enterOfferId(String value) {
        type(CATALOG_OFFER_ID, value);
        return this;
    }

    public OfferSelectionPage enterOfferName(String value) {
        type(CATALOG_OFFER_NAME, value);
        return this;
    }

    public String offerIdValue() {
        return getValue(CATALOG_OFFER_ID);
    }

    public String offerNameValue() {
        return getValue(CATALOG_OFFER_NAME);
    }

    // Kampanya sekmesi alanlari
    public boolean hasCampaignCategorySelect() {
        return isDisplayedAfterWait(CAMPAIGN_CATEGORY);
    }

    public OfferSelectionPage enterCampaignRef(String value) {
        type(CAMPAIGN_REF, value);
        return this;
    }

    public OfferSelectionPage enterCampaignName(String value) {
        type(CAMPAIGN_NAME, value);
        return this;
    }

    public String campaignRefValue() {
        return getValue(CAMPAIGN_REF);
    }

    // --- FR-013: sonuc tablosu (ACC-004, ACC-006, ACC-008, ACC-010, ACC-013, ACC-014) ---

    /** Aktif sekmedeki tablonun kolon basliklari, soldan saga. */
    public List<String> resultColumnHeaders() {
        List<String> headers = new ArrayList<>();
        for (WebElement th : findAll(RESULTS_HEADERS)) {
            headers.add(th.getText().trim());
        }
        return headers;
    }

    /** Genisletilmis kampanyanin ic tablosu HARIC, gercek sonuc satiri sayisi. */
    public int resultRowCount() {
        return findAll(RESULT_ROWS).size();
    }

    /** ACC-013: kayit bulunamadiginda gosterilen satirin metni. */
    public String emptyResultText() {
        return getText(EMPTY_ROW);
    }

    public boolean hasEmptyResultRow() {
        return isDisplayedAfterWait(EMPTY_ROW);
    }

    /**
     * Aramayi sonuc gelene kadar tekrarlar.
     *
     * <p>Katalog/kampanya listeleri sekme acildiginda ASENKRON yuklenir ve arama bellekteki
     * liste uzerinde calisir; hemen aranirsa liste henuz bos oldugu icin "kayit bulunamadi"
     * doner. Bu bir urun hatasi degil, veri yuklenme yarisidir.
     */
    public OfferSelectionPage searchUntilResults() {
        for (int attempt = 0; attempt < 6; attempt++) {
            search().waitForResults();
            if (resultRowCount() > 0) {
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

    /** Sonuclarin yuklenmesini bekler; bos sonuc da gecerli bir sonuctur. */
    public OfferSelectionPage waitForResults() {
        wait.until(driver -> !driver.findElements(RESULT_ROWS).isEmpty()
                || !driver.findElements(EMPTY_ROW).isEmpty());
        return this;
    }

    // --- FR-013: kampanya bagli urunleri (ACC-011, ACC-012) ---

    public boolean hasBundledOffersToggle() {
        return isDisplayed(BUNDLED_TOGGLE);
    }

    /** Satirdaki genisletme okuna tiklar; ayni tiklama kapatma islevi de gorur. */
    public OfferSelectionPage toggleBundledOffers(int rowIndex) {
        findAll(BUNDLED_TOGGLE).get(rowIndex).click();
        return this;
    }

    public boolean isBundledOffersExpanded() {
        return isDisplayedAfterWait(BUNDLED_ROW);
    }

    public boolean isBundledOffersCollapsed() {
        return findAll(BUNDLED_ROW).isEmpty();
    }

    /** Genisletilmis satirin ic tablosundaki bagli urun sayisi. */
    public int bundledOfferCount() {
        return findAll(BUNDLED_OFFER_ROWS).size();
    }

    // --- FR-013: sayfalama (ACC-014) ---

    public boolean hasPagination() {
        return isDisplayed(PAGINATION);
    }

    public int paginationPageCount() {
        return findAll(PAGINATION_PAGES).size();
    }

    /** "1-5 / 12" benzeri aralik etiketi. */
    public String paginationRangeLabel() {
        return getText(PAGINATION_RANGE);
    }

    // --- FR-014: sepete ekleme (ACC-001, ACC-010, ACC-011) ---

    public String addButtonLabel(int rowIndex) {
        return findAll(ADD_BUTTONS).get(rowIndex).getText().trim();
    }

    public boolean isAddButtonEnabled(int rowIndex) {
        return findAll(ADD_BUTTONS).get(rowIndex).isEnabled();
    }

    /**
     * Ekle butonuna tiklar ama sepetin BUYUMESINI BEKLEMEZ.
     *
     * <p>Catisma senaryosunda (ACC-012) ekleme reddedilir ve sepet degismez; bekleyen
     * {@link #addToBasket(int)} bu durumda bosuna zaman asimina ugrardi.
     */
    public OfferSelectionPage clickAddButton(int rowIndex) {
        findAll(ADD_BUTTONS).get(rowIndex).click();
        return this;
    }

    /** Belirtilen sonuc satirini sepete ekler ve sepetin guncellenmesini bekler. */
    public OfferSelectionPage addToBasket(int rowIndex) {
        int before = basketLineCount();
        findAll(ADD_BUTTONS).get(rowIndex).click();
        wait.until(driver -> basketLineCount() > before);
        return this;
    }

    /** Sepetteki TUM satirlar (kullanici secimleri + otomatik eklenenler + kampanya gruplari). */
    public int basketLineCount() {
        return findAll(BASKET_USER_ITEMS).size()
                + findAll(BASKET_LOCKED_ITEMS).size()
                + findAll(BASKET_CAMPAIGN_GROUPS).size();
    }

    // --- FR-014: sepet icerigi (ACC-002, ACC-003, ACC-005, ACC-007, ACC-008) ---

    /** ACC-005: kullanicinin kendi ekledigi, silinebilir satirlar. */
    public int userItemCount() {
        return findAll(BASKET_USER_ITEMS).size();
    }

    /** ACC-003/ACC-005: zorunlu iliski nedeniyle otomatik eklenen, kilitli satirlar. */
    public int lockedItemCount() {
        return findAll(BASKET_LOCKED_ITEMS).size();
    }

    public List<String> basketItemNames() {
        List<String> names = new ArrayList<>();
        for (WebElement el : findAll(BASKET_ITEM_NAMES)) {
            names.add(el.getText().trim());
        }
        return names;
    }

    /** ACC-002: her satirda bir fiyat gosterilmelidir. */
    public int basketPriceCount() {
        return findAll(BASKET_ITEM_PRICES).size();
    }

    /** ACC-005: silme ikonu yalnizca kullanici satirlarinda bulunmalidir. */
    public int removeButtonCount() {
        return findAll(BASKET_REMOVE_BUTTONS).size();
    }

    /** ACC-005: zorunlu satirlarda silme yerine kilit ikonu gosterilir. */
    public int lockIconCount() {
        return findAll(BASKET_LOCK_ICONS).size();
    }

    /**
     * ACC-008: Total Amount alanindaki tutar, sayisal deger olarak.
     *
     * <p>Metin yerine SAYI dondurulur: dokuman tutari Turkce bicimle ("₺0,00") yazarken
     * uygulama EN yerelinde ("0.00 ₺") gosteriyor. Bicim farki bir kural ihlali degildir,
     * bu yuzden test bicimi degil DEGERI dogrular.
     */
    public double totalAmount() {
        String raw = getText(BASKET_TOTAL_VALUE).replaceAll("[^0-9.,]", "").trim();
        // Son ayirici ondalik kabul edilir; digerleri binlik ayiricidir.
        int lastDot = raw.lastIndexOf('.');
        int lastComma = raw.lastIndexOf(',');
        int decimalAt = Math.max(lastDot, lastComma);
        if (decimalAt < 0) {
            return Double.parseDouble(raw.isEmpty() ? "0" : raw);
        }
        String intPart = raw.substring(0, decimalAt).replaceAll("[.,]", "");
        String fracPart = raw.substring(decimalAt + 1);
        return Double.parseDouble((intPart.isEmpty() ? "0" : intPart) + "." + fracPart);
    }

    // --- FR-014: sepetten cikarma (ACC-013, ACC-014, ACC-015) ---

    public OfferSelectionPage removeBasketItem(int index) {
        int before = basketLineCount();
        findAll(BASKET_REMOVE_BUTTONS).get(index).click();
        wait.until(driver -> basketLineCount() < before);
        return this;
    }

    public boolean isClearBasketDisabled() {
        return remainsDisabled(CLEAR_BASKET_BUTTON);
    }

    public OfferSelectionPage clearBasket() {
        click(CLEAR_BASKET_BUTTON);
        wait.until(driver -> basketLineCount() == 0);
        return this;
    }

    // --- FR-014: catisma bildirimi (ACC-012) ---

    public String toastMessage() {
        return getText(TOAST_MESSAGE);
    }

    public boolean hasToast() {
        return isDisplayedAfterWait(TOAST_MESSAGE);
    }

    /**
     * Toast metni verilenden FARKLI olana kadar bekler ve yeni metni dondurur.
     *
     * <p>Sepete ekleme basarili oldugunda da toast cikar. Ikinci bir islemin sonucunu
     * hemen okumak, ekranda hala duran ONCEKI toast'i okumak demektir - catisma testi
     * tam olarak bu yuzden yanlis mesajla kirmiziya dusuyordu.
     */
    public String toastMessageOtherThan(String previous) {
        wait.until(driver -> {
            List<WebElement> toasts = driver.findElements(TOAST_MESSAGE);
            return !toasts.isEmpty() && !toasts.get(0).getText().trim().equals(previous);
        });
        return toastMessage();
    }

    // --- FR-014: Cancel akisi (ACC-016) ---

    public OfferSelectionPage clickCancel() {
        click(WIZARD_CANCEL_BUTTON);
        return this;
    }

    /**
     * Ayni butonun ikinci adimdaki hali: ilk adimda "Cancel", sonraki adimlarda "Previous"
     * etiketiyle render edilir. Cagiranin niyeti okunabilsin diye ayri ad verilmistir.
     */
    public OfferSelectionPage clickPrevious() {
        click(WIZARD_CANCEL_BUTTON);
        return this;
    }

    /** Sihirbaz cubugundaki ilk butonun etiketi (Cancel / Previous). */
    public String wizardBackButtonLabel() {
        return getText(WIZARD_CANCEL_BUTTON);
    }

    public String cancelConfirmMessage() {
        return getText(CANCEL_CONFIRM_MESSAGE);
    }

    public void confirmCancel() {
        click(CANCEL_CONFIRM_ACCEPT);
    }

    // --- FR-014: ilerleme (ACC-017, ACC-018) ---

    public boolean isNextEnabled() {
        return isEnabled(NEXT_BUTTON);
    }

    /** Sepette urun varken Next'e basar; sihirbaz Configuration adimina gecer. */
    public OfferSelectionPage clickNext() {
        click(NEXT_BUTTON);
        return this;
    }

    /**
     * Aktif adim etiketi beklenene esitlenene kadar bekler.
     *
     * <p>Adim gecisi ASENKRONDUR (sepet dogrulamasi sunucuya gider ve buton spinner
     * gosterir). Next'e bastiktan hemen sonra etiketi okumak, henuz degismemis ONCEKI
     * adimi okumak demektir.
     */
    public OfferSelectionPage waitForActiveStep(String expectedLabel) {
        wait.until(driver -> {
            List<WebElement> labels = driver.findElements(ACTIVE_STEP_LABEL);
            return !labels.isEmpty() && labels.get(0).getText().trim().equals(expectedLabel);
        });
        return this;
    }
}
