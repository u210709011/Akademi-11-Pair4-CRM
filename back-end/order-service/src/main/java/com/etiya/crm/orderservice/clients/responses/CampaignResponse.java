package com.etiya.crm.orderservice.clients.responses;

import java.time.LocalDate;

public record CampaignResponse(

    Long campaignId,
    String name,
    String descr,
    String campaignCode,
    LocalDate activityEndDate,
    Long statusId,
    boolean penalty
) {

}
