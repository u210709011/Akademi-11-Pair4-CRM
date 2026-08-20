package com.crmlite.ui.core.report;

import com.crmlite.ui.core.config.FrameworkConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/**
 * Allure raporunun "Environment" bolumunu doldurur.
 * Her raporda hangi ortam / tarayici / dil ile kosuldugu gorunur olur — bir bulgu
 * tartisilirken ilk sorulan sorunun cevabi rapordadir.
 */
public final class AllureEnvironmentWriter {

    private static final Logger log = LoggerFactory.getLogger(AllureEnvironmentWriter.class);

    private AllureEnvironmentWriter() {
    }

    public static void write(FrameworkConfig config) {
        Path resultsDir = Paths.get(System.getProperty("allure.results.directory", "target/allure-results"));

        Properties properties = new Properties();
        properties.setProperty("Ortam", config.env());
        properties.setProperty("UI URL", config.baseUrl());
        properties.setProperty("API URL", config.apiBaseUrl());
        properties.setProperty("Tarayici", config.browser());
        properties.setProperty("Headless", String.valueOf(config.headless()));
        properties.setProperty("UI Dili", config.uiLanguage());
        properties.setProperty("Explicit Timeout (sn)", String.valueOf(config.explicitTimeout()));
        properties.setProperty("Timeout Carpani", String.valueOf(config.timeoutMultiplier()));
        properties.setProperty("Java", System.getProperty("java.version"));
        properties.setProperty("Isletim Sistemi", System.getProperty("os.name"));

        try {
            Files.createDirectories(resultsDir);
            try (OutputStream out = Files.newOutputStream(resultsDir.resolve("environment.properties"))) {
                properties.store(out, "CRM Lite UI Test Automation");
            }
        } catch (IOException e) {
            log.warn("Allure environment.properties yazilamadi: {}", e.getMessage());
        }

        copyCategories(resultsDir);
    }

    /**
     * Hata siniflandirmasi (categories.json) yalnizca allure-results icinde bulunursa
     * uygulanir; classpath'te durmasi yeterli degildir.
     */
    private static void copyCategories(Path resultsDir) {
        try (InputStream source = AllureEnvironmentWriter.class
                .getClassLoader().getResourceAsStream("categories.json")) {
            if (source == null) {
                log.warn("categories.json classpath'te bulunamadi; hata siniflandirmasi uygulanmayacak.");
                return;
            }
            Files.copy(source, resultsDir.resolve("categories.json"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.warn("categories.json kopyalanamadi: {}", e.getMessage());
        }
    }
}
