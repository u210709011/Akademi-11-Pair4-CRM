package com.crmlite.ui.pages.components;

import com.crmlite.ui.core.waits.AppConditions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * FR-009 — "Product Offer Details" modal'i (Accounts sekmesi &gt; urun satiri &gt; goz ikonu).
 *
 * <p><b>Onemli:</b> modal su an <b>mock veriyle</b> calisiyor. Frontend'de
 * {@code PRODUCT_DETAIL_MOCK_MODE = true} ve dosyanin basinda "backend spec/karakteristik/
 * baslangic tarihi alanlarini donene kadar gecici test modu" notu var. Product Spec ID,
 * Service Start Date ve Prod Chars alanlari sabit mock degerlerden gelir (orn. SPEC-5521).
 *
 * <p>Bu yuzden testler alanlarin <b>varligini</b> dogrular, <b>degerlerini</b> degil:
 * mock degere assertion yazmak yesil ama anlamsiz bir test uretir ve mock kaldirildiginda
 * kirilir. Gercek veri baglandiginda deger dogrulamalari eklenmelidir.
 */
public class ProductDetailModalComponent extends BaseComponent {

    private static final By ROOT = By.cssSelector(".modal-backdrop .modal-card.product-detail-card");
    private static final By TITLE = By.cssSelector(".product-detail-card .modal-title");
    private static final By CLOSE_BUTTON = By.cssSelector(".product-detail-card .modal-close");
    private static final By LABELS = By.cssSelector(".product-detail-card .address-preview-label");
    private static final By SECTION_TITLES = By.cssSelector(".product-detail-card .address-card-city");

    public ProductDetailModalComponent(WebDriver driver) {
        super(driver, ROOT);
    }

    @Override
    protected String pageName() {
        return "Product Offer Details Modal";
    }

    public String title() {
        return getText(TITLE);
    }

    /**
     * Modaldaki tum etiketler — ACC-007'nin sart kostugu alanlarin varligini dogrulamak icin.
     *
     * <p>Sonlarindaki iki nokta temizlenir ({@code "Product Offer Name:"} → {@code "Product Offer Name"}).
     */
    public List<String> fieldLabels() {
        List<String> labels = new ArrayList<>();
        for (WebElement label : findAll(LABELS)) {
            labels.add(label.getText().trim().replaceAll(":$", ""));
        }
        for (WebElement section : findAll(SECTION_TITLES)) {
            labels.add(section.getText().trim().replaceAll(":$", ""));
        }
        return labels;
    }

    /** Modalda "Service Address" bolumu goruntuleniyor mu (ACC-007). */
    public boolean hasServiceAddressSection() {
        return getText(ROOT).contains("Service Address");
    }

    /** ACC-008: carpi ikonuyla kapatir. */
    public void close() {
        click(CLOSE_BUTTON);
    }

    public boolean isOpen() {
        return isDisplayed(ROOT);
    }

    public void waitUntilClosed() {
        wait.until(AppConditions.absentFromDom(ROOT));
    }
}
