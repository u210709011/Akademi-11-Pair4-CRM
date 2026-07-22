package com.etiya.crm.shared.contracts.gnlcharval;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;

@Schema(description = "Karakteristik degeri.")
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
