package com.crmlite.ui.core.config;

import com.crmlite.ui.core.exceptions.FrameworkException;
import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link FrameworkConfig} ornegini kuran ve dogrulayan tek giris noktasi.
 *
 * <p>Konfigurasyon suite basinda bir kez cozulur ve tum thread'ler ayni salt-okunur
 * ornegi paylasir.
 */
public final class ConfigLoader {

    private static final Logger log = LoggerFactory.getLogger(ConfigLoader.class);

    private static final String ENV_KEY = "env";
    private static final String DEFAULT_ENV = "local";

    /** Kimlik bilgilerinin dosyaya yazilmadan verilebilmesi icin ortam degiskeni adlari. */
    private static final String ENV_USERNAME = "CRM_UI_USERNAME";
    private static final String ENV_PASSWORD = "CRM_UI_PASSWORD";

    private static volatile FrameworkConfig instance;

    private ConfigLoader() {
    }

    public static FrameworkConfig get() {
        if (instance == null) {
            synchronized (ConfigLoader.class) {
                if (instance == null) {
                    instance = load();
                }
            }
        }
        return instance;
    }

    private static synchronized FrameworkConfig load() {
        // @Sources icindeki ${env} ifadesi sistem parametresinden cozulur.
        String env = System.getProperty(ENV_KEY);
        if (env == null || env.isBlank()) {
            env = DEFAULT_ENV;
            System.setProperty(ENV_KEY, env);
        }

        // Ortam degiskenlerini sistem parametresine tasi: en yuksek oncelik ve
        // kimlik bilgisinin repoya girmemesi boylece saglanir.
        promoteEnvVariable(ENV_USERNAME, "ui.username");
        promoteEnvVariable(ENV_PASSWORD, "ui.password");

        FrameworkConfig config = ConfigFactory.create(FrameworkConfig.class, System.getProperties());
        validate(config, env);

        log.info("Konfigurasyon yuklendi | env={} | baseUrl={} | browser={} | headless={} | dil={}",
                config.env(), config.baseUrl(), config.browser(), config.headless(), config.uiLanguage());

        return config;
    }

    private static void promoteEnvVariable(String envName, String propertyKey) {
        String value = System.getenv(envName);
        if (value != null && !value.isBlank() && System.getProperty(propertyKey) == null) {
            System.setProperty(propertyKey, value);
        }
    }

    /**
     * Eksik konfigurasyonu ilk testte degil, suite baslarken ve acik bir mesajla yakalar.
     * Ozellikle dev/test ortamlarinin sessizce localhost'a dusmesini engeller.
     */
    private static void validate(FrameworkConfig config, String env) {
        requireValue(config.baseUrl(), "base.url", env);
        requireValue(config.apiBaseUrl(), "api.base.url", env);
        requireValue(config.username(), "ui.username (veya " + ENV_USERNAME + ")", env);
        requireValue(config.password(), "ui.password (veya " + ENV_PASSWORD + ")", env);

        if (config.baseUrl().endsWith("/")) {
            throw new FrameworkException(
                    "base.url sonunda '/' olmamalidir (mevcut: " + config.baseUrl() + ")");
        }
        if (config.timeoutMultiplier() <= 0) {
            throw new FrameworkException(
                    "timeout.multiplier pozitif olmalidir (mevcut: " + config.timeoutMultiplier() + ")");
        }
    }

    private static void requireValue(String value, String key, String env) {
        if (value == null || value.isBlank()) {
            throw new FrameworkException(String.format(
                    "'%s' ayari '%s' ortami icin tanimli degil. "
                            + "src/test/resources/config/%s.properties dosyasina ekleyin "
                            + "veya -D%s=... ile gecin.",
                    key, env, env, key.split(" ")[0]));
        }
    }
}
