package com.crmlite.ui.pages.components;

import com.crmlite.ui.core.waits.AppConditions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * FR-002 arama sonuc tablosu.
 *
 * <p>Kolon sirasi bilerek sabitlendi: ACC-006 kolonlarin <b>hangi sirayla</b>
 * gosterilecegini de tanimliyor (Customer ID, First, Second, Last, Role, NAT ID).
 */
public class ResultsTableComponent extends BaseComponent {

    private static final By ROOT = By.cssSelector("table.results-table");

    private static final By HEADERS = By.cssSelector("table.results-table thead th");
    private static final By ROWS = By.cssSelector("table.results-table tbody tr");
    private static final By CUSTOMER_ID_LINKS = By.cssSelector("table.results-table tbody .customer-id-link");

    /** ACC-006'daki kolon sirasi. */
    public enum Column {
        CUSTOMER_ID(0),
        FIRST_NAME(1),
        SECOND_NAME(2),
        LAST_NAME(3),
        ROLE(4),
        NAT_ID(5);

        private final int index;

        Column(int index) {
            this.index = index;
        }

        public int index() {
            return index;
        }
    }

    public ResultsTableComponent(WebDriver driver) {
        super(driver, ROOT);
    }

    @Override
    protected String pageName() {
        return "Arama Sonuc Tablosu";
    }

    // --- Kolonlar (ACC-006) ---

    public List<String> headerTexts() {
        List<String> headers = new ArrayList<>();
        for (WebElement header : findAll(HEADERS)) {
            // Baslikta siralama gostergesi (▲/▼) da bulunuyor; ayiklanir.
            headers.add(header.getText().replace("▲", "").replace("▼", "").trim());
        }
        return headers;
    }

    public int columnCount() {
        return findAll(HEADERS).size();
    }

    // --- Satirlar (ACC-005, ACC-007) ---

    public int rowCount() {
        return findAll(ROWS).size();
    }

    public boolean hasAnyRow() {
        return isDisplayedAfterWait(ROOT) && rowCount() > 0;
    }

    public void waitForRows() {
        wait.until(AppConditions.hasAnyRow(ROWS));
    }

    /** Belirli bir satir/kolon hucresinin metni (0 tabanli satir indeksi). */
    public String cellText(int rowIndex, Column column) {
        return getText(cellLocator(rowIndex, column));
    }

    /** Bir kolonun tum degerleri — siralama (ACC-008) dogrulamalarinda kullanilir. */
    public List<String> columnValues(Column column) {
        List<String> values = new ArrayList<>();
        for (WebElement cell : findAll(By.cssSelector(String.format(
                "table.results-table tbody tr td:nth-child(%d)", column.index() + 1)))) {
            values.add(cell.getText().trim());
        }
        return values;
    }

    public List<String> customerIds() {
        List<String> ids = new ArrayList<>();
        for (WebElement link : findAll(CUSTOMER_ID_LINKS)) {
            ids.add(link.getText().trim());
        }
        return ids;
    }

    public boolean containsCustomerId(String customerId) {
        return customerIds().contains(customerId.trim());
    }

    // --- Eylemler ---

    /** ACC-009: Customer ID'ye tiklayarak musteri detayina gider. */
    public void openCustomerById(String customerId) {
        click(By.xpath(String.format(
                "//table[contains(@class,'results-table')]//button[contains(@class,'customer-id-link')]"
                        + "[normalize-space()='%s']",
                customerId)));
    }

    public void openCustomerAtRow(int rowIndex) {
        click(cellLocator(rowIndex, Column.CUSTOMER_ID));
    }

    /** ACC-008: kolon basligina tiklayarak siralama; ilk tik ASC, ikinci tik DESC. */
    public void sortBy(Column column) {
        click(headerLocator(column));
    }

    /** Siralama gostergesi: "▲" (artan) veya "▼" (azalan). */
    public String sortIndicator(Column column) {
        return getText(By.cssSelector(String.format(
                "table.results-table thead th:nth-child(%d) .sort-indicator", column.index() + 1)));
    }

    public boolean isSortedAscending(Column column) {
        return "▲".equals(sortIndicator(column));
    }

    private By cellLocator(int rowIndex, Column column) {
        return By.cssSelector(String.format(
                "table.results-table tbody tr:nth-child(%d) td:nth-child(%d)",
                rowIndex + 1, column.index() + 1));
    }

    private By headerLocator(Column column) {
        return By.cssSelector(String.format(
                "table.results-table thead th:nth-child(%d)", column.index() + 1));
    }
}
