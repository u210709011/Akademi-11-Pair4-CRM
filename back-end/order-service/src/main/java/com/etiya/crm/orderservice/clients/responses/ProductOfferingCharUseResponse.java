package com.etiya.crm.orderservice.clients.responses;

// FR-015 ACC-002/TC-015-44: finishOrder'da her item icin, teklifin mandatory=true+active=true
// isaretli karakteristiklerinin doldurulmus olup olmadigini kontrol etmek icin - bkz.
// CustOrdManager.ensureMandatoryCharacteristicsProvided.
public record ProductOfferingCharUseResponse(

    Long productOfferingCharUseId,
    Long productOfferingId,
    Long characteristicId,
    String characteristicName,
    Boolean mandatory,
    Boolean active
) {

}
