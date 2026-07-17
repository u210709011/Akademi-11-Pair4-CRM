package com.etiya.crm.apigateway.auth.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

		@NotBlank
		String username,

		@NotBlank
		String password) {
}
