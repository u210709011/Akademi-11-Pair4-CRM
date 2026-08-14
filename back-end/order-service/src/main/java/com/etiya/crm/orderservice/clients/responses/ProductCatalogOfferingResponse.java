package com.etiya.crm.orderservice.clients.responses;

// BR-05 (genisletilmis): bir teklifin ait oldugu katalog kategorisi (Internet/Mobile/TV) - hesapta
// o kategoriden FARKLI bir teklif zaten aktifse de yenisi eklenemez (bkz. ensureOfferNotAlreadyActive).
public record ProductCatalogOfferingResponse(

    Long productCatalogId,
    Long productOfferingId
) {

}
