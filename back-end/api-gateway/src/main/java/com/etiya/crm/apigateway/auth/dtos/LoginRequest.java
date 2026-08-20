package com.etiya.crm.apigateway.auth.dtos;

import com.etiya.crm.apigateway.auth.constants.MessageKeys;
import com.etiya.crm.apigateway.constants.SwaggerText;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(

		@Schema(example = "salesperson")
		@NotBlank
		@Size(max = 50, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
		@Pattern(regexp = "\\S(.*\\S)?", message = "{" + MessageKeys.NO_LEADING_TRAILING_WHITESPACE + "}")
		String username,

		@Schema(example = "password")
		@NotBlank
		@Size(max = 50, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
		@Pattern(regexp = "\\S(.*\\S)?", message = "{" + MessageKeys.NO_LEADING_TRAILING_WHITESPACE + "}")
		String password,

		@Schema(description = SwaggerText.CLIENT_ID_DESCRIPTION,
				example = "short-lived", allowableValues = {"default", "short-lived"})
		@Pattern(regexp = "|default|short-lived", flags = Pattern.Flag.CASE_INSENSITIVE,
				message = "{" + MessageKeys.INVALID_CLIENT_ID + "}")
		String clientId) {
}
