package com.crmlite.ui.core.config;

import org.aeonbits.owner.Config;

/**
 * Tip guvenli konfigurasyon arayuzu.
 *
 * <p>Kaynak onceligi (ustteki alttakini ezer):
 * <ol>
 *   <li>{@code -Dsystem.property} (CI/CLI parametreleri)</li>
 *   <li>{@code classpath:config/{env}.properties}</li>
 *   <li>{@code classpath:config/config.properties}</li>
 *   <li>Bu arayuzdeki {@code @DefaultValue}</li>
 * </ol>
 *
 * <p>Ortam degiskenleri (CRM_UI_USERNAME / CRM_UI_PASSWORD) {@link ConfigLoader}
 * tarafindan sistem parametresine cevrilerek en yuksek onceligi alir.
 */
@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "classpath:config/${env}.properties",
        "classpath:config/config.properties"
})
public interface FrameworkConfig extends Config {

    // --- Ortam ---

    @Key("env")
    @DefaultValue("local")
    String env();

    /** Ornek: http://localhost:4200 — sondaki '/' olmadan. */
    @Key("base.url")
    String baseUrl();

    /** API gateway; yalnizca test verisi hazirlama/temizleme icin kullanilir. */
    @Key("api.base.url")
    String apiBaseUrl();

    @Key("keycloak.url")
    String keycloakUrl();

    // --- Kimlik bilgileri ---

    @Key("ui.username")
    String username();

    @Key("ui.password")
    String password();

    // --- Tarayici ---

    @Key("browser")
    @DefaultValue("chrome")
    String browser();

    @Key("headless")
    @DefaultValue("false")
    boolean headless();

    @Key("window.width")
    @DefaultValue("1920")
    int windowWidth();

    @Key("window.height")
    @DefaultValue("1080")
    int windowHeight();

    /** Bos ise yerel driver, dolu ise RemoteWebDriver kullanilir. */
    @Key("remote.url")
    @DefaultValue("")
    String remoteUrl();

    // --- Bekleme sureleri (saniye) ---

    @Key("timeout.explicit")
    @DefaultValue("15")
    int explicitTimeout();

    @Key("timeout.short")
    @DefaultValue("5")
    int shortTimeout();

    @Key("timeout.long")
    @DefaultValue("45")
    int longTimeout();

    @Key("timeout.pageload")
    @DefaultValue("45")
    int pageLoadTimeout();

    @Key("timeout.multiplier")
    @DefaultValue("1.0")
    double timeoutMultiplier();

    /**
     * WebDriver'a gonderilen tek bir komutun yanit bekleme siniri (saniye).
     *
     * <p>Varsayilan Selenium istemcisinde bu sinir <b>cok yuksektir</b>; tarayici veya
     * chromedriver cokerse istemci olu oturumdan yanit beklerken suite testler
     * <i>arasinda</i> asili kalir (gozlenen: 11 ve 20 dakika hicbir cikti yok).
     * {@code RetryAnalyzer} bunu yakalayamaz — asilma test icinde degil, driver
     * kapatma/acma adimindadir. Sinirli bir deger, olu oturumu sessiz bir asilma
     * yerine <b>acik bir hataya</b> cevirir ve suite ilerler.
     *
     * <p>Sayfa yuklemesinden buyuk olmali ({@code timeout.pageload}), aksi halde
     * yavas ama saglikli sayfalar bosuna kesilir.
     */
    @Key("timeout.command")
    @DefaultValue("120")
    int commandTimeout();

    // --- Uygulama davranisi ---

    @Key("ui.language")
    @DefaultValue("en")
    String uiLanguage();

    // --- Cerceve davranisi ---

    @Key("retry.count")
    @DefaultValue("1")
    int retryCount();

    /** all | failure | off */
    @Key("screenshot.mode")
    @DefaultValue("failure")
    String screenshotMode();
}
