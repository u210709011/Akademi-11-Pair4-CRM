package com.crmlite.ui.core.listeners;

import com.crmlite.ui.core.config.ConfigLoader;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Yalnizca <b>altyapi</b> kaynakli hatalari yeniden dener.
 *
 * <p>{@link AssertionError} <b>asla</b> yeniden denenmez: gercek bir urun hatasini
 * tekrar deneyerek yesile boyamak, test otomasyonundaki en tehlikeli anti-pattern'dir.
 *
 * <p>Yeniden denenen testler Allure raporunda "retried/flaky" olarak gorunur;
 * sessizce basarili sayilmaz.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(RetryAnalyzer.class);

    private int attempt = 0;

    @Override
    public boolean retry(ITestResult result) {
        int maxRetries = ConfigLoader.get().retryCount();
        if (attempt >= maxRetries) {
            return false;
        }

        Throwable cause = result.getThrowable();
        if (!isInfrastructureFailure(cause)) {
            return false;
        }

        attempt++;
        log.warn("ALTYAPI HATASI — yeniden deneniyor ({}/{}) | test={} | sebep={}",
                attempt, maxRetries, result.getName(),
                cause == null ? "bilinmiyor" : cause.getClass().getSimpleName());
        return true;
    }

    private boolean isInfrastructureFailure(Throwable cause) {
        if (cause == null) {
            return false;
        }
        if (cause instanceof AssertionError) {
            return false;
        }
        return cause instanceof TimeoutException
                || cause instanceof StaleElementReferenceException
                || cause instanceof ElementClickInterceptedException
                || cause instanceof ElementNotInteractableException
                || (cause instanceof WebDriverException && !(cause.getCause() instanceof AssertionError));
    }
}
