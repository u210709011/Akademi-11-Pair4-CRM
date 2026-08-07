package com.etiya.crm.productservice.business.dtos.requests.CampaignOffering;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCampaignOfferingRequest {

    @NotNull(message = "Kampanya id alanı zorunludur")
    private Long campaignId;

    @NotNull(message = "Product Offer id alanı zorunludur")
    private Long productOfferingId;

    private Integer priority;

    @NotNull(message = "İndirim yüzdesi zorunludur")
    @DecimalMin(value = "0", message = "İndirim yüzdesi negatif olamaz")
    @DecimalMax(value = "100", message = "İndirim yüzdesi 100'ü geçemez")
    private BigDecimal discountPct;

    @NotNull(message = "Start Date alanı zorunludur")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "active alanı zorunludur")
    private Boolean active;
}
