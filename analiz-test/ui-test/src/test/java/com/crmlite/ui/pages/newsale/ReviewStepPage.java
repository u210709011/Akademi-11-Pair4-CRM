package com.crmlite.ui.pages.newsale;

import com.crmlite.ui.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * FR-016 / FR-017 — "Review &amp; Submit" ekrani (Yeni Satis sihirbazinin son adimi).
 *
 * <p>Ozet alanlari etiket-deger ciftleri halinde render edilir. Deger okumak icin SIRA
 * (nth-of-type) yerine ETIKET METNI kullanilir: alan sirasi degistiginde sirasal secici
 * sessizce yanlis degeri okur, etiket tabanli secici ise acikca patlar.
 */
public class ReviewStepPage extends BasePage {

    private static final By ROOT = By.cssSelector(".review-content-wrap");

    private static final By SUMMARY_FIELDS = By.cssSelector(".review-summary-field");

    // Kampanya gruplarini tasiyan baslik satiri gercek bir urun degildir, disarida birakilir.
    private static final By PRODUCT_ROWS = By.cssSelector(
            ".review-products-table > tbody > tr:not(.review-campaign-group-row)");
    private static final By PRODUCT_ID_CELLS = By.cssSelector(".review-products-table .offer-id-cell");

    private static final By ADDRESS_CARD = By.cssSelector(".review-address-card");
    private static final By ADDRESS_EMPTY = By.cssSelector(".review-address-empty");
    private static final By ADDRESS_GRID = By.cssSelector(".review-address-grid");

    private static final By PRICE_CARD = By.cssSelector(".review-price-card");
    // Toplam satiri .review-price-row TASIMAZ; yalnizca .review-total-row sinifina sahiptir.
    private static final By TOTAL_ROW_VALUE =
            By.cssSelector(".review-total-row span:last-child");

    // --- Submit sonrasi basari modali (FR-017) ---
    private static final By SUCCESS_CARD = By.cssSelector(".modal-card.order-success-card");
    private static final By SUCCESS_TITLE = By.cssSelector(".order-success-title");
    private static final By SUCCESS_SUBTITLE = By.cssSelector(".order-success-subtitle");
    private static final By GO_TO_BILLING_ACCOUNT =
            By.cssSelector(".order-success-actions .cancel-button");

    public ReviewStepPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By pageReadyLocator() {
        return ROOT;
    }

    @Override
    protected String pageName() {
        return "Review & Submit";
    }

    public boolean isDisplayed() {
        return isDisplayed(ROOT);
    }

    // --- FR-016 ACC-002: ozet alanlari ---

    /**
     * Etiketi verilen ozet alaninin degeri.
     *
     * <p>Etiket metni dile gore degistigi icin cagiran taraf beklenen metni
     * {@code ExpectedMessages} uzerinden gecirmelidir.
     */
    public String summaryValueFor(String label) {
        return getText(By.xpath(String.format(
                "//span[contains(@class,'review-summary-label')][normalize-space()='%s']"
                        + "/following-sibling::span[contains(@class,'review-summary-value')][1]",
                label)));
    }

    public boolean hasSummaryField(String label) {
        return isDisplayed(By.xpath(String.format(
                "//span[contains(@class,'review-summary-label')][normalize-space()='%s']", label)));
    }

    public int summaryFieldCount() {
        return findAll(SUMMARY_FIELDS).size();
    }

    // --- FR-016 ACC-003: siparis kalemleri ---

    public int productRowCount() {
        return findAll(PRODUCT_ROWS).size();
    }

    /** ACC-003: her kalem Prod Offer ID bilgisiyle listelenmelidir. */
    public List<String> productIds() {
        List<String> ids = new ArrayList<>();
        for (WebElement cell : findAll(PRODUCT_ID_CELLS)) {
            ids.add(cell.getText().trim());
        }
        return ids;
    }

    public boolean hasProductsTable() {
        return isDisplayed(By.cssSelector(".review-products-table"));
    }

    // --- FR-016 ACC-004: servis adresi ---

    public boolean hasServiceAddress() {
        return isDisplayed(ADDRESS_CARD) && isDisplayed(ADDRESS_GRID);
    }

    public boolean hasNoAddressPlaceholder() {
        return isDisplayed(ADDRESS_EMPTY);
    }

    public String serviceAddressText() {
        return getText(ADDRESS_GRID);
    }

    // --- FR-016 ACC-005: toplam tutar ---

    public boolean hasPriceCard() {
        return isDisplayed(PRICE_CARD);
    }

    /** Bicim degil DEGER dogrulanir; para bicimi yerele gore degisir. */
    public double totalAmount() {
        String raw = getText(TOTAL_ROW_VALUE).replaceAll("[^0-9.,]", "").trim();
        int decimalAt = Math.max(raw.lastIndexOf('.'), raw.lastIndexOf(','));
        if (decimalAt < 0) {
            return Double.parseDouble(raw.isEmpty() ? "0" : raw);
        }
        String intPart = raw.substring(0, decimalAt).replaceAll("[.,]", "");
        String fracPart = raw.substring(decimalAt + 1);
        return Double.parseDouble((intPart.isEmpty() ? "0" : intPart) + "." + fracPart);
    }

    // --- FR-017 ACC-002: submit onayi ---

    /**
     * Submit'e basildiginda onay diyalogu acildi mi.
     *
     * <p>Uygulamada boyle bir diyalog YOKTUR; sihirbazin diger onaylariyla ayni kalibi
     * kullanacagi varsayilarak ortak {@code .delete-confirm-*} isaretcileri aranir.
     * Eslesme olmamasi FR-017-GAP-ACC002 bulgusunun ta kendisidir.
     */
    public boolean hasCancelConfirmDialog() {
        return isDisplayedAfterWait(By.cssSelector(".modal-card .delete-confirm-message"));
    }

    public String confirmDialogMessage() {
        return getText(By.cssSelector(".modal-card .delete-confirm-message"));
    }

    /** ACC-002: onay diyalogundaki "Yes, Submit Order" butonuna basar. */
    public ReviewStepPage confirmSubmit() {
        click(By.cssSelector(".modal-actions .delete-confirm-button"));
        return this;
    }

    /** Submit sonrasi acilan basari modali. */
    public boolean hasSuccessModal() {
        return isDisplayedAfterWait(SUCCESS_CARD);
    }

    public String successTitle() {
        return getText(SUCCESS_TITLE);
    }

    public String successMessage() {
        return getText(SUCCESS_SUBTITLE);
    }

    /** ACC-005: basari modalindan musteri ekranina donus. */
    public void goToBillingAccount() {
        click(GO_TO_BILLING_ACCOUNT);
    }
}
