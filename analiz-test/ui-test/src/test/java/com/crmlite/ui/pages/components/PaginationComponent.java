package com.crmlite.ui.pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * FR-002 ACC-007 sayfalama bileseni.
 *
 * <p>Front-end sayfa boyutu 10'dur ({@code PAGE_SIZE = 10}), dolayisiyla sayfalama
 * kontrolu yalnizca 10'dan fazla sonuc varken DOM'a eklenir ({@code totalPages() > 1}).
 * Bu yuzden {@link #isVisible()} kontrolu, sayfalama testlerinde on kosul sayilir.
 */
public class PaginationComponent extends BaseComponent {

    private static final By ROOT = By.cssSelector(".pagination");

    private static final By RANGE_LABEL = By.cssSelector(".pagination .pagination-range");
    private static final By PAGE_BUTTONS = By.cssSelector(".pagination .pagination-page");
    private static final By ACTIVE_PAGE = By.cssSelector(".pagination .pagination-page.active");
    private static final By ARROWS = By.cssSelector(".pagination .pagination-arrow");

    // Oklar ve sayfa butonlari ayni ebeveynin <button> kardesleridir; bu yuzden
    // :nth-of-type(2) ikinci oku DEGIL, ikinci butonu secer. first/last-of-type dogru olanidir.
    private static final By PREVIOUS_ARROW = By.cssSelector(".pagination .pagination-controls button:first-of-type");
    private static final By NEXT_ARROW = By.cssSelector(".pagination .pagination-controls button:last-of-type");

    public PaginationComponent(WebDriver driver) {
        super(driver, ROOT);
    }

    @Override
    protected String pageName() {
        return "Sayfalama";
    }

    /** Ornegin "1-10 / 23" benzeri aralik etiketi. */
    public String rangeLabel() {
        return getText(RANGE_LABEL);
    }

    public int pageCount() {
        return findAll(PAGE_BUTTONS).size();
    }

    /** Aktif sayfa numarasi (kullaniciya gorunen 1 tabanli deger). */
    public int activePageNumber() {
        return Integer.parseInt(getText(ACTIVE_PAGE));
    }

    public void goToPage(int pageNumber) {
        click(By.xpath(String.format(
                "//div[contains(@class,'pagination')]//button[contains(@class,'pagination-page')]"
                        + "[normalize-space()='%d']",
                pageNumber)));
    }

    public void goToNextPage() {
        click(NEXT_ARROW);
    }

    public void goToPreviousPage() {
        click(PREVIOUS_ARROW);
    }

    /** Ilk sayfadayken "onceki" oku pasif olmalidir. */
    public boolean isPreviousEnabled() {
        return findAll(ARROWS).get(0).isEnabled();
    }

    /** Son sayfadayken "sonraki" oku pasif olmalidir. */
    public boolean isNextEnabled() {
        return findAll(ARROWS).get(1).isEnabled();
    }
}
