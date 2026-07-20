package com.etiya.crm.lookupservice.business.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "POST /api/v1/resource-specs istek govdesi.")
public record CreateRsrcSpecRequest(

        @Schema(description = "Kaynak spec adi.", example = "SIM Kart")
        @NotBlank @Size(max = 100)
        String name,

        @Schema(description = "Aciklama.", example = "Fiziksel SIM kart kaynagi")
        @NotBlank @Size(max = 100)
        String descr,

        @Schema(description = "GNL_ST grubundaki durum id'si (gnl_st_id).", example = "1")
        @NotNull
        Long stId,

        @Schema(description = "Kisa kod.", example = "SIM")
        @Size(max = 30)
        String rsrcCode) {
}
