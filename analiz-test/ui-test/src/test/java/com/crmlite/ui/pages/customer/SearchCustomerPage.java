package com.crmlite.ui.pages.customer;

import com.crmlite.ui.pages.BasePage;
import com.crmlite.ui.pages.customer.create.CreateCustomerPage;
import com.crmlite.ui.pages.components.NavbarComponent;
import com.crmlite.ui.pages.components.PaginationComponent;
import com.crmlite.ui.pages.components.ResultsTableComponent;
import com.crmlite.ui.pages.components.SidebarComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.regex.Pattern;

/**
 * FR-002 — Musteri Arama ve Goruntuleme (UC-EACRML-002).
 */
public class SearchCustomerPage extends BasePage {

    // --- Filtre alanlari ---
    private static final By NAT_ID = By.id("natIdNumber");
    private static final By CUSTOMER_ID = By.id("customerId");
    private static final By ACCOUNT_NUMBER = By.id("accountNumber");
    private static final By GSM_NUMBER = By.id("gsmNumber");
    private static final By FIRST_NAME = By.id("firstName");
    private static final By LAST_NAME = By.id("lastName");
    private static final By ORDER_NUMBER = By.id("orderNumber");

    // --- Aksiyonlar ---
    private static final By SEARCH_BUTTON = By.cssSelector(".filters-actions .search-button");
    private static final By CLEAR_BUTTON = By.cssSelector(".filters-actions .clear-button");
    private static final By CREATE_CUSTOMER_HEADER = By.cssSelector(".create-customer-button-header");
    private static final By CREATE_CUSTOMER_EMPTY_STATE = By.cssSelector(".results-empty-state .create-customer-button");

    // --- Sonuc paneli ---
    private static final By PAGE_TITLE = By.cssSelector("h1.page-title");
    private static final By RESULTS_COUNT = By.cssSelector(".results-panel-header .results-count");
    private static final By EMPTY_STATE = By.cssSelector(".results-empty-state");
    private static final By EMPTY_TITLE = By.cssSelector(".results-empty-state .results-empty-title");
    private static final By EMPTY_DESCRIPTION = By.cssSelector(".results-empty-state .results-empty-description");

    private static final Pattern DETAIL_URL = Pattern.compile("/detail-customer/\\d+");

    /** Aranabilir filtre alanlari — data-driven testler icin. */
    public enum Filter {
        NAT_ID_NUMBER("natIdNumber"),
        CUSTOMER_ID("customerId"),
        ACCOUNT_NUMBER("accountNumber"),
        GSM_NUMBER("gsmNumber"),
        FIRST_NAME("firstName"),
        LAST_NAME("lastName"),
        ORDER_NUMBER("orderNumber");

        private final String id;

        Filter(String id) {
            this.id = id;
        }

        public String fieldId() {
            return id;
        }

        public By locator() {
            return By.id(id);
        }
    }

    public SearchCustomerPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By pageReadyLocator() {
        return SEARCH_BUTTON;
    }

    @Override
    protected String pageName() {
        return "Customer Search";
    }

    // --- Bilesenler ---

    public ResultsTableComponent resultsTable() {
        return new ResultsTableComponent(driver);
    }

    public PaginationComponent pagination() {
        return new PaginationComponent(driver);
    }

    public NavbarComponent navbar() {
        return new NavbarComponent(driver);
    }

    public SidebarComponent sidebar() {
        return new SidebarComponent(driver);
    }

    // --- Filtre doldurma (ACC-001) ---

    public SearchCustomerPage enterNatId(String value) {
        type(NAT_ID, value);
        return this;
    }

    public SearchCustomerPage enterCustomerId(String value) {
        type(CUSTOMER_ID, value);
        return this;
    }

    public SearchCustomerPage enterAccountNumber(String value) {
        type(ACCOUNT_NUMBER, value);
        return this;
    }

    public SearchCustomerPage enterGsmNumber(String value) {
        type(GSM_NUMBER, value);
        return this;
    }

    public SearchCustomerPage enterFirstName(String value) {
        type(FIRST_NAME, value);
        return this;
    }

    public SearchCustomerPage enterLastName(String value) {
        type(LAST_NAME, value);
        return this;
    }

