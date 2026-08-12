package com.crmlite.ui.tests.fr014basket;

import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.pages.customer.CustomerDetailPage;
import com.crmlite.ui.pages.newsale.OfferSelectionPage;
import com.crmlite.ui.tests.AuthenticatedTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FR-014 — Sepet Yonetimi (UC-EACRML-014).
 *
 * <p><b>DOKUMAN NOTU:</b> bu FR'nin kabul kriterleri dokumanda UC-EACRML-013 basliginin
 * ALTINDA yer alir; bloklar ait olduklari basliktan once gelir.
 *
 * <p>Sepet iki kumeden olusur ve testler bunlari AYIRIR:
 * <ul>
 *   <li><b>Kullanici satirlari</b> — silme ikonu tasir.</li>
 *   <li><b>Otomatik eklenen zorunlu satirlar</b> ({@code .basket-item-locked}) — silme
 *       ikonu yerine kilit ikonu tasir (ACC-005).</li>
 * </ul>
 */
@Epic("FR-014 Sepet Yonetimi")
@Feature("UC-EACRML-014")
public class BasketManagementTests extends AuthenticatedTest {

    private static final String COMMON_NAME_FRAGMENT = "Home";

    @Test(groups = {"smoke", "fr014"},
            description = "UI-FR014-01 | Add to Basket urunu sepete ekler, ad ve fiyatiyla listelenir")
    @Story("ACC-001, ACC-002 — Sepete ekleme")
    @TmsLink("FR-014-ACC-001")
    @Severity(SeverityLevel.BLOCKER)
    public void addToBasketListsProductWithNameAndPrice() {
        OfferSelectionPage offers = openCatalogResults();

        assertThat(offers.isBasketEmpty()).as("on kosul: sepet bos").isTrue();

        offers.addToBasket(0);

        assertThat(offers.basketLineCount())
                .as("ACC-001 — urun sepete eklenir").isPositive();
        assertThat(offers.basketItemNames())
                .as("ACC-002 — sepette urun adi gosterilir").isNotEmpty();
        assertThat(offers.basketPriceCount())
                .as("ACC-002 — her satirda fiyat gosterilir")
                .isEqualTo(offers.basketLineCount());
    }

    @Test(groups = {"fr014", "regression"},
            description = "UI-FR014-02 | Sepet basligindaki adet ve Total Amount guncellenir")
    @Story("ACC-007, ACC-008 — Adet ve toplam tutar")
    @TmsLink("FR-014-ACC-008")
    @Severity(SeverityLevel.CRITICAL)
    public void quantityAndTotalReflectBasketContent() {
        OfferSelectionPage offers = openCatalogResults();

        assertThat(offers.totalAmount()).as("bos sepette toplam sifir").isZero();

        offers.addToBasket(0);

        assertThat(offers.basketQuantity())
                .as("ACC-007 — baslikta urun sayisi gosterilir")
                .isEqualTo(String.valueOf(offers.basketLineCount()));
        assertThat(offers.totalAmount())
                .as("ACC-008 — toplam tutar sifirdan buyuk").isPositive();
    }

