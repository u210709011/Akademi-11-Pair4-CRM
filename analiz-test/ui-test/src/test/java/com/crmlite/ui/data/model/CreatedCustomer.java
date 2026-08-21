package com.crmlite.ui.data.model;

import java.util.List;

/**
 * API ile olusturulmus bir test musterisi: gonderilen veri + sunucudan donen kimlikler.
 *
 * <p>Testler bu nesne uzerinden hem "ne gonderdik" (beklenen degerler) hem de
 * "nereye gidecegiz" ({@code custId}) bilgisine ulasir.
 */
public record CreatedCustomer(
        long custId,
        Long partyRoleId,
        String accountNo,
        Integer defaultAccountTypeId,
        CustomerData data) {

    public IndividualInfo individual() {
        return data.individual();
    }

    public ContactInfo contact() {
        return data.contact();
    }

    public List<AddressInfo> addresses() {
        return data.addresses();
    }

    public String nationalId() {
        return data.individual().nationalId();
    }

    public String customerId() {
        return String.valueOf(custId);
    }

    /** Musteri detay ekraninin rotasi. */
    public String detailPath() {
        return "/detail-customer/" + custId;
    }
}
