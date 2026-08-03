package com.etiya.crm.shared.contracts.typevalue;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "lookup-service POST /api/v1/type-values istek govdesi.")
public record CreateTypeValueRequest(

        @Schema(description = "Polimorfik sahiplik etiketi verilen gercek is tablosunun adi.", example = "PARTY")
        @NotBlank @Size(max = 40)
        String tableName,

        @Schema(description = "Bu tabloya atanan tip etiketi numarasi.", example = "9")
        @NotNull
        Long fieldName,

        @Schema(description = "Aciklama.", example = "Party_id")
        @NotBlank @Size(max = 200)
        String description,

        @Schema(description = "Kisa kod (opsiyonel).", example = "PARTY_ID")
        @Size(max = 50)
        String value,

        @Schema(description = "Bu degeri kullanan servis/modul (bilgi amacli).", example = "party-service")
        @Size(max = 50)
        String usingModuleName) {
}
