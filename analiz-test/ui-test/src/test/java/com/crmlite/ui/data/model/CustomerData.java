package com.crmlite.ui.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * {@code POST /api/v1/customers/onboarding} istek govdesi ve ayni zamanda
 * UI sihirbazini doldurmak icin kullanilan tam musteri sablonu.
 *
 * <p>Ayni nesne hem API ile hizli kurulum (arrange) hem de UI'dan adim adim giris
 * icin kullanilir; boylece iki yol arasinda veri tutarsizligi olusmaz.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomerData(
        IndividualInfo individual,
        List<AddressInfo> addresses,
        ContactInfo contact) {

    /** Onboarding govdesinde adreslerde {@code primary} alani gonderilmez. */
    public CustomerData forOnboarding() {
        return new CustomerData(
                individual,
                addresses.stream().map(AddressInfo::withoutPrimaryFlag).toList(),
                contact);
    }
}
