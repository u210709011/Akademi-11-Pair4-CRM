package com.etiya.crm.apigateway.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(

		@Schema(example = "salesperson")
		@NotBlank
		@Pattern(regexp = "\\S(.*\\S)?", message = "username bastaki/sondaki bosluklarla gonderilemez.")
		String username,

		@Schema(example = "password")
		@NotBlank
		String password,

		@Schema(description = "Opsiyonel - hangi Keycloak client'i ile token alinacak. Bos birakilirse "
				+ "varsayilan (8 saat) client kullanilir. Test amacli 30sn'lik token icin 'short-lived' gonderin. "
				+ "Bu ikisi disinda (orn. gercek Keycloak client_id'si) bir deger gonderilirse istek reddedilir.",
				example = "short-lived", allowableValues = {"default", "short-lived"})
		@Pattern(regexp = "|default|short-lived", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Invalid clientId.")
		String clientId) {
}
