package com.crmlite.ui.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Iletisim bilgileri — FR-003 3. adim.
 *
 * <p>Zorunlu: {@code email}, {@code mobilePhone}. Opsiyonel: {@code homePhone}, {@code fax}.
 * Telefonlar UI'da +90 oneki disinda, yalnizca 10 hane olarak girilir.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record ContactInfo(
        String email,
        String mobilePhone,
        String homePhone,
        String fax) {
}
