package com.etiya.crm.productservice.business.dtos.responses.Campaign;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllCampaignResponse {
    private Long campaignId;
    private String name;
    private String descr;
    private String campaignCode;
    private LocalDate activityEndDate;
    private Long statusId;
    private boolean penalty;
}
