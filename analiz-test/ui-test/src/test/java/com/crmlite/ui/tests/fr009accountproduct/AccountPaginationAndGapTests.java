package com.crmlite.ui.tests.fr009accountproduct;

import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.api.AddressApi;
import com.crmlite.ui.data.api.BillingAccountApi;
import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.pages.customer.CustomerDetailPage;
import com.crmlite.ui.tests.AuthenticatedTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FR-009 ACC-009 (sayfalama) ve ACC-001'in bos durum davranisi.
 *
 * <p>ACC-001'in ikinci yarisi ("hesap yoksa mesaj gosterilmeli") once uygulamada yoktu ve
 * ilgili test {@code documented-gap} olarak kirmizi birakilmisti; 10.08.2026'da eklendi.
 * Bos durumda <b>tablo hic render edilmez</b> — bu yuzden sayfa acilisinda tablo degil
 * panel beklenir (bkz. {@code CustomerDetailPage.openAccountsTab}).
 */
@Epic("FR-009 Fatura Hesabi ve Bagli Urun Goruntuleme")
@Feature("UC-EACRML-009")
public class AccountPaginationAndGapTests extends AuthenticatedTest {

    /** ACC-009: ilk sayfada gosterilecek kayit sayisi. */
    private static final int PAGE_SIZE = 5;

    @Test(groups = {"fr009", "regression"},
            description = "UI-FR009-11 | Bes hesapta sayfalama gorunmez")
    @Story("ACC-009 — Ilk 5 kayit")
    @TmsLink("FR-009-ACC-009")
    @Severity(SeverityLevel.NORMAL)
    public void paginationIsHiddenWhenAccountsFitOnePage() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        createBillingAccounts(customer, PAGE_SIZE);

        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        assertThat(detail.accountCount())
                .as("ACC-009 — bes hesabin tamami ilk sayfada gosterilir").isEqualTo(PAGE_SIZE);
        assertThat(detail.hasAccountsPagination())
                .as("ACC-009 — tek sayfa varken sayfalama gosterilmez").isFalse();
    }

    @Test(groups = {"fr009", "regression"},
            description = "UI-FR009-12 | Besten fazla hesapta ilk 5 gosterilir, kalani sayfalanir")
    @Story("ACC-009 — Sayfalama")
    @TmsLink("FR-009-ACC-009")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Dokumanin alternatif senaryosu: musterinin hesap sayisi 5'ten fazlaysa "
            + "sistem ilk 5 kaydi gosterir, kalani sayfalama ile goruntulenir.")
    public void firstFiveShownAndRestPaginated() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        createBillingAccounts(customer, PAGE_SIZE + 2);

        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        assertThat(detail.accountCount())
                .as("ACC-009 — ilk sayfada yalnizca bes kayit").isEqualTo(PAGE_SIZE);
        assertThat(detail.hasAccountsPagination())
                .as("ACC-009 — kalan kayitlar icin sayfalama gosterilir").isTrue();
        assertThat(detail.accountsRangeLabel())
                .as("ACC-009 — aralik etiketi toplam kayit sayisini icerir")
                .contains(String.valueOf(PAGE_SIZE + 2));
    }

    @Test(groups = {"fr009", "regression"},
            description = "UI-FR009-13 | Fatura hesabi yokken bilgilendirme mesaji gosterilir")
    @Story("ACC-001 — Hesap bulunmamasi")
    @TmsLink("FR-009-ACC-001")
    @Severity(SeverityLevel.NORMAL)
    @Description("Dokuman ACC-001 hesap yoksa tablo yerine 'There are no billing accounts yet.' "
            + "gosterilmesini sart kosar. Onceden uygulamada boyle bir metin yoktu ve bu test "
            + "documented-gap olarak kirmizi birakilmisti; 10.08.2026'da eklendi ve yesile dondu.")
    public void emptyStateMessageIsShownWhenNoBillingAccounts() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();

        assertThat(detail.accountCount())
                .as("on kosul: varsayilan CUST_ACCT bu tabloda listelenmez").isZero();
        assertThat(detail.pageText())
                .as("ACC-001 — hesap yokken bilgilendirme mesaji gosterilmelidir")
                .contains(ExpectedMessages.get("detail.noBillingAccounts"));
    }

    /** Musteriye birincil adresine bagli {@code count} adet fatura hesabi acar. */
    private void createBillingAccounts(CreatedCustomer customer, int count) {
        long addressId = AddressApi.primaryAddress(customer.custId()).id();
        for (int i = 0; i < count; i++) {
            BillingAccountApi.create(customer.custId(), addressId, "Sayfalama Hesabi " + (i + 1));
        }
    }
}
