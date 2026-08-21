package com.etiya.crm.shared.contracts.gnlcharval;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

@Schema(description = SwaggerText.GNL_CHAR_VAL_RESPONSE_DESCRIPTION)
public record GnlCharValResponse(

        Long charValId,
        Long charId,
        boolean dflt,
        String val,
        String shrtCode,
        LocalDate sdate,
        LocalDate edate,
        boolean active,
        Instant cdate,
        String cuser,
        Instant udate,
        String uuser) {
}
