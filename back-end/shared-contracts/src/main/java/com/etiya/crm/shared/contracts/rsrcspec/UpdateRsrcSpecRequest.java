package com.etiya.crm.shared.contracts.rsrcspec;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

@Schema(description = SwaggerText.RSRC_SPEC_UPDATE_REQUEST_DESCRIPTION)
public record UpdateRsrcSpecRequest(

        @Schema(description = SwaggerText.RSRC_SPEC_NAME_DESCRIPTION, example = "SIM Kart")
        @NotBlank @Size(max = 100)
        String name,

        @Schema(description = SwaggerText.RSRC_SPEC_DESCR_DESCRIPTION, example = "Fiziksel SIM kart kaynagi")
        @NotBlank @Size(max = 100)
        String descr,

        @Schema(description = SwaggerText.SPEC_ST_ID_DESCRIPTION, example = "1")
        @NotNull
        Long stId,

        @Schema(description = SwaggerText.RSRC_SPEC_RSRC_CODE_DESCRIPTION, example = "SIM")
        @Size(max = 30)
        String rsrcCode) {
}
