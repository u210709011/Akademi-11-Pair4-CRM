package com.crmlite.ui.core.utils;

import org.openqa.selenium.WebDriver;

/**
 * localStorage / sessionStorage erisimi.
 *
 * <p>Iki yerde kritik:
 * <ul>
 *   <li>UI dilini sabitlemek ({@code crm-lite-lang}) — metin assertion'larini deterministik yapar</li>
 *   <li>Testler arasi oturum sizintisini onlemek</li>
 * </ul>
 */
public final class BrowserStorageUtil {

    /** Front-end'in dil tercihini sakladigi anahtar (bkz. core/i18n/i18n.service.ts). */
    public static final String LANG_KEY = "crm-lite-lang";

    private BrowserStorageUtil() {
    }

    public static void setLocalStorage(WebDriver driver, String key, String value) {
        JsUtil.execute(driver, "window.localStorage.setItem(arguments[0], arguments[1]);", key, value);
    }

    public static String getLocalStorage(WebDriver driver, String key) {
        Object value = JsUtil.execute(driver, "return window.localStorage.getItem(arguments[0]);", key);
        return value == null ? null : value.toString();
    }

    public static void removeLocalStorage(WebDriver driver, String key) {
        JsUtil.execute(driver, "window.localStorage.removeItem(arguments[0]);", key);
    }

    public static void clearAll(WebDriver driver) {
        JsUtil.execute(driver, "window.localStorage.clear(); window.sessionStorage.clear();");
    }
}
