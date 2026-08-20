package com.etiya.crm.shared.contracts.gnlst;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

@Schema(description = SwaggerText.GNL_ST_CREATE_REQUEST_DESCRIPTION)
public record CreateGnlStRequest(

        @Schema(description = SwaggerText.GNL_ST_NAME_DESCRIPTION, example = "Aktif")
        @NotBlank @Size(max = 100)
        String name,

        @Schema(description = SwaggerText.GNL_ST_DESCR_DESCRIPTION, example = "Aktif")
        @NotBlank @Size(max = 100)
        String descr,

        @Schema(description = SwaggerText.GNL_ST_SHRT_CODE_DESCRIPTION, example = "ACTIVE")
        @NotBlank @Size(max = 15)
        String shrtCode,

        @Schema(description = SwaggerText.GNL_ST_ACTIVE_DESCRIPTION, example = "true")
        @NotNull
        Boolean active,

        @Schema(description = SwaggerText.GNL_ST_ENT_CODE_NAME_DESCRIPTION, example = "CUST_STATUS")
        @NotBlank @Size(max = 100)
        String entCodeName,

        @Schema(description = SwaggerText.GNL_ST_ENT_NAME_DESCRIPTION, example = "CUST_STATUS")
        @Size(max = 100)
        String entName) {
}
