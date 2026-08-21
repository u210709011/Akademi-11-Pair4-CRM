package com.crmlite.ui.tests.fr002search;

import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.pages.components.PaginationComponent;
import com.crmlite.ui.pages.components.ResultsTableComponent;
import com.crmlite.ui.pages.customer.SearchCustomerPage;
import com.crmlite.ui.tests.AuthenticatedTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FR-002 ACC-007 / ACC-008 — Sayfalama ve siralama.
 *
 * <p>Bu senaryolar 10'dan fazla eslesen kayit gerektirir (front-end sayfa boyutu 10).
 * On kosul API ile kurulur: ayni islemi UI'dan yapmak 12 kez sihirbaz doldurmak demektir.
 */
@Epic("FR-002 Musteri Arama ve Goruntuleme")
@Feature("UC-EACRML-002")
public class SearchPaginationSortTests extends AuthenticatedTest {

    private static final int PAGE_SIZE = 10;
    private static final int DATASET_SIZE = 12;

    private TestDataFactory.SearchDataSet dataSet;

    @BeforeClass(alwaysRun = true)
    public void createDataSet() {
        dataSet = TestDataFactory.searchDataSet(DATASET_SIZE);
    }

    private SearchCustomerPage searchForDataSet() {
        SearchCustomerPage page = openSearchCustomer();
        page.enterLastName(dataSet.sharedLastName()).search();
        page.resultsTable().waitForRows();
        return page;
    }

    @Test(groups = {"fr002", "regression"},
            description = "UI-FR002-11 | Ilk sayfada en fazla 10 kayit gosterilir")
    @Story("ACC-007 — Ilk 10 kayit + sayfalama")
    @TmsLink("FR-002-ACC-007")
    @Severity(SeverityLevel.CRITICAL)
    public void firstPageShowsTenRecords() {
        SearchCustomerPage page = searchForDataSet();

        assertThat(page.resultsTable().rowCount())
                .as("ACC-007 — ilk sayfada 10 kayit").isEqualTo(PAGE_SIZE);
        assertThat(page.pagination().isVisible())
                .as("10'dan fazla sonuc oldugundan sayfalama gorunur").isTrue();
    }

    @Test(groups = {"fr002", "regression"},
            description = "UI-FR002-12 | Sonraki sayfa kalan kayitlari gosterir")
    @Story("ACC-007 — Ilk 10 kayit + sayfalama")
    @TmsLink("FR-002-ACC-007")
    @Severity(SeverityLevel.NORMAL)
    public void secondPageShowsRemainingRecords() {
        SearchCustomerPage page = searchForDataSet();
        List<String> firstPageIds = new ArrayList<>(page.resultsTable().customerIds());

        PaginationComponent pagination = page.pagination();
        assertThat(pagination.isPreviousEnabled())
                .as("ilk sayfada 'onceki' oku pasif").isFalse();

        pagination.goToNextPage();
        page.resultsTable().waitForRows();

        List<String> secondPageIds = page.resultsTable().customerIds();

        assertThat(secondPageIds).as("ikinci sayfada kalan kayitlar").isNotEmpty();
        assertThat(secondPageIds).as("sayfalar ayni kaydi tekrar etmemeli")
                .doesNotContainAnyElementsOf(firstPageIds);
        assertThat(pagination.activePageNumber()).isEqualTo(2);
    }

    @Test(groups = {"fr002", "regression"},
            description = "UI-FR002-13, -14 | Kolon basligina tiklama artan, tekrar tiklama azalan siralar")
    @Story("ACC-008 — Kolon basligina tiklayinca siralama")
    @TmsLink("FR-002-ACC-008")
    @Severity(SeverityLevel.NORMAL)
    @Description("Ilk tik A-Z (artan), ikinci tik tersine cevirir. Siralama tum sonuc kumesi "
            + "uzerinde uygulanir (backend destekli).")
    public void columnSortingTogglesDirection() {
        SearchCustomerPage page = searchForDataSet();
        ResultsTableComponent table = page.resultsTable();

        table.sortBy(ResultsTableComponent.Column.FIRST_NAME);
        table.waitForRows();
        List<String> ascending = table.columnValues(ResultsTableComponent.Column.FIRST_NAME);

        assertThat(ascending).as("ACC-008 — ilk tik artan (A-Z)").isSorted();

        table.sortBy(ResultsTableComponent.Column.FIRST_NAME);
        table.waitForRows();
        List<String> descending = table.columnValues(ResultsTableComponent.Column.FIRST_NAME);

        assertThat(descending).as("ACC-008 — ikinci tik azalan (Z-A)")
                .isSortedAccordingTo(java.util.Comparator.reverseOrder());
    }
}
