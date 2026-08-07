package com.crmlite.ui.tests;

import com.crmlite.ui.core.config.ConfigLoader;
import com.crmlite.ui.core.config.FrameworkConfig;
import com.crmlite.ui.core.driver.DriverFactory;
import com.crmlite.ui.core.driver.DriverManager;
import com.crmlite.ui.core.exceptions.FrameworkException;
import com.crmlite.ui.core.utils.BrowserStorageUtil;
import com.crmlite.ui.core.waits.WaitFactory;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

/**
 * Tum UI testlerinin atasi. WebDriver yasam dongusunu ve testler arasi izolasyonu yonetir.
 *
 * <p>Her test metodu <b>temiz bir tarayici oturumu</b> ile baslar: testler birbirinin
 * biraktigi duruma guvenemez, bu yuzden herhangi bir sirada calistirilabilirler.
 */
public abstract class BaseTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected FrameworkConfig config;

    /**
     * Bu test sinifi tarayici kullaniyor mu?
     *
     * <p>Saf veri/birim dogrulamalari (or. {@code DataLayerSelfCheckTest}) icin
     * {@code false} donmelidir: her test metodu icin Chrome acip kapatmak, hicbir sey
     * kazandirmadan test basina ~25 saniye ekler ve CI suresini kati kat buyutur.
     */
    protected boolean requiresBrowser() {
        return true;
    }

    /**
     * Test sinifi {@code @BeforeMethod} icinde uygulamaya gitsin mi?
     * Uygulamaya ihtiyac duymayan testler (or. altyapi self-check) bunu {@code false} yapar.
     */
    protected boolean shouldOpenApplication() {
        return true;
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        config = ConfigLoader.get();

        if (!requiresBrowser()) {
            return;
        }

        WebDriver driver = DriverFactory.create(config);
        DriverManager.set(driver);

        if (shouldOpenApplication()) {
            openApplication();
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        // Delil toplama (ekran goruntusu / sayfa kaynagi / console log) TestListener
        // icinde, driver kapatilmadan ONCE yapilir. Burada yalnizca kapatma kalir.
        DriverManager.quit();
    }

    // --- Ortak yardimcilar ---

    protected WebDriver driver() {
        return DriverManager.get();
    }

    /**
     * Uygulamayi acar, onceki oturumu temizler ve UI dilini sabitler.
     *
     * <p>Dil sabitlemesi kritiktir: front-end varsayilani {@code en}, FR-018 ise Turkce
     * diyor. Dili acikca pinlemezsek metin assertion'lari makineye gore degisir.
     */
    protected void openApplication() {
        WebDriver driver = driver();
        navigateWithRetry(driver, config.baseUrl());
        awaitApplicationOrigin(driver);

        BrowserStorageUtil.clearAll(driver);
        BrowserStorageUtil.setLocalStorage(driver, BrowserStorageUtil.LANG_KEY, config.uiLanguage());

        // Dil ve temiz oturum ancak yeniden yuklemeden sonra uygulanir.
        driver.navigate().refresh();
        awaitApplicationOrigin(driver);
    }

    /**
     * Sayfayi acar; gecici bir yukleme zaman asiminda bir kez yeniden dener.
     *
     * <p>Angular dev-server modulleri istek uzerine derledigi icin ilk istekler
     * ara sira {@code pageLoadTimeout}'a takilabiliyor. Bu, urun hatasi degil ortam
     * gurultusudur. Hata {@code @BeforeMethod} icinde olustugu icin {@code RetryAnalyzer}
     * devreye giremez (TestNG konfigurasyon hatalarini yeniden denemez) — bu yuzden
     * yeniden deneme burada, kaynagında yapilir.
     */
    private void navigateWithRetry(WebDriver driver, String url) {
        try {
            driver.get(url);
            return;
        } catch (WebDriverException e) { // TimeoutException bunun alt sinifidir
            log.warn("Sayfa yukleme zaman asimina ugradi | {} | {}",
                    url, e.getMessage().lines().findFirst().orElse(""));
        }

        // Zaman asimi, sayfanin kullanilamaz oldugu anlamina gelmez: EAGER stratejisinde
        // DOM hazir olsa bile arka plandaki istekler acik kalabiliyor. Once mevcut duruma
        // bakilir; gercekten uygulamaya gecilmediyse tek bir yeniden deneme yapilir.
        if (isOnApplicationOrigin(driver)) {
            log.info("Zaman asimina ragmen uygulama adresi acik, devam ediliyor.");
            return;
        }

        try {
            driver.get(url);
        } catch (WebDriverException e) {
            if (!isOnApplicationOrigin(driver)) {
                throw new FrameworkException(
                        "Uygulama sayfasi iki denemede de acilamadi: " + url, e);
            }
        }
    }

    private boolean isOnApplicationOrigin(WebDriver driver) {
        try {
            return driver.getCurrentUrl().startsWith(config.baseUrl());
        } catch (WebDriverException e) {
            return false;
        }
    }

    /**
     * Tarayicinin gercekten uygulama adresine gectigini dogrular.
     *
     * <p>Navigasyon basarisiz olursa Chrome baslangictaki {@code data:,} adresinde kalir
     * ve sonraki {@code localStorage} erisimi "Storage is disabled inside 'data:' URLs"
     * gibi <b>gercek sebebi gizleyen</b> bir hata uretir. Burada durum acikca yakalanir.
     */
    private void awaitApplicationOrigin(WebDriver driver) {
        try {
            WaitFactory.defaultWait(driver).until(d -> d.getCurrentUrl().startsWith(config.baseUrl()));
        } catch (TimeoutException e) {
            throw new FrameworkException(String.format(
                    "Uygulama adresine gecilemedi. Beklenen: %s, mevcut: %s. "
                            + "Front-end (ng serve) ayakta mi?",
                    config.baseUrl(), driver.getCurrentUrl()), e);
        }
    }

    protected String urlOf(String path) {
        return config.baseUrl() + (path.startsWith("/") ? path : "/" + path);
    }
}
