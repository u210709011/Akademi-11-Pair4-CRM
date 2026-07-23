package com.etiya.crm.shared.contracts.gnlchar;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "lookup-service PUT /api/v1/characteristics/{id} istek govdesi.")
public record UpdateGnlCharRequest(

        @Schema(description = "Karakteristik adi.", example = "Renk")
        @NotBlank @Size(max = 100)
        String name,

        @Schema(description = "Aciklama.", example = "Urun rengi")
        @NotBlank @Size(max = 100)
        String descr,

        @Schema(description = "Deger saglayici sinif (opsiyonel).", example = "com.etiya.crm.ColorProvider")
        @Size(max = 100)
        String prvdrCls,

        @Schema(description = "Aktif mi.", example = "true")
        @NotNull
        Boolean active) {
}
