package com.etiya.crm.shared.contracts.typevalue;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.etiya.crm.shared.contracts.constants.SwaggerText;

@Schema(description = SwaggerText.CREATE_TYPE_VALUE_REQUEST_DESCRIPTION)
public record CreateTypeValueRequest(

        @Schema(description = SwaggerText.TYPE_VALUE_TABLE_NAME_DESCRIPTION, example = "PARTY")
        @NotBlank @Size(max = 40)
        String tableName,

        @Schema(description = SwaggerText.TYPE_VALUE_FIELD_NAME_DESCRIPTION, example = "9")
        @NotNull
        Long fieldName,

        @Schema(description = SwaggerText.CREATE_TYPE_VALUE_DESCRIPTION_FIELD_DESCRIPTION, example = "Party_id")
        @NotBlank @Size(max = 200)
        String description,

        @Schema(description = SwaggerText.CREATE_TYPE_VALUE_VALUE_DESCRIPTION, example = "PARTY_ID")
        @Size(max = 50)
        String value,

        @Schema(description = SwaggerText.TYPE_VALUE_USING_MODULE_NAME_DESCRIPTION, example = "party-service")
        @Size(max = 50)
        String usingModuleName) {
}
