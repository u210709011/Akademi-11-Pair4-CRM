package com.etiya.crm.shared.contracts.gnlchar;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

@Schema(description = SwaggerText.GNL_CHAR_UPDATE_REQUEST_DESCRIPTION)
public record UpdateGnlCharRequest(

        @Schema(description = SwaggerText.GNL_CHAR_NAME_DESCRIPTION, example = "Renk")
        @NotBlank @Size(max = 100)
        String name,

        @Schema(description = SwaggerText.GNL_CHAR_DESCR_DESCRIPTION, example = "Urun rengi")
        @NotBlank @Size(max = 100)
        String descr,

        @Schema(description = SwaggerText.GNL_CHAR_PRVDR_CLS_DESCRIPTION, example = "com.etiya.crm.ColorProvider")
        @Size(max = 100)
        String prvdrCls,

        @Schema(description = SwaggerText.GNL_CHAR_ACTIVE_DESCRIPTION, example = "true")
        @NotNull
        Boolean active) {
}
