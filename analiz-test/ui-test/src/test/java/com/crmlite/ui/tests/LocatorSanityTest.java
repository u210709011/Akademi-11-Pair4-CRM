package com.crmlite.ui.tests;

import com.crmlite.ui.core.report.AllureAttachment;
import com.crmlite.ui.pages.auth.LoginPage;
import com.crmlite.ui.pages.components.AddressCardComponent;
import com.crmlite.ui.pages.components.AddressModalComponent;
import com.crmlite.ui.pages.components.ConfirmDialogComponent;
import com.crmlite.ui.pages.components.NavbarComponent;
import com.crmlite.ui.pages.components.PaginationComponent;
import com.crmlite.ui.pages.components.ResultsTableComponent;
import com.crmlite.ui.pages.components.SidebarComponent;
import com.crmlite.ui.pages.customer.CustomerDetailPage;
import com.crmlite.ui.pages.customer.SearchCustomerPage;
import com.crmlite.ui.pages.customer.UpdateCustomerPage;
import com.crmlite.ui.pages.customer.create.AddressStep;
import com.crmlite.ui.pages.customer.create.ContactStep;
import com.crmlite.ui.pages.customer.create.CreateCustomerPage;
import com.crmlite.ui.pages.customer.create.DemographicStep;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Faz 2 kabul testi — <b>uygulama gerektirmez.</b>
 *
 * <p>Page Object'lerdeki her {@link By} locator'i bos bir sayfada calistirilir.
 * Hatali yazilmis bir CSS/XPath ifadesi {@code InvalidSelectorException} firlatir;
 * boylece bir yazim hatasi Faz 4'te saatlerce hata ayiklamak yerine burada yakalanir.
 *
 * <p><b>Kapsam siniri (bilincli):</b> bu test locator'larin <b>sozdizimini</b> dogrular,
 * dogru elementi bulup bulmadigini degil. Locator'larin gercek DOM ile eslesmesi ancak
 * uygulama ayaktayken, Faz 4 testleriyle dogrulanir.
 */
@Epic("Cerceve Self-Check")
@Feature("Faz 2 — Page Object locator'lari")
public class LocatorSanityTest extends BaseTest {

    /** Icinde statik {@code By} alanlari bulunan tum sayfa ve bilesen siniflari. */
    private static final List<Class<?>> PAGE_CLASSES = List.of(
            LoginPage.class,
            SearchCustomerPage.class,
            CustomerDetailPage.class,
            UpdateCustomerPage.class,
            CreateCustomerPage.class,
            DemographicStep.class,
            AddressStep.class,
            ContactStep.class,
            NavbarComponent.class,
            SidebarComponent.class,
            ResultsTableComponent.class,
            PaginationComponent.class,
            AddressModalComponent.class,
            ConfirmDialogComponent.class);

    /** Uygulamaya gitme; bos sayfa yeterli. */
    @Override
    protected boolean shouldOpenApplication() {
        return false;
    }

