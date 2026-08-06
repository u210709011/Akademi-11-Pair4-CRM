package com.etiya.crm.orderservice.business.dtos.responses;

// BR-03 "Already Active": bir hesabin PROCESSING/FINISHED durumundaki bir siparisiyle
// zaten sahip oldugu teklif - Offer Selection ekraninda "Already Active" rozeti icin.
public record ActiveOfferResponse(

    Long prodOfrId,
    Long custOrdItemId,
    Long prodId
) {

}
