package com.crmlite.ui.tests.fr005address;

import com.crmlite.ui.data.ExpectedMessages;
import com.crmlite.ui.data.api.AddressApi;
import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.pages.components.AddressCardComponent;
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
 * FR-005 — Birincil adres kurallari (ACC-006, ACC-007, ACC-009).
 */
@Epic("FR-005 Adres Yonetimi")
@Feature("UC-EACRML-005")
public class PrimaryAddressTests extends AuthenticatedTest {

    @Test(groups = {"fr005", "regression"},
            description = "UI-FR005-08 | Tek adres otomatik birincildir ve degistirilemez")
    @Story("ACC-007 — Tek adres otomatik birincil")
    @TmsLink("FR-005-ACC-007")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Onboarding'de girilen tek adres sunucu tarafinda birincil olur; "
            + "tek adres varken 'Set as Primary' secenegi pasif kalmalidir.")
    public void singleAddressIsAutomaticallyPrimaryAndImmutable() {
        CreatedCustomer customer = TestDataFactory.simpleCustomer();
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAddressTab();

        assertThat(detail.addressCount()).as("tek adres").isEqualTo(1);

        AddressCardComponent card = detail.addressCard(0);
        assertThat(card.isPrimary()).as("ACC-007 — otomatik birincil").isTrue();
        assertThat(card.tagText()).contains(ExpectedMessages.get("detail.primaryBadge"));
        assertThat(card.isSetAsPrimaryEnabled())
                .as("ACC-007 — birincillik degistirilemez").isFalse();
    }

    @Test(groups = {"fr005", "regression"},
            description = "UI-FR005-07 | Set as Primary ile birincil adres degistirilir, tek birincil kalir")
    @Story("ACC-006 — Birincil adres secilebilir")
    @TmsLink("FR-005-ACC-006")
    @Severity(SeverityLevel.CRITICAL)
    public void settingAnotherAddressAsPrimaryMovesTheFlag() {
        CreatedCustomer customer = TestDataFactory.customerWithAddresses(2);
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAddressTab();

        int originalPrimary = detail.primaryAddressIndex();
        int target = originalPrimary == 0 ? 1 : 0;

        // Adres KIMLIGI indeksle degil, adres satiriyla takip edilir: uygulama
        // birincil adresi listenin basina tasidigi icin islem sonrasi indeksler kayar
        // (API'de dogrulandi: [96=primary, 97] -> [97=primary, 96]).
        // Indekse guvenen bir dogrulama, dogru davranisi yanlis raporlar.
        String targetAddressLine = detail.addressCard(target).addressLine();

        detail.addressCard(target).setAsPrimary();

        // Sunucu tarafi yerlesene kadar bekle. Eski adresin bayragi aninda kalkmadigi
        // icin bu araliktaki bir sayfa yuklemesi IKI karti da "Primary" gosteriyor
        // (ekran goruntusuyla dogrulandi; README'de urun bulgusu olarak kayitli).
        // Bu bir assertion degil senkronizasyondur; kalici tutarsizlikta acikca patlar.
        AddressApi.awaitSinglePrimary(customer.custId());

        CustomerDetailPage refreshed = openCustomerDetail(customer.custId()).openAddressTab();

        assertThat(refreshed.addressCards())
                .as("ACC-006 — secilen adres (%s) birincil oldu", targetAddressLine)
                .filteredOn(card -> card.addressLine().equals(targetAddressLine))
                .hasSize(1)   // eslesme yoksa allMatch bos listede sessizce gecerdi
                .allMatch(AddressCardComponent::isPrimary);
        assertThat(refreshed.addressCards().stream().filter(AddressCardComponent::isPrimary).count())
                .as("yalnizca tek birincil adres olabilir").isEqualTo(1);
    }

    @Test(groups = {"fr005", "regression"},
            description = "UI-FR005-09 | Birincil adres silinemez, uyari gosterilir")
    @Story("ACC-009 — Birincil adres silinemez")
    @TmsLink("FR-005-ACC-009")
    @Severity(SeverityLevel.CRITICAL)
    @Description("12.08.2026: ozellik uygulandi, test acildi. Dokuman ACC-009 silme "
            + "secenegininin pasif olmasini ve ipucu metninin sebebi aciklamasini sart kosar. "
            + "Uygulamada birincil adres icin HICBIR koruma yok: menudeki Delete butonunda "
            + "[disabled] baglamasi bulunmuyor, openDeleteAddressConfirm() birincil adresi "
            + "ayirt etmiyor ve \"Primary address cannot be deleted.\" metni frontend'de hic "
            + "tanimli degil. Koruma varsa yalnizca backend tarafindadir; dokumanin istedigi "
            + "UI davranisi karsilanmiyor.\n"
            + "10.08.2026: pull oncesi ve sonrasi surumler karsilastirildi, buton her iki "
            + "surumde de ayni - bu bir regresyon degil, bastan beri var olan bir eksik.")
    public void primaryAddressCannotBeDeleted() {
        CreatedCustomer customer = TestDataFactory.customerWithAddresses(2);
        CustomerDetailPage detail = openCustomerDetail(customer.custId()).openAddressTab();

        int before = detail.addressCount();
        int primaryIndex = detail.primaryAddressIndex();
        assertThat(primaryIndex).as("birincil adres bulunmali").isNotNegative();

        AddressCardComponent card = detail.addressCard(primaryIndex);

        assertThat(card.isDeleteEnabled())
                .as("ACC-009 — birincil adreste Delete pasif olmalidir").isFalse();
        assertThat(card.deleteTooltip())
                .as("ACC-009 — sebep aciklanmalidir")
                .isEqualTo(ExpectedMessages.get("detail.primaryAddressCannotDelete"));
        assertThat(detail.addressCount())
                .as("ACC-009 — liste degismeden kalir").isEqualTo(before);
    }
}
