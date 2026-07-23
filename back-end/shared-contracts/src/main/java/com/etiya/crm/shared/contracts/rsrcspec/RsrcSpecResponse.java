package com.etiya.crm.shared.contracts.rsrcspec;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Kaynak spesifikasyonu.")
public record RsrcSpecResponse(

        Long rsrcSpecId,
        String name,
        String descr,
        Long stId,
        String rsrcCode,
        Instant cdate,
        String cuser,
        Instant udate,
        String uuser) {
}
