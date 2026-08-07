package com.etiya.crm.orderservice.clients.requests;

// FR-021: finishOrder'da her item'in secilmis karakteristiklerini (CustOrdCharVal) gercek
// Product kaydina (product-service) islemek icin.
public record CreateProductCharacteristicValueRequest(

    Long productId,
    Long characteristicId,
    Long characteristicValueId,
    String value,
    String statusCode
) {

}
