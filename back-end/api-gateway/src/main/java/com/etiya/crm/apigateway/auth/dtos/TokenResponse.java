package com.etiya.crm.apigateway.auth.dtos;

public record TokenResponse(
		String accessToken,
		String refreshToken,
		String tokenType,
		Long expiresIn) {
}
