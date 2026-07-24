package com.etiya.crm.shared.contracts.srvcspec;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "lookup-service PUT /api/v1/service-specs/{id} istek govdesi.")
public record UpdateSrvcSpecRequest(

        @Schema(description = "Servis spec adi.", example = "Mobil Hat Aktivasyonu")
        @NotBlank @Size(max = 100)
        String name,

        @Schema(description = "Aciklama.", example = "Yeni mobil hat aktivasyon servisi")
        @NotBlank @Size(max = 100)
        String descr,

        @Schema(description = "Kisa kod.", example = "MOBILE_ACTIVATION")
        @NotBlank @Size(max = 100)
        String srvcCode,

        @Schema(description = "GNL_ST grubundaki durum id'si (gnl_st_id).", example = "1")
        @NotNull
        Long stId) {
}
