package com.etiya.crm.productservice.business.dtos.requests.CampaignOffering;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCampaignOfferingRequest {

    @NotNull(message = "Kampanya id alanı zorunludur")
    private Long campaignId;

    @NotNull(message = "Product Offer id alanı zorunludur")
    private Long productOfferingId;

    private Integer priority;

    @NotNull(message = "Start Date alanı zorunludur")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "active alanı zorunludur")
    private Boolean active;

}
