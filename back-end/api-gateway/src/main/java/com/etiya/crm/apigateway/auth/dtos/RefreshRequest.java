package com.etiya.crm.apigateway.auth.dtos;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(

		@NotBlank
		String refreshToken) {
}
