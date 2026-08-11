package com.crmlite.ui.core.utils;

import java.security.SecureRandom;

/**
 * Benzersiz 11 haneli Nationality ID uretir.
 *
 * <p>Gereksinim yalnizca "11 hane, rakam" diyor ve back-end'deki
 * {@code FakeIdentityVerificationServiceImpl} her kimligi dogruluyor. Yine de
 * <b>algoritmik olarak gecerli</b> bir TCKN uretiliyor: ileride gercek KPS entegre
 * edilir ya da checksum dogrulamasi eklenirse test verisi kirilmaz.
 *
 * <p>Kurallar: ilk hane 0 olamaz; 10. hane {@code ((tek hanelerin toplami * 7) -
 * cift hanelerin toplami) mod 10}; 11. hane {@code ilk 10 hanenin toplami mod 10}.
 */
public final class NationalIdGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private NationalIdGenerator() {
    }

    public static String next() {
        int[] digits = new int[11];

        digits[0] = 1 + RANDOM.nextInt(9); // ilk hane 0 olamaz
        for (int i = 1; i < 9; i++) {
            digits[i] = RANDOM.nextInt(10);
        }

        int oddSum = digits[0] + digits[2] + digits[4] + digits[6] + digits[8];
        int evenSum = digits[1] + digits[3] + digits[5] + digits[7];

        digits[9] = Math.floorMod(oddSum * 7 - evenSum, 10);

        int firstTenSum = 0;
        for (int i = 0; i < 10; i++) {
            firstTenSum += digits[i];
        }
        digits[10] = firstTenSum % 10;

        StringBuilder sb = new StringBuilder(11);
        for (int digit : digits) {
            sb.append(digit);
        }
        return sb.toString();
    }

    /**
     * Gecerli formatta olmayan (10 haneli) bir deger — negatif sinir testleri icin.
     */
    public static String tooShort() {
        return next().substring(0, 10);
    }

    /**
     * 12 haneli deger — negatif sinir testleri icin.
     */
    public static String tooLong() {
        return next() + "7";
    }
}
