package com.crmlite.ui.core.report;

import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.stream.Collectors;

/**
 * Allure ek dosyalari.
 *
 * <p>Bilincli olarak {@code @Attachment}/{@code @Step} anotasyonlari yerine programatik
 * Allure API'si kullanilir: bu sayede AspectJ weaver javaagent'ina ihtiyac kalmaz ve
 * JDK surumu ile AspectJ arasindaki uyum riski ortadan kalkar.
 */
public final class AllureAttachment {

    private static final Logger log = LoggerFactory.getLogger(AllureAttachment.class);

    private AllureAttachment() {
    }

    public static void screenshot(String name, byte[] png) {
        if (png == null || png.length == 0) {
            return;
        }
        Allure.addAttachment(name, "image/png", new ByteArrayInputStream(png), ".png");
    }

    public static void pageSource(WebDriver driver) {
        try {
            Allure.addAttachment("Sayfa kaynagi (HTML)", "text/html", driver.getPageSource(), ".html");
        } catch (RuntimeException e) {
            log.warn("Sayfa kaynagi alinamadi: {}", e.getMessage());
        }
    }

    public static void currentUrl(WebDriver driver) {
        try {
            Allure.addAttachment("Mevcut URL", "text/plain", driver.getCurrentUrl(), ".txt");
        } catch (RuntimeException e) {
            log.warn("URL alinamadi: {}", e.getMessage());
        }
    }

    /**
     * Tarayici console log'u. Angular'daki sessiz JS hatalarini ortaya cikardigi icin
     * UI testlerinde en cok atlanan, en degerli delildir.
     */
    public static void browserConsoleLogs(WebDriver driver) {
        try {
            LogEntries entries = driver.manage().logs().get(LogType.BROWSER);
            String text = entries.getAll().stream()
                    .map(entry -> "[" + entry.getLevel() + "] " + entry.getMessage())
                    .collect(Collectors.joining(System.lineSeparator()));
            if (!text.isBlank()) {
                Allure.addAttachment("Tarayici console log", "text/plain", text, ".txt");
            }
        } catch (RuntimeException e) {
            // Firefox bu log tipini desteklemez; bu bir hata degildir.
            log.debug("Tarayici console log'u okunamadi: {}", e.getMessage());
        }
    }

    public static void text(String name, String content) {
        if (content != null && !content.isBlank()) {
            Allure.addAttachment(name, "text/plain", content, ".txt");
        }
    }
}
