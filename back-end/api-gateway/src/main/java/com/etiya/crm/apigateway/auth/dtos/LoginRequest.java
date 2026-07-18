package com.etiya.crm.apigateway.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

		@Schema(example = "salesperson")
		@NotBlank
		String username,

		@Schema(example = "password")
		@NotBlank
		String password) {
}
