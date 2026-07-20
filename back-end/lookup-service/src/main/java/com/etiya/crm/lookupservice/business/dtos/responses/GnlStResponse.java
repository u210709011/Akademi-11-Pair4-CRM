package com.etiya.crm.lookupservice.business.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Genel durum grubu.")
public record GnlStResponse(

        Long gnlStId,
        String name,
        String descr,
        String shrtCode,
        boolean active,
        String entCodeName,
        String entName,
        Instant cdate,
        String cuser,
        Instant udate,
        String uuser) {
}
