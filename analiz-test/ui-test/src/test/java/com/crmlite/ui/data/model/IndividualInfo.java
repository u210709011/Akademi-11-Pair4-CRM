package com.crmlite.ui.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Demografik bilgiler — FR-003 1. adim ve FR-004 guncelleme formu.
 *
 * <p>API sozlesmesi ile birebir ayni alan adlarini kullanir
 * ({@code POST /api/v1/customers/onboarding} icindeki {@code individual} nesnesi ve
 * {@code PUT /api/v1/customers/{custId}/individual} govdesi).
 *
 * <p>{@code birthDate} her iki tarafta da <b>DD/MM/YYYY</b> formatindadir.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record IndividualInfo(
        String firstName,
        String middleName,
        String lastName,
        String birthDate,
        Integer genderId,
        String motherName,
        String fatherName,
        String nationalId) {

    public Gender gender() {
        return genderId == null ? null : Gender.fromId(genderId);
    }

    /** Ekranda gosterilen tam ad (arama sonuclarinda karsilastirma icin). */
    public String fullName() {
        return (firstName + " " + lastName).trim();
    }
}
