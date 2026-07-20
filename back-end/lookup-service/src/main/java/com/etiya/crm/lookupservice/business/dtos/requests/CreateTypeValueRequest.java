package com.etiya.crm.lookupservice.business.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "POST /api/v1/type-values istek govdesi.")
public record CreateTypeValueRequest(

        @Schema(description = "Hangi tabloya ait: GNL_TP veya GNL_ST.", example = "GNL_TP")
        @NotBlank
        @Pattern(regexp = "GNL_TP|GNL_ST")
        String tableName,

        @Schema(description = "tableName GNL_TP ise gnl_tp_id, GNL_ST ise gnl_st_id.", example = "1")
        @NotNull
        Long fieldName,

        @Schema(description = "Gosterim metni.", example = "Standart Hesap")
        @NotBlank @Size(max = 200)
        String description,

        @Schema(description = "Kisa kod.", example = "STANDARD")
        @Size(max = 50)
        String value,

        @Schema(description = "Bu degeri kullanan servis/modul (bilgi amacli).", example = "customer-service")
        @Size(max = 50)
        String usingModuleName) {
}
