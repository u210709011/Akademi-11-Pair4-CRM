package com.etiya.crm.lookupservice.business.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "PUT /api/v1/general-statuses/{id} istek govdesi.")
public record UpdateGnlStRequest(

        @Schema(description = "Grup adi.", example = "Customer Status")
        @NotBlank @Size(max = 100)
        String name,

        @Schema(description = "Aciklama.", example = "Musteri durumu")
        @NotBlank @Size(max = 100)
        String descr,

        @Schema(description = "Grup kodu, benzersiz olmali.", example = "CUST_STATUS")
        @NotBlank @Size(max = 15)
        String shrtCode,

        @Schema(description = "Aktif mi.", example = "true")
        @NotNull
        Boolean active,

        @Schema(description = "Bu durumu kullanan entity'nin kod adi.", example = "CUST_STATUS")
        @NotBlank @Size(max = 100)
        String entCodeName,

        @Schema(description = "Bu durumu kullanan entity'nin adi (opsiyonel).", example = "Cust")
        @Size(max = 100)
        String entName) {
}
