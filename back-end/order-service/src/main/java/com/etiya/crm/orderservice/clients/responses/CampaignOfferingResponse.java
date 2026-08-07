package com.etiya.crm.orderservice.clients.responses;

import java.math.BigDecimal;
import java.time.LocalDate;

// product-service'in campaign+offering eslesmesi - discountedPct/discountedPrice'i server-side hesaplayip donuyor.
public record CampaignOfferingResponse(

    Long campaignOfferingId,
    Long campaignId,
    Long productOfferingId,
    String productOfferingName,
    Integer priority,
    BigDecimal discountPct,
    BigDecimal discountedPrice,
    LocalDate startDate,
    LocalDate endDate,
    boolean active
) {

}
