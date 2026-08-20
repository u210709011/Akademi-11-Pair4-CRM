package com.crmlite.ui.data.builder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Kosuma ozel kisa kimlik. Uretilen tum test verisi bununla etiketlenir.
 *
 * <p>Iki ise yarar:
 * <ul>
 *   <li>Ayni ortamda es zamanli / tekrarli kosumlar birbirinin verisiyle catismaz</li>
 *   <li>Temizlenemeyen kayitlarin hangi kosumdan kaldigi veritabaninda anlasilir
 *       (FR-011 bosluğu nedeniyle fatura hesabi acilmis musteriler silinemiyor)</li>
 * </ul>
 *
 * <p>{@code -DrunId=...} ile disaridan da verilebilir (CI is numarasi gibi).
 */
public final class TestRunId {

    private static final String VALUE = resolve();

    private TestRunId() {
    }

    public static String value() {
        return VALUE;
    }

    private static String resolve() {
        String provided = System.getProperty("runId");
        if (provided != null && !provided.isBlank()) {
            return sanitize(provided);
        }
        long stamp = Long.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmss")));
        return toLetters(stamp);
    }

    /**
     * Yalnizca harflerden olusan bir kimlik uretir.
     *
     * <p><b>Kritik:</b> Bu deger uretilen musteri ad/soyadlarina ekleniyor ve API
     * "Name should contain letters only." kurali ile rakam iceren adlari 400 ile
     * reddediyor. Bu yuzden sayisal zaman damgasi 26 tabanina cevrilerek harfe donusturulur.
     */
    public static String toLetters(long value) {
        StringBuilder builder = new StringBuilder();
        long remaining = Math.abs(value);
        do {
            builder.append((char) ('a' + (remaining % 26)));
            remaining /= 26;
        } while (remaining > 0);
        return builder.toString();
    }

    /** Disaridan verilen runId'de rakam ve isaretler harflere indirgenir. */
    private static String sanitize(String value) {
        StringBuilder builder = new StringBuilder();
        for (char c : value.toCharArray()) {
            if (Character.isLetter(c)) {
                builder.append(c);
            } else if (Character.isDigit(c)) {
                builder.append((char) ('a' + (c - '0')));
            }
        }
        return builder.isEmpty() ? "run" : builder.toString();
    }
}
