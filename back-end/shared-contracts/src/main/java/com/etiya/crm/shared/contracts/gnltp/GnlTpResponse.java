package com.etiya.crm.shared.contracts.gnltp;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

@Schema(description = SwaggerText.GNL_TP_RESPONSE_DESCRIPTION)
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
