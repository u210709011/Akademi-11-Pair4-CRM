package com.etiya.crm.shared.contracts.gnlchar;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

@Schema(description = SwaggerText.GNL_CHAR_RESPONSE_DESCRIPTION)
public record GnlCharResponse(

        Long charId,
        String name,
        String descr,
        String prvdrCls,
        String shrtCode,
        boolean active,
        Instant cdate,
        String cuser,
        Instant udate,
        String uuser) {
}
