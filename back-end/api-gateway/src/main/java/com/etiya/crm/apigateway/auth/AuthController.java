package com.etiya.crm.apigateway.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.etiya.crm.apigateway.auth.dtos.LoginRequest;
import com.etiya.crm.apigateway.auth.dtos.LogoutRequest;
import com.etiya.crm.apigateway.auth.dtos.RefreshRequest;
import com.etiya.crm.apigateway.auth.dtos.TokenResponse;
import com.etiya.crm.apigateway.constants.SwaggerText;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@Tag(name = SwaggerText.AUTH_TAG_NAME, description = SwaggerText.AUTH_TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@Operation(summary = SwaggerText.LOGIN_SUMMARY, description = SwaggerText.LOGIN_DESCRIPTION)
	@PostMapping("/login")
	public Mono<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request.username(), request.password(), request.clientId());
	}

	@Operation(summary = SwaggerText.REFRESH_SUMMARY, description = SwaggerText.DEFAULT_CLIENT_ONLY_DESCRIPTION)
	@PostMapping("/refresh")
	public Mono<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
		return authService.refresh(request.refreshToken());
	}

	@Operation(summary = SwaggerText.LOGOUT_SUMMARY, description = SwaggerText.DEFAULT_CLIENT_ONLY_DESCRIPTION)
	@PostMapping("/logout")
	public Mono<ResponseEntity<Void>> logout(@Valid @RequestBody LogoutRequest request) {
		return authService.logout(request.refreshToken()).thenReturn(ResponseEntity.noContent().build());
	}
}
