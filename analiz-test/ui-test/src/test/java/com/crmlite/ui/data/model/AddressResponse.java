package com.crmlite.ui.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * {@code GET/POST/PUT /api/v1/customers/{custId}/addresses} yaniti.
 *
 * <p>Yanit alan adlari istek govdesinden farklidir: {@code houseName} (istekte
 * {@code buildingName}) ve {@code addrDesc} (istekte {@code addressDesc}).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AddressResponse(
        Long id,
        Long rowId,
        Integer dataTypeId,
        Integer cityId,
        String streetName,
        String houseName,
        String addrDesc,
        Boolean primary) {

    public boolean isPrimary() {
        return Boolean.TRUE.equals(primary);
    }

    /** UI'daki {@code .address-card-line} ile ayni gosterim. */
    public String displayLine() {
        return (streetName + " " + houseName).trim();
    }
}
