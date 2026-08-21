package com.crmlite.ui.pages.customer.create;

import com.crmlite.ui.core.waits.AppConditions;
import com.crmlite.ui.core.waits.WaitFactory;
import com.crmlite.ui.pages.BasePage;
import com.crmlite.ui.pages.customer.CustomerDetailPage;
import com.crmlite.ui.pages.customer.SearchCustomerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * FR-003 — Musteri Olusturma sihirbazi kabugu (UC-EACRML-003).
 *
 * <p>Sihirbaz <b>tek rota</b> ({@code /create-customer}) uzerinde calisir; adimlar
 * {@code NgComponentOutlet} ile degistirilir. Tek bir "Next" butonu vardir ve
 * son adimda etiketi "Create" olur.
 *
 * <p><b>Dokuman notu:</b> FR-003 ACC-003 "Previous ile musteri arama ekranina donulur"
 * diyor; uygulamada bu butonun etiketi <b>Cancel</b> ({@code .cancel-button}) ve ayni
 * islevi goruyor (arama ekranina doner, kayit olusturmaz).
 */
public class CreateCustomerPage extends BasePage {

    private static final By ROOT = By.cssSelector(".create-customer-page");
    private static final By PAGE_TITLE = By.cssSelector(".create-customer-page h1.page-title");

    private static final By TABS = By.cssSelector(".tabs-container .tab");
    private static final By ACTIVE_TAB = By.cssSelector(".tabs-container .tab.active");
    private static final By TAB_TITLE = By.cssSelector(".tab-content .tab-title");

    private static final By NEXT_BUTTON = By.cssSelector(".wizard-actions .next-button");
    private static final By CANCEL_BUTTON = By.cssSelector(".wizard-actions .cancel-button");

    // 10.08.2026: spinner sinifi .verifying-indicator -> .btn-spinner-wrap olarak degisti
    // (commit 6cce9c1). Bkz. AppConditions.identityVerificationFinished().
    private static final By VERIFYING_INDICATOR = By.cssSelector(".btn-spinner-wrap");
    private static final By ERROR_BANNER = By.cssSelector(".identity-error-banner");

    /** Sihirbaz adimlari; sekme butonlari bu sirayla render edilir. */
    public enum Step {
        DEMOGRAPHIC(0),
        ADDRESS(1),
        CONTACT(2);

        private final int index;

        Step(int index) {
            this.index = index;
        }

        public int index() {
            return index;
        }
    }

    public CreateCustomerPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By pageReadyLocator() {
        return NEXT_BUTTON;
    }

    @Override
    protected String pageName() {
        return "Create Customer (sihirbaz)";
    }

    // --- Adimlar ---

    public DemographicStep demographicStep() {
        return new DemographicStep(driver);
    }

    public AddressStep addressStep() {
        return new AddressStep(driver);
    }

    public ContactStep contactStep() {
        return new ContactStep(driver);
    }

    // --- Sekme durumu ---

    public String pageTitle() {
        return getText(PAGE_TITLE);
    }

    public String activeTabLabel() {
        return getText(ACTIVE_TAB);
    }

    public String stepTitle() {
        return getText(TAB_TITLE);
    }

    public int tabCount() {
        return findAll(TABS).size();
    }

    /**
     * Sekme butonlari, Next ile henuz ulasilmamis adimlar icin kilitlidir
     * ({@code isTabLocked}); ileri atlama boylece engellenir.
     */
    public boolean isStepLocked(Step step) {
        return !isEnabled(tabLocator(step));
    }

    public void selectStep(Step step) {
        click(tabLocator(step));
    }

    // --- Next / Create butonu (ACC-002, ACC-011, ACC-014) ---

    /** Son adimda "Create", digerlerinde "Next" yazar. */
    public String nextButtonLabel() {
        return getText(NEXT_BUTTON);
    }

    /** ACC-002 / ACC-011 / ACC-014: zorunlu alanlar eksikken buton pasif KALMALIDIR. */
    public boolean isNextDisabled() {
        return remainsDisabled(NEXT_BUTTON);
    }

    public boolean isNextEnabled() {
        return becomesEnabled(NEXT_BUTTON);
    }

    /**
     * ACC-004 → ACC-008: Next'e basar ve tekillik + KPS dogrulamasi bitene kadar bekler.
     * Sonrasinda ya bir sonraki adima gecilmis ya da hata banner'i gorunmus olur.
     */
    public CreateCustomerPage clickNext() {
        click(NEXT_BUTTON);
        waitForIdentityVerification();
        return this;
    }

    /**
     * ACC-015/016: son adimda Create'e basar ve musteri detay ekranini bekler.
     * Basarili kayit sonrasi uygulama {@code /detail-customer/{custId}} rotasina gider.
     */
    public CustomerDetailPage clickCreate() {
        click(NEXT_BUTTON);
        WaitFactory.longWait(driver).until(AppConditions.absentFromDom(VERIFYING_INDICATOR));
        CustomerDetailPage detail = new CustomerDetailPage(driver);
        detail.waitUntilLoaded();
        return detail;
    }

    /** ACC-003: kayit olusturmadan arama ekranina doner (dokumanda "Previous", kodda "Cancel"). */
    public SearchCustomerPage cancel() {
        click(CANCEL_BUTTON);
        SearchCustomerPage page = new SearchCustomerPage(driver);
        page.waitUntilLoaded();
        return page;
    }

    public String cancelButtonLabel() {
        return getText(CANCEL_BUTTON);
    }

    // --- Dogrulama / hata durumu (ACC-005, ACC-007) ---

    public boolean isVerifying() {
        return isDisplayed(VERIFYING_INDICATOR);
    }

    /** ACC-005: ayni Nationality ID mevcutsa gosterilen uyari. */
    public boolean hasErrorBanner() {
        return isDisplayedAfterWait(ERROR_BANNER);
    }

    public String errorBannerText() {
        return getText(ERROR_BANNER);
    }

    private By tabLocator(Step step) {
        return By.cssSelector(String.format(".tabs-container .tab:nth-of-type(%d)", step.index() + 1));
    }
}
