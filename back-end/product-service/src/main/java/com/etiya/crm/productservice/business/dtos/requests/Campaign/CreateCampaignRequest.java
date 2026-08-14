package com.etiya.crm.productservice.business.dtos.requests.Campaign;

import com.etiya.crm.productservice.constants.MessageKeys;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCampaignRequest {
    @NotBlank(message = "{" + MessageKeys.NAME_REQUIRED + "}")
    @Size(max = 100, message = "{" + MessageKeys.NAME_MAX_LENGTH + "}")
    private String name;

    @NotBlank(message = "{" + MessageKeys.DESCRIPTION_REQUIRED + "}")
    @Size(max = 100, message = "{" + MessageKeys.DESCRIPTION_MAX_LENGTH + "}")
    private String descr;

    @NotBlank(message = "{" + MessageKeys.CAMPAIGN_CODE_REQUIRED + "}")
    private String campaignCode;

    private LocalDate activityEndDate;

    @NotBlank(message = "{" + MessageKeys.STATUS_CODE_REQUIRED + "}")
    private String statusCode;

    @NotNull(message = "{" + MessageKeys.CAMPAIGN_PENALTY_REQUIRED + "}")
    private Boolean penalty;
}
