package com.crmlite.ui.core.waits;

import com.crmlite.ui.core.config.ConfigLoader;
import com.crmlite.ui.core.config.FrameworkConfig;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Bekleme nesnelerini uretir. Tum sureler {@code timeout.multiplier} ile olceklenir;
 * boylece yavas CI makinelerinde tek bir ayar ile tolerans artirilabilir.
 */
public final class WaitFactory {

    private static final Duration POLLING_INTERVAL = Duration.ofMillis(250);

    private WaitFactory() {
    }

    public static WebDriverWait defaultWait(WebDriver driver) {
        return waitOf(driver, config().explicitTimeout());
    }

    public static WebDriverWait shortWait(WebDriver driver) {
        return waitOf(driver, config().shortTimeout());
    }

    public static WebDriverWait longWait(WebDriver driver) {
        return waitOf(driver, config().longTimeout());
    }

    public static WebDriverWait waitOf(WebDriver driver, int seconds) {
        WebDriverWait wait = new WebDriverWait(driver, scaled(seconds), POLLING_INTERVAL);
        wait.ignoring(NoSuchElementException.class);
        wait.ignoring(StaleElementReferenceException.class);
        return wait;
    }

    /**
     * Bir kosulun belirli bir sure boyunca <b>bozulmadigini</b> dogrulamak icin kullanilir.
     * Negatif kabul kriterlerinde ("buton aktif olmamalidir") anlik kontrol yaniltici
     * gecer sonuc uretir; bu yuzden kisa bir sure gozlemlemek gerekir.
     */
    public static FluentWait<WebDriver> observationWait(WebDriver driver, Duration duration) {
        return new FluentWait<>(driver)
                .withTimeout(duration)
                .pollingEvery(POLLING_INTERVAL);
    }

    public static Duration scaled(int seconds) {
        return Duration.ofMillis(Math.round(seconds * 1000L * config().timeoutMultiplier()));
    }

    private static FrameworkConfig config() {
        return ConfigLoader.get();
    }
}
