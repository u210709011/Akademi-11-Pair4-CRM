package com.etiya.crm.shared.contracts.typevalue;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

/** tableName/fieldName kasten yok - immutable kimlik alanlari (bkz. eski seed'in "VALUE_ID'nin
 * anlamini asla degistirme" kurali; ayni prensip TYPE_VALUE_ID'nin ait oldugu tablo icin de gecerli). */
@Schema(description = SwaggerText.UPDATE_TYPE_VALUE_REQUEST_DESCRIPTION)
public record UpdateTypeValueRequest(

        @Schema(description = SwaggerText.UPDATE_TYPE_VALUE_DESCRIPTION_FIELD_DESCRIPTION, example = "Standart Hesap")
        @NotBlank @Size(max = 200)
        String description,

        @Schema(description = SwaggerText.UPDATE_TYPE_VALUE_VALUE_DESCRIPTION, example = "STANDARD")
        @Size(max = 50)
        String value,

        @Schema(description = SwaggerText.TYPE_VALUE_USING_MODULE_NAME_DESCRIPTION, example = "customer-service")
        @Size(max = 50)
        String usingModuleName) {
}