    public SearchCustomerPage enterOrderNumber(String value) {
        type(ORDER_NUMBER, value);
        return this;
    }

    /** Data-driven testler icin genel giris. */
    public SearchCustomerPage enter(Filter filter, String value) {
        type(filter.locator(), value);
        return this;
    }

    /**
     * Deger girip odagi kaldirir.
     *
     * <p>NAT ID ve GSM format kurallari {@code (blur)} olayinda degerlendirilir
     * ({@code onNatIdBlur} / {@code onGsmBlur}); odak tasinmadan hata mesaji gorunmez.
     */
    public SearchCustomerPage enterAndBlur(Filter filter, String value) {
        type(filter.locator(), value);
        blur(filter.locator());
        return this;
    }

    public String valueOf(Filter filter) {
        return getValue(filter.locator());
    }

    // --- Buton durumlari (ACC-003) ---

    /** ACC-003: en az bir filtre dolmadan Search aktif OLMAMALIDIR. */
    public boolean isSearchDisabled() {
        return remainsDisabled(SEARCH_BUTTON);
    }

    public boolean isSearchEnabled() {
        return becomesEnabled(SEARCH_BUTTON);
    }

    // --- Eylemler ---

    /** ACC-004: aramayi tetikler ve sonuc panelinin guncellenmesini bekler. */
    public SearchCustomerPage search() {
        click(SEARCH_BUTTON);
        return this;
    }

    /** ACC-012: tum filtreleri ve sonuclari temizler. */
    public SearchCustomerPage clearFilters() {
        click(CLEAR_BUTTON);
        return this;
    }

    /** ACC-011: sag ust kosedeki Create Customer butonu. */
    public CreateCustomerPage goToCreateCustomer() {
        click(CREATE_CUSTOMER_HEADER);
        CreateCustomerPage page = new CreateCustomerPage(driver);
        page.waitUntilLoaded();
        return page;
    }

    /** ACC-010: "kayit bulunamadi" ekranindaki Create Customer butonu. */
    public CreateCustomerPage goToCreateCustomerFromEmptyState() {
        click(CREATE_CUSTOMER_EMPTY_STATE);
        CreateCustomerPage page = new CreateCustomerPage(driver);
        page.waitUntilLoaded();
        return page;
    }

    /** ACC-009: Customer ID'ye tiklayarak Customer Info ekranini acar. */
    public CustomerDetailPage openCustomer(String customerId) {
        resultsTable().openCustomerById(customerId);
        return waitForDetailPage();
    }

    public CustomerDetailPage openFirstResult() {
        resultsTable().openCustomerAtRow(0);
        return waitForDetailPage();
    }

    // --- Sonuc durumu ---

    public String pageTitle() {
        return getText(PAGE_TITLE);
    }

    public boolean hasResults() {
        return resultsTable().hasAnyRow();
    }

    /** ACC-010: sonuc bulunamadi durumu. */
    public boolean isEmptyStateDisplayed() {
        return isDisplayedAfterWait(EMPTY_STATE);
    }

    /**
     * ACC-010 baslik metni.
     *
     * <p><b>Not:</b> Dokumanda iki farkli metin var — ACC tablosu
     * "No customer found. You can create a new customer using the Create Customer button…",
     * UC-002 Adim 6.2 ise "No customer found! Would you like to create the customer?" diyor.
     * Uygulama <b>UC surumunu</b> gerceklestiriyor (baslik + alt baslik olarak ikiye bolunmus).
     */
    public String emptyStateTitle() {
        return getText(EMPTY_TITLE);
    }

    public String emptyStateDescription() {
        return getText(EMPTY_DESCRIPTION);
    }

    public String resultsCountLabel() {
        return getText(RESULTS_COUNT);
    }

    /** Filtre alanina ait validasyon hatasi (ornegin NAT ID / GSM format kurallari). */
    public String filterError(Filter filter) {
        return fieldErrorText(filter.fieldId());
    }

    public boolean hasFilterError(Filter filter) {
        return hasFieldError(filter.fieldId());
    }

    private CustomerDetailPage waitForDetailPage() {
        CustomerDetailPage page = new CustomerDetailPage(driver);
        page.waitUntilLoaded();
        return page;
    }

    public Pattern detailUrlPattern() {
        return DETAIL_URL;
    }
}
