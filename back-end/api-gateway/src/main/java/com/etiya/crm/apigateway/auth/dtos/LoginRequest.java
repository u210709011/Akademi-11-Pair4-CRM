package com.etiya.crm.apigateway.auth.dtos;

import com.etiya.crm.apigateway.auth.constants.MessageKeys;

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

		@Schema(description = "Opsiyonel - hangi Keycloak client'i ile token alinacak. Bos birakilirse "
				+ "varsayilan (8 saat) client kullanilir. Test amacli 30sn'lik token icin 'short-lived' gonderin. "
				+ "Bu ikisi disinda (orn. gercek Keycloak client_id'si) bir deger gonderilirse istek reddedilir.",
				example = "short-lived", allowableValues = {"default", "short-lived"})
		@Pattern(regexp = "|default|short-lived", flags = Pattern.Flag.CASE_INSENSITIVE,
				message = "{" + MessageKeys.INVALID_CLIENT_ID + "}")
		String clientId) {
}
