package com.etiya.crm.apigateway.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.etiya.crm.apigateway.auth.dtos.LoginRequest;
import com.etiya.crm.apigateway.auth.dtos.LogoutRequest;
import com.etiya.crm.apigateway.auth.dtos.RefreshRequest;
import com.etiya.crm.apigateway.auth.dtos.TokenResponse;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public Mono<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request.username(), request.password());
	}

	@PostMapping("/refresh")
	public Mono<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
		return authService.refresh(request.refreshToken());
	}

	@PostMapping("/logout")
	public Mono<ResponseEntity<Void>> logout(@Valid @RequestBody LogoutRequest request) {
		return authService.logout(request.refreshToken()).thenReturn(ResponseEntity.noContent().build());
	}
}
