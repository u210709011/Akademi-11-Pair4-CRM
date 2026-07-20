package com.etiya.crm.lookupservice.business.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "POST /api/v1/general-types istek govdesi.")
public record CreateGnlTpRequest(

        @Schema(description = "Grup adi.", example = "Account Type")
        @NotBlank @Size(max = 100)
        String name,

        @Schema(description = "Aciklama.", example = "Musteri hesap tipi")
        @NotBlank @Size(max = 100)
        String descr,

        @Schema(description = "Grup kodu - TYPE_VALUE'nun join anahtari, benzersiz olmali.", example = "ACCT_TP")
        @NotBlank @Size(max = 64)
        String shrtCode,

        @Schema(description = "Bu tipi kullanan entity'nin kod adi.", example = "ACCT_TP")
        @NotBlank @Size(max = 100)
        String entCodeName,

        @Schema(description = "Bu tipi kullanan entity'nin adi (opsiyonel).", example = "CustAcct")
        @Size(max = 100)
        String entName,

        @Schema(description = "Aktif mi.", example = "true")
        @NotNull
        Boolean active) {
}
