package com.etiya.crm.shared.contracts.typevalue;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** tableName/fieldName kasten yok - immutable kimlik alanlari (bkz. eski seed'in "VALUE_ID'nin
 * anlamini asla degistirme" kurali; ayni prensip TYPE_VALUE_ID'nin ait oldugu tablo icin de gecerli). */
@Schema(description = "lookup-service PUT /api/v1/type-values/{id} istek govdesi.")
public record UpdateTypeValueRequest(

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
