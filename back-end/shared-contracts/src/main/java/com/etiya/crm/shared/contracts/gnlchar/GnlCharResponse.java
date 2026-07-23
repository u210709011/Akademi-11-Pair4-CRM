package com.etiya.crm.shared.contracts.gnlchar;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Karakteristik tanimi.")
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
