package com.etiya.crm.productservice.business.dtos.responses.CampaignOffering;

import java.time.LocalDate;

public class GetAllCampaignOfferingResponse {
    private Long campaignOfferingId;      // PK
    private Long campaignId;              // ilişki 1 → ID
    private Long productOfferingId;       // ilişki 2 → ID
    private String productOfferingName;   // ← donmuş isim, response'ta VAR
    private Integer priority;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
}
