package com.etiya.crm.shared.contracts.gnltp;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Genel tip grubu.")
public record GnlTpResponse(

        Long gnlTpId,
        String name,
        String descr,
        String shrtCode,
        String entCodeName,
        String entName,
        boolean active,
        Instant cdate,
        String cuser,
        Instant udate,
        String uuser) {
}
