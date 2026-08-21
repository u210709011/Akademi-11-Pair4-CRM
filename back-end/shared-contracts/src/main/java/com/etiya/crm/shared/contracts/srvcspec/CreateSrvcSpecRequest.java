package com.etiya.crm.shared.contracts.srvcspec;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

@Schema(description = SwaggerText.SRVC_SPEC_CREATE_REQUEST_DESCRIPTION)
public record CreateSrvcSpecRequest(

        @Schema(description = SwaggerText.SRVC_SPEC_NAME_DESCRIPTION, example = "Mobil Hat Aktivasyonu")
        @NotBlank @Size(max = 100)
        String name,

        @Schema(description = SwaggerText.SRVC_SPEC_DESCR_DESCRIPTION, example = "Yeni mobil hat aktivasyon servisi")
        @NotBlank @Size(max = 100)
        String descr,

        @Schema(description = SwaggerText.SRVC_SPEC_SRVC_CODE_DESCRIPTION, example = "MOBILE_ACTIVATION")
        @NotBlank @Size(max = 100)
        String srvcCode,

        @Schema(description = SwaggerText.SPEC_ST_ID_DESCRIPTION, example = "1")
        @NotNull
        Long stId) {
}
