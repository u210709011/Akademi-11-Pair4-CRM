package com.crmlite.ui.data.model;

import com.crmlite.ui.data.api.LookupApi;

/**
 * Cinsiyet degerleri.
 *
 * <p><b>Id'ler sabit degildir</b> — lookup-service'ten kisa kod ({@code MALE}/{@code FEMALE})
 * uzerinden calisma zamaninda cozulur. Once 1/2 olarak sabitlenmislerdi; seed migration'i
 * degistiginde gercek id'ler 3/4 oldu ve {@code IndividualInfo.genderId} uzerindeki
 * {@code @ExistsInLookupGroup} dogrulamasi devreye girince musteri olusturma kirildi.
 * Sehir id'siyle birebir ayni tuzak — bkz. {@link LookupApi}.
 */
public enum Gender {

    MALE("MALE", "Male"),
    FEMALE("FEMALE", "Female");

    private final String shrtCode;
    private final String label;

    Gender(String shrtCode, String label) {
        this.shrtCode = shrtCode;
        this.label = label;
    }

    /** Lookup'tan cozulen gecerli id (surec omru boyunca onbelleklenir). */
    public int id() {
        return LookupApi.gender(shrtCode);
    }

    /** Lookup'taki kisa kod — id'yi cozmek icin kullanilir. */
    public String shrtCode() {
        return shrtCode;
    }

    /** UI {@code <select>} icin string deger. Secenek degerleri lookup id'sidir. */
    public String value() {
        return String.valueOf(id());
    }

    /** Ekranda gosterilen etiket (EN). Liste secimlerinde deger yerine bunu tercih et. */
    public String label() {
        return label;
    }

    public static Gender fromId(int id) {
        for (Gender gender : values()) {
            if (gender.id() == id) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Bilinmeyen gender id: " + id);
    }
}
