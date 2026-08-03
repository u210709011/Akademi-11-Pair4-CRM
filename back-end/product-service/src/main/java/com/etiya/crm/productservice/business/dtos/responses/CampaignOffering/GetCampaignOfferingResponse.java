package com.etiya.crm.productservice.business.dtos.responses.CampaignOffering;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetCampaignOfferingResponse {
    private Long campaignOfferingId;      // PK
    private Long campaignId;              // ilişki 1 → ID
    private Long productOfferingId;       // ilişki 2 → ID
    private String productOfferingName;   // ← donmuş isim, response'ta VAR
    private Integer priority;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
}
