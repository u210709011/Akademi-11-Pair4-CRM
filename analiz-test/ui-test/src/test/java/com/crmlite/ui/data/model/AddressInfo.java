package com.crmlite.ui.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Adres — FR-003 2. adim ve FR-005.
 *
 * <p><b>Dikkat — alan adlari istek ve yanitta farklidir</b> (API sozlesmesinden):
 * <ul>
 *   <li>Istek  ({@code AddressEditRequest}) : {@code streetName}, {@code buildingName}, {@code addressDesc}</li>
 *   <li>Yanit  ({@code AddressResponse})    : {@code streetName}, {@code houseName},    {@code addrDesc}</li>
 * </ul>
 * Bu kayit <b>istek</b> tarafini modeller; yanit icin {@link AddressResponse} kullanilir.
 *
 * <p>Onboarding sirasinda listenin <b>ilk adresi</b> sunucu tarafinda birincil olur.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record AddressInfo(
        Integer cityId,
        String streetName,
        String buildingName,
        String addressDesc,
        Boolean primary) {

    // Sehir id'si BURADA SABITLENMEZ: lookup seed'i degistiginde 201 -> 5 kaydi ve
    // AddressInfo.cityId uzerindeki @ExistsInLookupGroup dogrulamasi adres iceren tum
    // musteri olusturma akisini kirdi. Gecerli id icin LookupApi.cityAnkara() kullanilir.

    /** Onboarding govdesi {@code primary} alani icermez; onu disarida birakan kopya. */
    public AddressInfo withoutPrimaryFlag() {
        return new AddressInfo(cityId, streetName, buildingName, addressDesc, null);
    }

    public AddressInfo asPrimary(boolean value) {
        return new AddressInfo(cityId, streetName, buildingName, addressDesc, value);
    }
}
