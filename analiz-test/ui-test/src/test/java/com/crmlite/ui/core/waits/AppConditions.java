package com.crmlite.ui.core.waits;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.List;
import java.util.regex.Pattern;

/**
 * CRM Lite'a ozel bekleme kosullari.
 *
 * <p>Uygulama Angular <b>Signal Forms</b> kullaniyor: buton {@code [disabled]} durumu
 * ve alan hatalari DOM'a senkron yansimiyor. Bu yuzden gereksinimlerdeki
 * "buton aktif olmalidir / olmamalidir" tipi kabul kriterleri, standart
 * {@code ExpectedConditions} yerine buradaki kosullarla dogrulanir.
 */
public final class AppConditions {

    private AppConditions() {
    }

    /** Buton aktiflesene kadar bekler. "Zorunlu alanlar dolunca Next/Save/Login aktif olur" ACC'lerinin temeli. */
    public static ExpectedCondition<WebElement> buttonBecomesEnabled(By locator) {
        return new ExpectedCondition<>() {
            @Override
            public WebElement apply(WebDriver driver) {
                try {
                    WebElement element = driver.findElement(locator);
                    return element.isDisplayed() && element.isEnabled() ? element : null;
                } catch (NoSuchElementException | StaleElementReferenceException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return "buton aktiflesmesi bekleniyor: " + locator;
            }
        };
    }

    /**
     * Butonun verilen sure boyunca pasif <b>kaldigini</b> dogrular.
     * NEGATIF kabul kriterleri icindir: anlik "disabled" kontrolu, Angular henuz
     * render etmediginde yanlis-gecer uretir.
     *
     * @return sure boyunca pasif kaldiysa {@code true}; bir an bile aktiflestiyse {@code false}
     */
    public static ExpectedCondition<Boolean> buttonBecameEnabled(By locator) {
        return driver -> {
            try {
                WebElement element = driver.findElement(locator);
                return element.isEnabled() ? Boolean.TRUE : null;
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                return null;
            }
        };
    }

    /**
     * FR-003/FR-004: Next'e basildiginda calisan tekillik + KPS dogrulama gostergesi
     * kaybolana kadar bekler.
     *
     * <p>10.08.2026: sinif adi {@code .verifying-indicator} -> {@code .btn-spinner-wrap}
     * olarak degisti (commit 6cce9c1) ve spinner tum ekranlarda ortak hale geldi. Eski
     * secici hicbir sey bulmadigi icin bekleme aninda donuyor ve adim gecisleri yarisa
     * giriyordu - testler sirayla degil, rastgele kirmizi oluyordu.
     */
    public static ExpectedCondition<Boolean> identityVerificationFinished() {
        By spinner = By.cssSelector(".btn-spinner-wrap");
        return driver -> {
            List<WebElement> indicators = driver.findElements(spinner);
            return indicators.stream().noneMatch(AppConditions::isSafelyDisplayed);
        };
    }

    /** FR-001 ACC-003: sifre alaninin {@code type} degeri (password &lt;-&gt; text) degisimi. */
    public static ExpectedCondition<Boolean> attributeToBe(By locator, String attribute, String expected) {
        return driver -> {
            try {
                return expected.equals(driver.findElement(locator).getDomAttribute(attribute));
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                return null;
            }
        };
    }

    /** Dinamik rotalar icin: ornegin {@code /detail-customer/\d+}. */
    public static ExpectedCondition<Boolean> urlMatches(Pattern pattern) {
        return new ExpectedCondition<>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return pattern.matcher(driver.getCurrentUrl()).find();
            }

            @Override
            public String toString() {
                return "URL'nin '" + pattern.pattern() + "' desenine uymasi bekleniyor";
            }
        };
    }

    /** FR-002: arama/sayfalama/siralama sonrasi tablonun beklenen satir sayisina ulasmasi. */
    public static ExpectedCondition<Boolean> rowCountToBe(By rowLocator, int expected) {
        return driver -> driver.findElements(rowLocator).size() == expected;
    }

    /** FR-002: sonuc tablosunun en az bir satir icermesi. */
    public static ExpectedCondition<Boolean> hasAnyRow(By rowLocator) {
        return driver -> !driver.findElements(rowLocator).isEmpty();
    }

    /** Modal/menu gibi bilesenlerin DOM'dan tamamen kalkmasi (Angular {@code @if} bloklarini kaldiriyor). */
    public static ExpectedCondition<Boolean> absentFromDom(By locator) {
        return driver -> driver.findElements(locator).isEmpty();
    }

    /**
     * Acik XHR kalmayana kadar bekler. Son care olarak kullanilmalidir;
     * oncelik her zaman gozle gorulur bir DOM kosuludur.
     */
    public static ExpectedCondition<Boolean> noPendingHttpRequests() {
        return driver -> {
            Object result = ((JavascriptExecutor) driver).executeScript(
                    "return window.performance"
                            + " && window.performance.getEntriesByType('resource')"
                            + "      .filter(function (r) { return r.responseEnd === 0; }).length === 0;");
            return Boolean.TRUE.equals(result);
        };
    }

    private static boolean isSafelyDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (StaleElementReferenceException e) {
            return false;
        }
    }
}
