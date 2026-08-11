package com.crmlite.ui.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON fixture'larindaki tek bir validasyon satiri.
 *
 * <p>Tum validasyon matrisleri ayni sekli paylasir; boylece tek bir model ve tek bir
 * DataProvider mekanizmasi ile FR-001…FR-005'in tamami beslenebilir.
 *
 * <p>{@code expectedMessageKey}, {@code expected/messages_en.properties} icindeki
 * anahtardir — beklenen metin testin icine gomulmez.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ValidationCase(
        @JsonProperty("case") String description,
        String field,
        String filter,
        String value,
        String username,
        String password,
        Boolean submitEnabled,
        Boolean nextEnabled,
        Boolean createEnabled,
        Boolean saveEnabled,
        Boolean expectError,
        String expectedMessageKey,
        String acc) {

    /**
     * Ilgili ekranin ana butonunun aktif olmasi bekleniyor mu.
     * Fixture'a gore {@code submitEnabled/nextEnabled/createEnabled/saveEnabled}
     * alanlarindan hangisi doluysa o kullanilir.
     */
    public boolean expectsActionEnabled() {
        if (submitEnabled != null) {
            return submitEnabled;
        }
        if (nextEnabled != null) {
            return nextEnabled;
        }
        if (createEnabled != null) {
            return createEnabled;
        }
        if (saveEnabled != null) {
            return saveEnabled;
        }
        return false;
    }

    public boolean expectsError() {
        return Boolean.TRUE.equals(expectError) || expectedMessageKey != null;
    }

    /** TestNG raporunda okunabilir isim. */
    @Override
    public String toString() {
        return (acc == null ? "" : acc + " | ") + description;
    }
}
