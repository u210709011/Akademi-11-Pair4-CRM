package com.etiya.crm.orderservice.business.dtos.requests;

import java.util.List;

import jakarta.validation.Valid;

// Product Configuration ekraninda kullanicinin girdigi karakteristikler + servis adresini
// kaydeder. WAIT durumundaki bir siparise, sayfa yenilense de kaybolmasin diye adim adim
// (idempotent, replace-all) yazilir - finishOrder'in kendisi bu veriyi tekrar istemez.
public record OrderConfigurationRequest(

    @Valid List<ProdCharValRequest> charVals,
    Long addressId,
    @Valid AddressInfoRequest newAddress
) {

}
