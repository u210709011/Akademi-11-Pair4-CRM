package com.crmlite.ui.core.driver;

import com.crmlite.ui.core.exceptions.FrameworkException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aktif thread'in WebDriver ornegini tutar.
 *
 * <p>Paralel calistirmanin tek dogru yolu {@link ThreadLocal}'dir; cerceve icinde
 * statik bir {@code WebDriver} alani <b>bulunmamalidir</b>.
 */
public final class DriverManager {

    private static final Logger log = LoggerFactory.getLogger(DriverManager.class);

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void set(WebDriver driver) {
        DRIVER.set(driver);
    }

    public static WebDriver get() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new FrameworkException(
                    "Bu thread icin WebDriver yok. Test sinifi BaseTest'i genisletiyor mu?");
        }
        return driver;
    }

    public static boolean isActive() {
        return DRIVER.get() != null;
    }

    /**
     * Driver'i kapatir ve ThreadLocal kaydini siler.
     * {@code remove()} cagrilmazsa CI'da uzun suren suite'lerde bellek sizintisi olusur.
     */
    public static void quit() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            return;
        }
        try {
            driver.quit();
        } catch (RuntimeException e) {
            log.warn("WebDriver kapatilirken hata olustu: {}", e.getMessage());
        } finally {
            DRIVER.remove();
        }
    }
}
