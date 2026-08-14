package com.etiya.crm.shared.contracts.rsrcspec;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

@Schema(description = SwaggerText.RSRC_SPEC_RESPONSE_DESCRIPTION)
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
