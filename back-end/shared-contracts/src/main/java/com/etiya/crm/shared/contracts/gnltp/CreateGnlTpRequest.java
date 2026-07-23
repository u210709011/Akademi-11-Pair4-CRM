package com.etiya.crm.shared.contracts.gnltp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "lookup-service POST /api/v1/general-types istek govdesi.")
public record CreateGnlTpRequest(

        @Schema(description = "Bu degerin gosterim adi.", example = "Musteri Hesap")
        @NotBlank @Size(max = 100)
        String name,

        @Schema(description = "Aciklama.", example = "Musteri Hesap")
        @NotBlank @Size(max = 100)
        String descr,

        @Schema(description = "Bu degerin kendi kisa kodu (ent_code_name ile birlikte benzersiz olmali).", example = "CUST_ACCT")
        @NotBlank @Size(max = 64)
        String shrtCode,

        @Schema(description = "Grup anahtari - bu degerin ait oldugu grup.", example = "ACCOUNT_TYPE")
        @NotBlank @Size(max = 100)
        String entCodeName,

        @Schema(description = "Pratikte entCodeName ile ayni deger girilir.", example = "ACCOUNT_TYPE")
        @Size(max = 100)
        String entName,

        @Schema(description = "Aktif mi.", example = "true")
        @NotNull
        Boolean active) {
}
