package com.etiya.crm.shared.contracts.srvcspec;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Servis spesifikasyonu.")
public record SrvcSpecResponse(

        Long srvcSpecId,
        String name,
        String descr,
        String srvcCode,
        Long stId,
        Instant cdate,
        String cuser,
        Instant udate,
        String uuser) {
}
