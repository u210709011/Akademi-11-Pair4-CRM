package com.crmlite.ui.data.builder;

import com.crmlite.ui.data.api.LookupApi;
import com.crmlite.ui.data.model.AddressInfo;

/**
 * Adres uretici. Varsayilan degerler gecerlidir; negatif senaryolar
 * {@code without*} / {@code with*} cagrilariyla tek satirda tureti.
 */
public final class AddressBuilder {

    // Varsayilan sehir build() aninda cozulur (bkz. LookupApi): kurucu icinde cozmek,
    // negatif senaryolar dahil her builder olusturmada gereksiz bir HTTP cagrisi demek olurdu.
    // cityOverridden, "varsayilan" ile "bilincli olarak null" (withoutCity) durumunu ayirir.
    private Integer cityId;
    private boolean cityOverridden;
    private String streetName = "Cumhuriyet Cad.";
    private String buildingName = "No:1 D:2";
    private String addressDesc = "Ev adresi";
    private Boolean primary;

    private AddressBuilder() {
    }

    public static AddressBuilder aValidAddress() {
        return new AddressBuilder();
    }

    /** Ayirt edilebilir adresler uretmek icin (ornegin 5 adres limiti testinde). */
    public static AddressBuilder aValidAddress(int index) {
        return new AddressBuilder()
                .withStreetName("Cumhuriyet Cad. " + index)
                .withBuildingName("No:" + (index + 1) + " D:2")
                .withDescription(index == 0 ? "Ev adresi" : "Ek adres " + index);
    }

    public AddressBuilder withCityId(Integer cityId) {
        this.cityId = cityId;
        this.cityOverridden = true;
        return this;
    }

    public AddressBuilder withStreetName(String streetName) {
        this.streetName = streetName;
        return this;
    }

    public AddressBuilder withBuildingName(String buildingName) {
        this.buildingName = buildingName;
        return this;
    }

    public AddressBuilder withDescription(String addressDesc) {
        this.addressDesc = addressDesc;
        return this;
    }

    public AddressBuilder asPrimary(boolean primary) {
        this.primary = primary;
        return this;
    }

    // --- Negatif varyantlar (FR-005 validasyon tablosu) ---

    public AddressBuilder withoutCity() {
        this.cityId = null;
        this.cityOverridden = true;
        return this;
    }

    public AddressBuilder withoutStreet() {
        this.streetName = "";
        return this;
    }

    public AddressBuilder withoutBuildingName() {
        this.buildingName = "";
        return this;
    }

    public AddressBuilder withoutDescription() {
        this.addressDesc = "";
        return this;
    }

    /** Street icin "maks 200 karakter" sinir degeri. */
    public AddressBuilder withStreetOfLength(int length) {
        this.streetName = "S".repeat(Math.max(0, length));
        return this;
    }

    public AddressInfo build() {
        // Integer.valueOf ZORUNLU: cityAnkara() int dondugu icin ucuncul operator sayisal
        // terfi uygular ve iki dali da int'e cevirir - bu da withoutCity()'nin biraktigi
        // null cityId'yi unbox edip NullPointerException firlatir. Sarmalayinca operatorun
        // tipi Integer olarak kalir ve null gecebilir.
        Integer city = cityOverridden ? cityId : Integer.valueOf(LookupApi.cityAnkara());
        return new AddressInfo(city, streetName, buildingName, addressDesc, primary);
    }
}
