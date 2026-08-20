package com.etiya.crm.shared.contracts.gnlst;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

@Schema(description = SwaggerText.GNL_ST_RESPONSE_DESCRIPTION)
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
