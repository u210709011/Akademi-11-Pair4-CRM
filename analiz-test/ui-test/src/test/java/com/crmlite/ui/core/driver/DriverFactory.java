package com.crmlite.ui.core.driver;

import com.crmlite.ui.core.config.FrameworkConfig;
import com.crmlite.ui.core.driver.options.ChromeOptionsBuilder;
import com.crmlite.ui.core.driver.options.EdgeOptionsBuilder;
import com.crmlite.ui.core.driver.options.FirefoxOptionsBuilder;
import com.crmlite.ui.core.exceptions.FrameworkException;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.List;

/**
 * WebDriver ornegi <b>uretir</b> — yasam dongusunu yonetmez.
 * Yasam dongusu {@code BaseTest} + {@link DriverManager} sorumlulugundadir (tek sorumluluk).
 *
 * <p>Driver binary'leri Selenium Manager tarafindan otomatik cozulur;
 * ayrica bir WebDriverManager bagimliligi yoktur.
 */
public final class DriverFactory {

    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);

    /**
     * Susturulan JUL logger'lari icin GUCLU referans.
     *
     * <p>JUL, logger nesnelerini <b>zayif (weak)</b> referansla tutar: referans
     * saklanmazsa logger cop toplanir, seviye ayari sifirlanir ve uyarilar geri doner.
     * Bu alan olmadan {@link #silenceCdpVersionWarnings()} etkisiz kalir.
     */
    private static final List<java.util.logging.Logger> SILENCED_LOGGERS = silenceCdpVersionWarnings();

    private DriverFactory() {
    }

    /**
     * Chrome kendini Selenium'dan daha hizli guncelledigi icin her yeni tarayici
     * surumunde "Unable to find an exact match for CDP version NNN" uyarisi olusur.
     *
     * <p>Bu cerceve DevTools/CDP <b>kullanmiyor</b>; uyari islevsel bir sorun degil,
     * yalnizca gurultu. Selenium surumunu tarayici pesinde surekli guncellemek yerine
     * ilgili JUL logger'lari susturuluyor.
     */
    private static List<java.util.logging.Logger> silenceCdpVersionWarnings() {
        List<java.util.logging.Logger> loggers = List.of(
                java.util.logging.Logger.getLogger("org.openqa.selenium.devtools.CdpVersionFinder"),
                java.util.logging.Logger.getLogger("org.openqa.selenium.chromium.ChromiumDriver"));
        loggers.forEach(logger -> logger.setLevel(java.util.logging.Level.SEVERE));
        return loggers;
    }

    public static WebDriver create(FrameworkConfig config) {
        BrowserType browser = BrowserType.from(config.browser());
        MutableCapabilities options = optionsFor(browser, config);
        ClientConfig clientConfig = clientConfig(config);

        WebDriver driver = config.remoteUrl().isBlank()
                ? createLocal(browser, options, clientConfig)
                : createRemote(config.remoteUrl(), options, clientConfig);

        // Implicit wait BILEREK ayarlanmiyor.
        // Implicit + explicit karisimi ongorulemez bekleme sureleri ve yaniltici
        // hata mesajlari uretir; tum beklemeler WaitFactory uzerinden explicit yapilir.
        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(scaled(config.pageLoadTimeout(), config.timeoutMultiplier())));

        if (!config.headless()) {
            driver.manage().window().maximize();
        }

        log.debug("WebDriver olusturuldu | tarayici={} | headless={} | remote={}",
                browser, config.headless(), !config.remoteUrl().isBlank());

        return driver;
    }

    private static MutableCapabilities optionsFor(BrowserType browser, FrameworkConfig config) {
        return switch (browser) {
            case CHROME -> ChromeOptionsBuilder.build(config);
            case FIREFOX -> FirefoxOptionsBuilder.build(config);
            case EDGE -> EdgeOptionsBuilder.build(config);
        };
    }

    /**
     * Surucu ile konusan HTTP istemcisinin yapilandirmasi.
     *
     * <p>Tek amaci <b>komut zaman asimi</b>: tarayici ya da chromedriver cokerse
     * istemci olu oturumdan yanit beklemeye devam eder ve suite, testlerin
     * <i>arasinda</i> sessizce asili kalir. Sinir konunca ayni durum acik bir
     * hataya donusur, {@code TestListener} delil toplar ve kosu ilerler.
     * Ayrintili gerekce: {@link FrameworkConfig#commandTimeout()}.
     */
    private static ClientConfig clientConfig(FrameworkConfig config) {
        return ClientConfig.defaultConfig()
                .readTimeout(Duration.ofSeconds(
                        scaled(config.commandTimeout(), config.timeoutMultiplier())));
    }

    private static WebDriver createLocal(BrowserType browser, MutableCapabilities options,
                                         ClientConfig clientConfig) {
        try {
            return switch (browser) {
                case CHROME -> new ChromeDriver(
                        ChromeDriverService.createDefaultService(),
                        (org.openqa.selenium.chrome.ChromeOptions) options, clientConfig);
                case FIREFOX -> new FirefoxDriver(
                        GeckoDriverService.createDefaultService(),
                        (org.openqa.selenium.firefox.FirefoxOptions) options, clientConfig);
                case EDGE -> new EdgeDriver(
                        EdgeDriverService.createDefaultService(),
                        (org.openqa.selenium.edge.EdgeOptions) options, clientConfig);
            };
        } catch (RuntimeException e) {
            throw new FrameworkException(
                    "Yerel WebDriver baslatilamadi (tarayici=" + browser + "). "
                            + "Tarayicinin kurulu oldugundan emin olun.", e);
        }
    }

    private static WebDriver createRemote(String remoteUrl, MutableCapabilities options,
                                          ClientConfig clientConfig) {
        try {
            URL url = URI.create(remoteUrl).toURL();
            return RemoteWebDriver.builder()
                    .oneOf(options)
                    .address(url)
                    .config(clientConfig)
                    .build();
        } catch (MalformedURLException e) {
            throw new FrameworkException("Gecersiz remote.url: " + remoteUrl, e);
        } catch (RuntimeException e) {
            throw new FrameworkException("Uzak WebDriver'a baglanilamadi: " + remoteUrl, e);
        }
    }

    private static long scaled(int seconds, double multiplier) {
        return Math.round(seconds * multiplier);
    }
}