    @Test(description = "Page Object'lerdeki tum statik locator'lar gecerli CSS/XPath ifadesidir")
    @Story("Statik locator sozdizimi")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Her sinifin statik By alanlari reflection ile toplanir ve bos sayfada "
            + "calistirilir. Bozuk bir secici InvalidSelectorException ile burada yakalanir.")
    public void staticLocatorsAreValid() {
        WebDriver driver = driver();
        driver.get("data:text/html;charset=utf-8,<html><body></body></html>");

        List<String> invalid = new ArrayList<>();
        int checked = 0;

        for (Class<?> pageClass : PAGE_CLASSES) {
            for (Field field : pageClass.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) || !By.class.isAssignableFrom(field.getType())) {
                    continue;
                }
                field.setAccessible(true);
                By locator;
                try {
                    locator = (By) field.get(null);
                } catch (IllegalAccessException e) {
                    invalid.add(pageClass.getSimpleName() + "." + field.getName() + " -> okunamadi");
                    continue;
                }
                checked++;
                try {
                    driver.findElements(locator);
                } catch (InvalidSelectorException e) {
                    invalid.add(pageClass.getSimpleName() + "." + field.getName() + " -> " + locator);
                }
            }
        }

        Allure.step("Dogrulanan locator sayisi: " + checked);
        AllureAttachment.text("Taranan sinif sayisi", String.valueOf(PAGE_CLASSES.size()));

        assertThat(checked)
                .as("taranan statik locator sayisi (siniflar bosalmis olabilir)")
                .isGreaterThan(60);

        assertThat(invalid)
                .as("gecersiz secici iceren locator'lar")
                .isEmpty();
    }

    @Test(description = "Indeksten uretilen dinamik locator'lar da gecerli ifadelerdir")
    @Story("Dinamik locator sozdizimi")
    @Severity(SeverityLevel.NORMAL)
    @Description("Adres karti, sonuc tablosu ve sayfalama bilesenleri locator'larini calisma "
            + "aninda uretir. Yalnizca findElements tabanli (beklemesiz) metotlar cagrilir; "
            + "bos sayfada hizli calisir ve sozdizimi hatalarini ortaya cikarir.")
    public void dynamicLocatorsAreValid() {
        WebDriver driver = driver();
        driver.get("data:text/html;charset=utf-8,<html><body></body></html>");

        Allure.step("Adres karti locator'lari (nth-child ile uretilir)");
        AddressCardComponent card = new AddressCardComponent(driver, 0);
        assertThat(card.isPrimary()).isFalse();
        assertThat(card.isMenuOpen()).isFalse();
        assertThat(card.hasLinkedAccount()).isFalse();
        assertThat(card.isVisible()).isFalse();

        Allure.step("Sonuc tablosu locator'lari (satir/kolon indeksi ile uretilir)");
        ResultsTableComponent table = new ResultsTableComponent(driver);
        assertThat(table.rowCount()).isZero();
        assertThat(table.columnCount()).isZero();
        assertThat(table.headerTexts()).isEmpty();
        assertThat(table.customerIds()).isEmpty();
        for (ResultsTableComponent.Column column : ResultsTableComponent.Column.values()) {
            assertThat(table.columnValues(column)).as("kolon %s", column).isEmpty();
        }

        Allure.step("Sayfalama locator'lari");
        PaginationComponent pagination = new PaginationComponent(driver);
        assertThat(pagination.pageCount()).isZero();
        assertThat(pagination.isVisible()).isFalse();

        Allure.step("Alan hatasi locator'lari (id'den ust sarmalayiciya cikan XPath)");
        SearchCustomerPage searchPage = new SearchCustomerPage(driver);
        for (SearchCustomerPage.Filter filter : SearchCustomerPage.Filter.values()) {
            assertThat(searchPage.filterError(filter)).as("filtre hatasi %s", filter).isNull();
        }

        DemographicStep demographic = new DemographicStep(driver);
        for (DemographicStep.Field field : DemographicStep.Field.values()) {
            assertThat(demographic.fieldError(field)).as("demografik alan hatasi %s", field).isNull();
        }

        ContactStep contact = new ContactStep(driver);
        for (ContactStep.Field field : ContactStep.Field.values()) {
            assertThat(contact.fieldError(field)).as("kontakt alan hatasi %s", field).isNull();
        }

        UpdateCustomerPage update = new UpdateCustomerPage(driver);
        for (UpdateCustomerPage.Field field : UpdateCustomerPage.Field.values()) {
            assertThat(update.fieldError(field)).as("guncelleme alan hatasi %s", field).isNull();
        }

        AddressModalComponent modal =
                new AddressModalComponent(driver, AddressModalComponent.Variant.CUSTOMER_DETAIL);
        for (AddressModalComponent.Field field : AddressModalComponent.Field.values()) {
            assertThat(modal.fieldError(field)).as("adres modal alan hatasi %s", field).isNull();
        }
    }
}
