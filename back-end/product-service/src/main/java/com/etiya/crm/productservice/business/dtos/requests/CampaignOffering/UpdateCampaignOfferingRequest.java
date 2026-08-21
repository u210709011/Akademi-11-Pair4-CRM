package com.etiya.crm.productservice.business.dtos.requests.CampaignOffering;

import com.etiya.crm.productservice.constants.MessageKeys;
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
public class UpdateCampaignOfferingRequest {

    @NotNull(message = "{" + MessageKeys.CAMPAIGN_ID_REQUIRED + "}")
    private Long campaignId;

    @NotNull(message = "{" + MessageKeys.PRODUCT_OFFERING_ID_REQUIRED + "}")
    private Long productOfferingId;

    private Integer priority;

    @NotNull(message = "{" + MessageKeys.DISCOUNT_PCT_REQUIRED + "}")
    @DecimalMin(value = "0", message = "{" + MessageKeys.DISCOUNT_PCT_MIN + "}")
    @DecimalMax(value = "100", message = "{" + MessageKeys.DISCOUNT_PCT_MAX + "}")
    private BigDecimal discountPct;

    @NotNull(message = "{" + MessageKeys.START_DATE_REQUIRED + "}")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "{" + MessageKeys.ACTIVE_REQUIRED + "}")
    private Boolean active;

}
