package com.crmlite.ui.core.driver.options;

import com.crmlite.ui.core.config.FrameworkConfig;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.edge.EdgeOptions;

import java.util.HashMap;
import java.util.Map;

public final class EdgeOptionsBuilder {

    private EdgeOptionsBuilder() {
    }

    public static EdgeOptions build(FrameworkConfig config) {
        EdgeOptions options = new EdgeOptions();

        // Bkz. ChromeOptionsBuilder.
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        if (config.headless()) {
            options.addArguments("--headless=new");
        }

        options.addArguments(
                "--window-size=" + config.windowWidth() + "," + config.windowHeight(),
                "--disable-gpu",
                "--disable-dev-shm-usage",
                "--no-sandbox",
                "--lang=en-US",
                "--disable-notifications"
        );

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        return options;
    }
}
