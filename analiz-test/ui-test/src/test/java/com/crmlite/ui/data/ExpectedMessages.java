package com.crmlite.ui.data;

import com.crmlite.ui.core.config.ConfigLoader;
import com.crmlite.ui.core.exceptions.FrameworkException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Beklenen UI metinlerinin tek kaynagi.
 *
 * <p>Metinler test kodunun icine gomulmez: dokumandaki celiskiler netlestiginde
 * ya da uygulama metni degistiginde tek bir properties dosyasi guncellenir.
 *
 * <p>Dosya, yapilandirilmis UI diline gore secilir
 * ({@code expected/messages_{lang}.properties}).
 */
public final class ExpectedMessages {

    private static volatile Properties messages;

    private ExpectedMessages() {
    }

    /** Anahtar bulunamazsa acik bir hata firlatir — sessizce {@code null} donmez. */
    public static String get(String key) {
        String value = load().getProperty(key);
        if (value == null) {
            throw new FrameworkException(String.format(
                    "Beklenen mesaj anahtari bulunamadi: '%s' (%s)", key, resourceName()));
        }
        return value;
    }

    /** Anahtar yoksa {@code null} dondurur (opsiyonel metinler icin). */
    public static String find(String key) {
        return key == null ? null : load().getProperty(key);
    }

    public static boolean contains(String key) {
        return load().containsKey(key);
    }

    private static Properties load() {
        if (messages == null) {
            synchronized (ExpectedMessages.class) {
                if (messages == null) {
                    messages = readFromClasspath();
                }
            }
        }
        return messages;
    }

    private static Properties readFromClasspath() {
        String resource = resourceName();
        Properties properties = new Properties();
        try (InputStream in = ExpectedMessages.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new FrameworkException("Beklenen mesaj dosyasi bulunamadi: " + resource);
            }
            properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new FrameworkException("Beklenen mesaj dosyasi okunamadi: " + resource, e);
        }
        return properties;
    }

    private static String resourceName() {
        return "expected/messages_" + ConfigLoader.get().uiLanguage() + ".properties";
    }
}
