package com.crmlite.ui.core.listeners;

import com.crmlite.ui.core.config.ConfigLoader;
import com.crmlite.ui.core.driver.DriverManager;
import com.crmlite.ui.core.report.AllureAttachment;
import com.crmlite.ui.core.utils.ScreenshotUtil;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.nio.file.Path;

/**
 * Test yasam dongusu: loglama, delil toplama ve MDC korelasyonu.
 *
 * <p>Hata aninda <b>uc delil birden</b> toplanir: ekran goruntusu, sayfa kaynagi ve
 * tarayici console log'u. Tek basina ekran goruntusu cogu Angular hatasini aciklamaz.
 */
public class TestListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(TestListener.class);

    private static final String MDC_TEST = "testName";

    @Override
    public void onTestStart(ITestResult result) {
        MDC.put(MDC_TEST, result.getTestClass().getRealClass().getSimpleName() + "." + result.getName());
        log.info(">>> BASLADI: {}", fullName(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if ("all".equalsIgnoreCase(ConfigLoader.get().screenshotMode())) {
            attachScreenshot(result, "Basarili - son ekran");
        }
        log.info("<<< GECTI  : {} ({} ms)", fullName(result), duration(result));
        MDC.remove(MDC_TEST);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("<<< KALDI  : {} ({} ms)", fullName(result), duration(result));

        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            log.error("      sebep: {}: {}", throwable.getClass().getSimpleName(), throwable.getMessage());
        }

        if (!"off".equalsIgnoreCase(ConfigLoader.get().screenshotMode())) {
            attachScreenshot(result, "Hata aninda ekran goruntusu");
        }
        attachDiagnostics();

        MDC.remove(MDC_TEST);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String reason = result.getThrowable() == null ? "-" : result.getThrowable().getMessage();
        log.warn("<<< ATLANDI: {} | sebep: {}", fullName(result), reason);
        MDC.remove(MDC_TEST);
    }

    private void attachScreenshot(ITestResult result, String label) {
        if (!DriverManager.isActive()) {
            return;
        }
        byte[] png = ScreenshotUtil.capture(DriverManager.get());
        AllureAttachment.screenshot(label, png);

        Path saved = ScreenshotUtil.saveToDisk(png, fullName(result));
        if (saved != null) {
            log.info("      ekran goruntusu: {}", saved.toAbsolutePath());
        }
    }

    private void attachDiagnostics() {
        if (!DriverManager.isActive()) {
            return;
        }
        WebDriver driver = DriverManager.get();
        AllureAttachment.currentUrl(driver);
        AllureAttachment.browserConsoleLogs(driver);
        AllureAttachment.pageSource(driver);
    }

    private String fullName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName() + "." + result.getName();
    }

    private long duration(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }
}
