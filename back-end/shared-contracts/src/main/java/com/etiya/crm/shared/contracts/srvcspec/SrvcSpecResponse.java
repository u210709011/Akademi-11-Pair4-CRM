package com.etiya.crm.shared.contracts.srvcspec;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

@Schema(description = SwaggerText.SRVC_SPEC_RESPONSE_DESCRIPTION)
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
