package com.etiya.crm.shared.contracts.gnlst;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "lookup-service PUT /api/v1/general-statuses/{id} istek govdesi.")
public record UpdateGnlStRequest(

        @Schema(description = "Bu degerin gosterim adi.", example = "Aktif")
        @NotBlank @Size(max = 100)
        String name,

        @Schema(description = "Aciklama.", example = "Aktif")
        @NotBlank @Size(max = 100)
        String descr,

        @Schema(description = "Bu degerin kendi kisa kodu (ent_code_name ile birlikte benzersiz olmali).", example = "ACTIVE")
        @NotBlank @Size(max = 15)
        String shrtCode,

        @Schema(description = "Aktif mi.", example = "true")
        @NotNull
        Boolean active,

        @Schema(description = "Grup anahtari - bu degerin ait oldugu grup.", example = "CUST_STATUS")
        @NotBlank @Size(max = 100)
        String entCodeName,

        @Schema(description = "Pratikte entCodeName ile ayni deger girilir.", example = "CUST_STATUS")
        @Size(max = 100)
        String entName) {
}
