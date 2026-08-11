package com.crmlite.ui.core.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** JavaScript tabanli yardimcilar. Yalnizca native etkilesim mumkun olmadiginda kullanilmalidir. */
public final class JsUtil {

    private JsUtil() {
    }

    public static Object execute(WebDriver driver, String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    public static void scrollIntoView(WebDriver driver, WebElement element) {
        execute(driver, "arguments[0].scrollIntoView({block:'center', inline:'nearest'});", element);
    }

    /**
     * JS ile tiklama. Native {@code click()} basarisiz olursa son care olarak kullanilir;
     * gercek kullanici davranisini birebir taklit etmedigi icin varsayilan yol degildir.
     */
    public static void click(WebDriver driver, WebElement element) {
        execute(driver, "arguments[0].click();", element);
    }

    /** Elemanin gorunur ekranda oldugunu dogrular (FR-002 ACC-011 gibi yerlesim kontrollerinde yardimci). */
    public static boolean isInViewport(WebDriver driver, WebElement element) {
        Object result = execute(driver,
                "var r = arguments[0].getBoundingClientRect();"
                        + "return r.top >= 0 && r.left >= 0"
                        + " && r.bottom <= (window.innerHeight || document.documentElement.clientHeight)"
                        + " && r.right <= (window.innerWidth || document.documentElement.clientWidth);",
                element);
        return Boolean.TRUE.equals(result);
    }
}
