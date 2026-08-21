package com.crmlite.ui.core.driver.options;

import com.crmlite.ui.core.config.FrameworkConfig;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.firefox.FirefoxOptions;

public final class FirefoxOptionsBuilder {

    private FirefoxOptionsBuilder() {
    }

    public static FirefoxOptions build(FrameworkConfig config) {
        FirefoxOptions options = new FirefoxOptions();

        // Bkz. ChromeOptionsBuilder: dev-server canli yeniden yukleme baglantisi
        // yuzunden NORMAL strateji page-load timeout'una takiliyor.
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        if (config.headless()) {
            options.addArguments("-headless");
        }

        options.addArguments("--width=" + config.windowWidth(), "--height=" + config.windowHeight());

        options.addPreference("intl.accept_languages", "en-US, en");
        options.addPreference("dom.webnotifications.enabled", false);
        options.addPreference("signon.rememberSignons", false);

        return options;
    }
}
