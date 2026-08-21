package com.crmlite.ui.core.listeners;

import com.crmlite.ui.core.config.ConfigLoader;
import com.crmlite.ui.core.config.FrameworkConfig;
import com.crmlite.ui.core.report.AllureEnvironmentWriter;
import com.crmlite.ui.data.api.TestDataCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * Suite yasam dongusu.
 *
 * <p>Konfigurasyon burada, ilk testten once cozulur: eksik bir ayar varsa hata
 * "3. testte NullPointerException" olarak degil, suite basinda acik bir mesajla ortaya cikar.
 */
public class SuiteListener implements ISuiteListener {

    private static final Logger log = LoggerFactory.getLogger(SuiteListener.class);

    @Override
    public void onStart(ISuite suite) {
        FrameworkConfig config = ConfigLoader.get();

        log.info("========================================================");
        log.info(" Suite basliyor: {}", suite.getName());
        log.info(" Ortam   : {} ({})", config.env(), config.baseUrl());
        log.info(" Tarayici: {} (headless={})", config.browser(), config.headless());
        log.info(" UI dili : {}", config.uiLanguage());
        log.info("========================================================");

        AllureEnvironmentWriter.write(config);
    }

    @Override
    public void onFinish(ISuite suite) {
        // Temizlik hicbir kosulda suite sonucunu bozmamalidir; silinemeyen kayitlar
        // "artik" olarak raporlanir (bkz. FR-007 ACC-003 / FR-011 ACC-005 bosluğu).
        try {
            TestDataCleaner.cleanUp();
        } catch (RuntimeException e) {
            log.warn("Test verisi temizligi tamamlanamadi: {}", e.getMessage());
        }
        log.info("Suite tamamlandi: {}", suite.getName());
    }
}
