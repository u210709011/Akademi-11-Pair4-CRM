package com.crmlite.ui.core.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotUtil {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotUtil.class);

    private static final Path OUTPUT_DIR = Paths.get("target", "screenshots");
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private ScreenshotUtil() {
    }

    /** Ekran goruntusunu byte dizisi olarak alir; hicbir kosulda test akisini kesmez. */
    public static byte[] capture(WebDriver driver) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (RuntimeException e) {
            log.warn("Ekran goruntusu alinamadi: {}", e.getMessage());
            return new byte[0];
        }
    }

    /** Ekran goruntusunu diske de yazar (Allure disinda hizli goz atmak icin). */
    public static Path saveToDisk(byte[] screenshot, String testName) {
        if (screenshot.length == 0) {
            return null;
        }
        try {
            Files.createDirectories(OUTPUT_DIR);
            String fileName = sanitize(testName) + "_" + LocalDateTime.now().format(STAMP) + ".png";
            Path target = OUTPUT_DIR.resolve(fileName);
            Files.write(target, screenshot);
            return target;
        } catch (IOException e) {
            log.warn("Ekran goruntusu diske yazilamadi: {}", e.getMessage());
            return null;
        }
    }

    private static String sanitize(String value) {
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
