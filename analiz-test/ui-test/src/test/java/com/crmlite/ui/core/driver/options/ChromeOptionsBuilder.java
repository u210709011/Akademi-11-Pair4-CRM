package com.crmlite.ui.core.driver.options;

import com.crmlite.ui.core.config.FrameworkConfig;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public final class ChromeOptionsBuilder {

    private ChromeOptionsBuilder() {
    }

    public static ChromeOptions build(FrameworkConfig config) {
        ChromeOptions options = new ChromeOptions();

        // Angular dev-server'in canli yeniden yukleme (live-reload) baglantisi acik
        // kaldigi icin sayfa "yukleniyor" durumundan cikmaz; NORMAL stratejide
        // driver.get() page-load timeout'una takilir ve tarayici 'data:,' adresinde kalir.
        // EAGER, DOM hazir olur olmaz geri doner. Tum beklemeler zaten explicit oldugu
        // icin bu guvenlidir ve SPA testlerinde dogru olan tercihtir.
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        if (config.headless()) {
            options.addArguments("--headless=new");
        }

        options.addArguments(
                "--window-size=" + config.windowWidth() + "," + config.windowHeight(),
                "--disable-gpu",
                "--disable-dev-shm-usage",   // CI konteynerlerinde /dev/shm kucuk oldugu icin sart
                "--no-sandbox",              // CI root kullanicisi icin
                "--lang=en-US",
                "--disable-notifications",
                "--disable-popup-blocking",
                "--disable-search-engine-choice-screen",
                "--remote-allow-origins=*"
        );

        // Chrome'un "sifreyi kaydet" balonu FR-001 login testlerinin ustune biniyor.
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);

        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        // Hata aninda tarayici console log'unu rapora ekleyebilmek icin.
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.WARNING);
        options.setCapability("goog:loggingPrefs", logPrefs);

        return options;
    }
}
