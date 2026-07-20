package com.etiya.crm.lookupservice.business.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Bir GNL_TP/GNL_ST grubuna ait deger.")
public record TypeValueResponse(

        Long typeValueId,
        String tableName,
        Long fieldName,
        String description,
        String value,
        String usingModuleName,
        Instant cdate,
        String cuser,
        Instant udate,
        String uuser) {
}
