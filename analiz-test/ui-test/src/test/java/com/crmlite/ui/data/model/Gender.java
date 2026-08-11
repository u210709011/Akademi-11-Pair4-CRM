package com.crmlite.ui.data.model;

/**
 * Cinsiyet degerleri.
 *
 * <p>Id'ler lookup-service'ten gelir; UI'daki {@code <select>} secenek degerleri
 * ile birebir aynidir ({@code value="1"} / {@code value="2"}).
 */
public enum Gender {

    MALE(1, "Male"),
    FEMALE(2, "Female");

    private final int id;
    private final String label;

    Gender(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int id() {
        return id;
    }

    /** UI {@code <select>} icin string deger. */
    public String value() {
        return String.valueOf(id);
    }

    /** Ekranda gosterilen etiket (EN). */
    public String label() {
        return label;
    }

    public static Gender fromId(int id) {
        for (Gender gender : values()) {
            if (gender.id == id) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Bilinmeyen gender id: " + id);
    }
}
