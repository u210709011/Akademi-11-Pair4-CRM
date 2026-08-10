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

    public OfferSelectionPage selectTab(Tab tab) {
        click(By.cssSelector(String.format(".offer-tabs .offer-tab:nth-of-type(%d)", tab.index() + 1)));
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
}
