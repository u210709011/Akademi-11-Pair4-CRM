package com.etiya.crm.shared.contracts.gnlcharval;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

/** charId kasten yok - hangi GNL_CHAR'a ait oldugu degistirilemez. */
@Schema(description = SwaggerText.GNL_CHAR_VAL_UPDATE_REQUEST_DESCRIPTION)
public record UpdateGnlCharValRequest(

        @Schema(description = SwaggerText.GNL_CHAR_VAL_DFLT_DESCRIPTION, example = "false")
        @NotNull
        Boolean dflt,

        @Schema(description = SwaggerText.GNL_CHAR_VAL_VAL_DESCRIPTION, example = "Kirmizi")
        @Size(max = 100)
        String val,

        @Schema(description = SwaggerText.GNL_CHAR_VAL_SHRT_CODE_DESCRIPTION, example = "RED")
        @NotBlank @Size(max = 100)
        String shrtCode,

        @Schema(description = SwaggerText.GNL_CHAR_VAL_SDATE_DESCRIPTION, example = "2026-01-01")
        @NotNull
        LocalDate sdate,

        @Schema(description = SwaggerText.GNL_CHAR_VAL_EDATE_DESCRIPTION, example = "2026-12-31")
        LocalDate edate,

        @Schema(description = SwaggerText.GNL_CHAR_VAL_ACTIVE_DESCRIPTION, example = "true")
        @NotNull
        Boolean active) {
}
