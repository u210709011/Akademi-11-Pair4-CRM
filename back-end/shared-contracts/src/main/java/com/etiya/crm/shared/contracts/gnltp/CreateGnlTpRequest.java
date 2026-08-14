package com.etiya.crm.shared.contracts.gnltp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

@Schema(description = SwaggerText.GNL_TP_CREATE_REQUEST_DESCRIPTION)
public record CreateGnlTpRequest(

        @Schema(description = SwaggerText.GNL_TP_NAME_DESCRIPTION, example = "Musteri Hesap")
        @NotBlank @Size(max = 100)
        String name,

        @Schema(description = SwaggerText.GNL_TP_DESCR_DESCRIPTION, example = "Musteri Hesap")
        @NotBlank @Size(max = 100)
        String descr,

        @Schema(description = SwaggerText.GNL_TP_SHRT_CODE_DESCRIPTION, example = "CUST_ACCT")
        @NotBlank @Size(max = 64)
        String shrtCode,

        @Schema(description = SwaggerText.GNL_TP_ENT_CODE_NAME_DESCRIPTION, example = "ACCOUNT_TYPE")
        @NotBlank @Size(max = 100)
        String entCodeName,

        @Schema(description = SwaggerText.GNL_TP_ENT_NAME_DESCRIPTION, example = "ACCOUNT_TYPE")
        @Size(max = 100)
        String entName,

        @Schema(description = SwaggerText.GNL_TP_ACTIVE_DESCRIPTION, example = "true")
        @NotNull
        Boolean active) {
}
