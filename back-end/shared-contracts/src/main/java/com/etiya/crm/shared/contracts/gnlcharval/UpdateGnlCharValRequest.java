package com.etiya.crm.shared.contracts.gnlcharval;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** charId kasten yok - hangi GNL_CHAR'a ait oldugu degistirilemez. */
@Schema(description = "lookup-service PUT /api/v1/characteristic-values/{id} istek govdesi.")
public record UpdateGnlCharValRequest(

        @Schema(description = "Varsayilan deger mi.", example = "false")
        @NotNull
        Boolean dflt,

        @Schema(description = "Deger metni.", example = "Kirmizi")
        @Size(max = 100)
        String val,

        @Schema(description = "Kisa kod.", example = "RED")
        @NotBlank @Size(max = 100)
        String shrtCode,

        @Schema(description = "Gecerlilik baslangic tarihi.", example = "2026-01-01")
        @NotNull
        LocalDate sdate,

        @Schema(description = "Gecerlilik bitis tarihi (opsiyonel).", example = "2026-12-31")
        LocalDate edate,

        @Schema(description = "Aktif mi.", example = "true")
        @NotNull
        Boolean active) {
}
