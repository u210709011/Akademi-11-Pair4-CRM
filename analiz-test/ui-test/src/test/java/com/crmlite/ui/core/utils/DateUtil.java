package com.crmlite.ui.core.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Tarih yardimcilari.
 *
 * <p>Gereksinim dokumaninda dogum tarihi formati <b>DD/MM/YYYY</b> olarak tanimli
 * (FR-003 / FR-004 validasyon tablolari) ve sinir degerler 01/01/1900 ile bugundur.
 */
public final class DateUtil {

    /** FR-003 / FR-004: dokumanda tanimli goruntuleme formati. */
    public static final DateTimeFormatter UI_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** FR-003 / FR-004: izin verilen en eski dogum tarihi (dahil). */
    public static final LocalDate MIN_BIRTH_DATE = LocalDate.of(1900, 1, 1);

    private DateUtil() {
    }

    public static String format(LocalDate date) {
        return date.format(UI_DATE);
    }

    public static LocalDate parse(String value) {
        return LocalDate.parse(value, UI_DATE);
    }

    public static String today() {
        return format(LocalDate.now());
    }

    /** Sinir degeri: gecerli en erken tarih. */
    public static String minValidBirthDate() {
        return format(MIN_BIRTH_DATE);
    }

    /** Sinir degeri: reddedilmesi gereken, 1900 oncesi tarih. */
    public static String justBeforeMinBirthDate() {
        return format(MIN_BIRTH_DATE.minusDays(1));
    }

    /** Sinir degeri: reddedilmesi gereken gelecek tarih. */
    public static String tomorrow() {
        return format(LocalDate.now().plusDays(1));
    }

    public static String yearsAgo(int years) {
        return format(LocalDate.now().minusYears(years));
    }
}
