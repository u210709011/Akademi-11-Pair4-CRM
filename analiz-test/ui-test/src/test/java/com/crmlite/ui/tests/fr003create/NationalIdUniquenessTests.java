package com.crmlite.ui.tests.fr003create;

import com.crmlite.ui.data.api.TestDataFactory;
import com.crmlite.ui.data.builder.CustomerBuilder;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.data.model.CustomerData;
import com.crmlite.ui.pages.customer.create.CreateCustomerPage;
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
 * FR-003 ACC-004/ACC-005 — Nationality ID tekillik kontrolu.
 *
 * <p><b>Kapsam disi:</b> ACC-006 ve ACC-007 (KPS dogrulamasi) otomatize edilemez.
 * Back-end'deki {@code FakeIdentityVerificationServiceImpl} her kimligi dogruladigi
 * icin basarisiz bir KPS senaryosu uretmek mumkun degildir.
 */
@Epic("FR-003 Musteri Olusturma")
@Feature("UC-EACRML-003")
public class NationalIdUniquenessTests extends AuthenticatedTest {

    @Test(groups = {"fr003", "regression"},
            description = "UI-FR003-07 | Mevcut Nationality ID ile ilerlenemez ve uyari gosterilir")
    @Story("ACC-004, ACC-005 — Ayni Nationality ID varsa ilerlenemez")
    @TmsLink("FR-003-ACC-005")
    @Severity(SeverityLevel.CRITICAL)
    @Description("On kosul musteri API ile kurulur, ayni NAT ID sihirbaza UI'dan girilir. "
            + "Sistem uyari gostermeli ve demografik adimda tutmalidir.")
    public void existingNationalIdBlocksProgress() {
        CreatedCustomer existing = TestDataFactory.simpleCustomer();

        CreateCustomerPage wizard = openCreateCustomer();
        CustomerData data = CustomerBuilder.aValidCustomer()
                .withNationalId(existing.nationalId())
                .build();

        wizard.demographicStep().fillRequired(
                data.individual().firstName(), data.individual().lastName(),
                data.individual().birthDate(), String.valueOf(data.individual().genderId()),
                data.individual().nationalId());

        wizard.clickNext();

        assertThat(wizard.hasErrorBanner())
                .as("ACC-005 — tekillik uyarisi gosterilmeli").isTrue();
        assertThat(wizard.demographicStep().isAt())
                .as("ACC-005 — demografik adimda kalinmali").isTrue();
        assertThat(wizard.isStepLocked(CreateCustomerPage.Step.ADDRESS))
                .as("ACC-005 — adres adimi kilitli kalmali").isTrue();
    }
}
