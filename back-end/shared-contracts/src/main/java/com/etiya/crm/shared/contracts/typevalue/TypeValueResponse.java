package com.etiya.crm.shared.contracts.typevalue;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Bir is tablosuna atanmis polimorfik tip etiketi.")
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
