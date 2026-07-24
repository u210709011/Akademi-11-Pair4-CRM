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

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@Tag(name = "Auth", description = "Keycloak (realm: crm) onunde confidential client olarak calisan token uclari.")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@Operation(summary = "Kullanici adi/sifre ile giris yap",
			description = "clientId alani opsiyoneldir. Bos birakilirsa ya da 'default' gonderilirse "
					+ "crm-client uzerinden 8 saat gecerli token doner. 'short-lived' gonderilirse "
					+ "crm-client-short uzerinden sadece 30 saniye gecerli bir token doner - token "
					+ "expiry/refresh davranisini test etmek icin kullanilir.")
	@PostMapping("/login")
	public Mono<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request.username(), request.password(), request.clientId());
	}

	@Operation(summary = "Refresh token ile yeni access token al",
			description = "Her zaman varsayilan client (crm-client) uzerinden calisir.")
	@PostMapping("/refresh")
	public Mono<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
		return authService.refresh(request.refreshToken());
	}

	@Operation(summary = "Oturumu kapat (refresh token'i gecersiz kil)",
			description = "Her zaman varsayilan client (crm-client) uzerinden calisir.")
	@PostMapping("/logout")
	public Mono<ResponseEntity<Void>> logout(@Valid @RequestBody LogoutRequest request) {
		return authService.logout(request.refreshToken()).thenReturn(ResponseEntity.noContent().build());
	}
}