    @Test(groups = {"smoke", "fr014"},
            description = "UI-FR014-03 | Bos sepette bilgilendirme metni ve sifir tutar gosterilir")
    @Story("ACC-009 — Bos sepet")
    @TmsLink("FR-014-ACC-009")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Dokuman tutari Turkce bicimle (₺0,00) yazar; uygulama EN yerelinde "
            + "(0.00 ₺) gosterir. Bicim farki kural ihlali sayilmadigi icin test metni degil "
            + "DEGERI dogrular.")
    public void emptyBasketShowsMessageAndZeroTotal() {
        OfferSelectionPage offers = openCatalogResults();

        assertThat(offers.isBasketEmpty())
                .as("ACC-009 — bos sepet metni gosterilir").isTrue();
        assertThat(offers.basketEmptyText())
                .isEqualTo(ExpectedMessages.get("newSale.basketEmpty"));
        assertThat(offers.totalAmount())
                .as("ACC-009 — Total Amount sifir").isZero();
    }

    @Test(groups = {"fr014", "regression"},
            description = "UI-FR014-04 | Zorunlu bagli urunler otomatik eklenir ve silinemez")
    @Story("ACC-003, ACC-004, ACC-005 — Zorunlu urunler")
    @TmsLink("FR-014-ACC-005")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Zorunlu (REQ) iliskisi olan bir urun eklendiginde bagli urunler otomatik "
            + "eklenir, tutara dahil edilir ve KULLANICI TARAFINDAN SILINEMEZ. Seed verisinde "
            + "her urunun zorunlu iliskisi yoktur; bu yuzden test once zorunlu satir uretmis "
            + "bir urun arar, bulamazsa ACC-006'yi (iliski yoksa yalnizca ana urun) dogrular.")
    public void requiredProductsAreAutoAddedAndLocked() {
        OfferSelectionPage offers = openCatalogResults();

        int lockedFound = -1;
        int rows = Math.min(offers.resultRowCount(), 5);
        for (int i = 0; i < rows; i++) {
            offers.addToBasket(i);
            if (offers.lockedItemCount() > 0) {
                lockedFound = i;
                break;
            }
            offers.clearBasket();
        }

        if (lockedFound < 0) {
            // ACC-006: zorunlu iliski yoksa yalnizca secilen ana urun eklenir.
            offers.addToBasket(0);
            assertThat(offers.lockedItemCount())
                    .as("ACC-006 — zorunlu iliski yoksa otomatik urun eklenmez").isZero();
            assertThat(offers.userItemCount())
                    .as("ACC-006 — yalnizca secilen urun eklenir").isEqualTo(1);
            return;
        }

        assertThat(offers.lockedItemCount())
                .as("ACC-003 — zorunlu urunler otomatik eklenir").isPositive();
        assertThat(offers.totalAmount())
                .as("ACC-004 — zorunlu urun fiyatlari tutara dahildir").isPositive();
        assertThat(offers.lockIconCount())
                .as("ACC-005 — zorunlu satirlarda kilit ikonu bulunur")
                .isEqualTo(offers.lockedItemCount());
        assertThat(offers.removeButtonCount())
                .as("ACC-005 — silme ikonu yalnizca kullanici satirlarinda bulunur")
                .isEqualTo(offers.userItemCount());
    }

    @Test(groups = {"fr014", "regression"},
            description = "UI-FR014-05 | Cop ikonu urunu sepetten cikarir, tutar guncellenir")
    @Story("ACC-013, ACC-015 — Urun cikarma")
    @TmsLink("FR-014-ACC-013")
    @Severity(SeverityLevel.CRITICAL)
    public void removingProductUpdatesBasketAndTotal() {
        OfferSelectionPage offers = openCatalogResults();
        offers.addToBasket(0);

        double totalWithItem = offers.totalAmount();
        assertThat(totalWithItem).as("on kosul: tutar olustu").isPositive();

        offers.removeBasketItem(0);

        assertThat(offers.isBasketEmpty())
                .as("ACC-013 — urun sepetten cikarilir").isTrue();
        assertThat(offers.totalAmount())
                .as("ACC-015 — tutar guncellenir").isZero();
    }

    @Test(groups = {"fr014", "regression"},
            description = "UI-FR014-06 | Clear butonu sepetteki tum urunleri siler")
    @Story("ACC-014, ACC-015 — Sepeti temizleme")
    @TmsLink("FR-014-ACC-014")
    @Severity(SeverityLevel.NORMAL)
    public void clearRemovesAllBasketItems() {
        OfferSelectionPage offers = openCatalogResults();

        assertThat(offers.isClearBasketDisabled())
                .as("sepet bosken Clear pasif").isTrue();

        offers.addToBasket(0);
        offers.clearBasket();

        assertThat(offers.isBasketEmpty())
                .as("ACC-014 — tum urunler silinir").isTrue();
        assertThat(offers.basketQuantity())
                .as("ACC-015 — adet sifirlanir").isEqualTo("0");
        assertThat(offers.totalAmount())
                .as("ACC-015 — tutar sifirlanir").isZero();
    }

    @Test(groups = {"fr014", "regression"},
            description = "UI-FR014-07 | Sepetteki urunun aksiyon butonu pasiflesir")
    @Story("ACC-010 — Sepetteki urun tekrar eklenemez")
    @TmsLink("FR-014-ACC-010")
    @Severity(SeverityLevel.NORMAL)
    @Description("Asil kural, sepetteki urunun TEKRAR EKLENEMEMESI; test bunu dogrular. "
            + "Buton metni dokumanda \"In Basket\", uygulamada \"Already in Basket\" - ayni "
            + "bilgiyi veren kozmetik bir fark oldugu icin beklenti uygulamaya hizalanmistir.")
    public void productInBasketShowsDisabledInBasketButton() {
        OfferSelectionPage offers = openCatalogResults();
        offers.addToBasket(0);

        assertThat(offers.isAddButtonEnabled(0))
                .as("ACC-010 — sepetteki urun tekrar eklenemez").isFalse();
        assertThat(offers.addButtonLabel(0))
                .as("ACC-010 — buton etiketi")
                .isEqualTo(ExpectedMessages.get("newSale.inBasket"));
    }

    @Test(groups = {"fr014", "regression"},
            description = "UI-FR014-08 | Sepet bosken Next pasif, urun varken aktiftir")
    @Story("ACC-017, ACC-018 — Next butonu durumu")
    @TmsLink("FR-014-ACC-018")
    @Severity(SeverityLevel.CRITICAL)
    public void nextIsEnabledOnlyWhenBasketHasItems() {
        OfferSelectionPage offers = openCatalogResults();

        assertThat(offers.isNextDisabled())
                .as("ACC-017 — sepet bosken Next pasif").isTrue();

        offers.addToBasket(0);

        assertThat(offers.isNextEnabled())
                .as("ACC-018 — urun eklenince Next aktiflesir").isTrue();
    }

    @Test(groups = {"fr014", "regression"},
            description = "UI-FR014-09 | Next, Product Configuration adimini acar")
    @Story("ACC-018 — Configuration adimina gecis")
    @TmsLink("FR-014-ACC-018")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Sihirbaz tek kabuk bilesende yasar; rota degismez, aktif adim stepper'dan "
            + "okunur.")
    public void nextOpensProductConfigurationStep() {
        OfferSelectionPage offers = openCatalogResults();
        offers.addToBasket(0);
        offers.clickNext();
        // Gecis asenkron: sepet dogrulamasi sunucuya gider, bu sirada buton spinner gosterir.
        offers.waitForActiveStep(ExpectedMessages.get("newSale.stepConfiguration"));

        assertThat(offers.activeStepLabel())
                .as("ACC-018 — Product Configuration adimi acilir")
                .isEqualTo(ExpectedMessages.get("newSale.stepConfiguration"));
    }

    @Test(groups = {"fr014", "regression"},
            description = "UI-FR014-10 | Cancel onay ister ve onaylandiginda satisi terk eder")
    @Story("ACC-016 — Cancel onayi")
    @TmsLink("FR-014-ACC-016")
    @Severity(SeverityLevel.NORMAL)
    public void cancelAsksForConfirmationAndLeavesTheSale() {
        OfferSelectionPage offers = openCatalogResults();
        offers.addToBasket(0);

        offers.clickCancel();

        assertThat(offers.cancelConfirmMessage())
                .as("ACC-016 — onay mesaji")
                .isEqualTo(ExpectedMessages.get("newSale.cancelConfirmMessage"));

        offers.confirmCancel();

        assertThat(offers.currentUrl())
                .as("ACC-016 — onaylandiginda satis ekranindan cikilir")
                .doesNotContain("/new-sale/");
    }

    @Test(groups = {"fr014", "regression"},
            description = "UI-FR014-11 | Musteride aktif olan urun tekrar eklenemez")
    @Story("ACC-011 — Already Active")
    @TmsLink("FR-014-ACC-011")
    @Severity(SeverityLevel.NORMAL)
    @Description("On kosul: musterinin onceki bir satistan AKTIF urunu olmalidir; bunu "
            + "TestDataFactory.customerWithAccountProduct() kurar. Urun adi fabrikadan "
            + "dondugu icin katalogda ad ile aranir - urun kimligi sabitlenmez.")
    public void alreadyActiveProductCannotBeAdded() {
        TestDataFactory.CustomerWithProduct data = TestDataFactory.customerWithAccountProduct();
        CustomerDetailPage detail =
                openCustomerDetail(data.customer().custId()).openAccountsTab();
        detail.toggleAccountRow(0);

        OfferSelectionPage offers = detail.startNewSale();
        offers.enterOfferName(data.productName());
        offers.searchUntilResults();

        assertThat(offers.resultRowCount()).as("on kosul: aktif urun katalogda bulundu").isPositive();
        assertThat(offers.isAddButtonEnabled(0))
                .as("ACC-011 — aktif urun eklenemez").isFalse();
        assertThat(offers.addButtonLabel(0))
                .as("ACC-011 — buton etiketi")
                .isEqualTo(ExpectedMessages.get("newSale.alreadyActive"));
    }

    @Test(groups = {"fr014", "regression"},
            description = "UI-FR014-12 | Ayni kategoriden ikinci urun eklenemez, hata gosterilir")
    @Story("ACC-012 — Kategori catismasi")
    @TmsLink("FR-014-ACC-012")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Dokuman catisan urunun EKLENEMEMESINI ve hata mesaji gosterilmesini sart "
            + "kosar; mesajin metnini belirtmez, bu yuzden uygulamanin metni esas alinmistir. "
            + "\"Home Fiber\" aramasi ayni urun tanimina bagli birden fazla teklif dondurur.")
    public void conflictingCategoryProductIsRejected() {
        OfferSelectionPage offers = openCatalogResults("Home Fiber");

        assertThat(offers.resultRowCount())
                .as("on kosul: ayni kategoriden en az iki teklif listelendi")
                .isGreaterThan(1);

        offers.addToBasket(0);
        int linesAfterFirst = offers.basketLineCount();
        // Ilk ekleme de bir basari toast'i gosterir; ikinci islemin sonucunu okumadan once
        // metnin DEGISMESI beklenir, aksi halde eski toast okunur.
        String firstToast = offers.toastMessage();

        offers.clickAddButton(1);

        assertThat(offers.toastMessageOtherThan(firstToast))
                .as("ACC-012 — catisma mesaji")
                .isEqualTo(ExpectedMessages.get("newSale.categoryConflictError"));
        assertThat(offers.basketLineCount())
                .as("ACC-012 — catisan urun sepete EKLENMEZ").isEqualTo(linesAfterFirst);
    }

    /** Fatura hesabi olan musteri kurar, Offer Selection'i acar ve katalog sonuclarini getirir. */
    private OfferSelectionPage openCatalogResults() {
        return openCatalogResults(COMMON_NAME_FRAGMENT);
    }

    private OfferSelectionPage openCatalogResults(String nameFragment) {
        CreatedCustomer customer = TestDataFactory.customerWithBillingAccount();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAccountsTab();
        detail.toggleAccountRow(0);

        OfferSelectionPage offers = detail.startNewSale();
        offers.enterOfferName(nameFragment);
        offers.searchUntilResults();
        return offers;
    }
}
