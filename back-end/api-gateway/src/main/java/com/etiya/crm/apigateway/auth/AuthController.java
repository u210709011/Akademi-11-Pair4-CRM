package com.etiya.crm.apigateway.auth;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.etiya.crm.apigateway.auth.constants.CookieNames;
import com.etiya.crm.apigateway.auth.dtos.LoginRequest;
import com.etiya.crm.apigateway.auth.dtos.TokenResponse;
import com.etiya.crm.apigateway.constants.SwaggerText;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

// Token'lar artik httpOnly cookie'de tutulur, JSON govdede hic donmez - front-end JS'i
// token'i hic gormez (bkz. AuthCookieFactory). refresh/logout de ayni sebeple govde yerine
// cookie'den refresh_token okur.
@Tag(name = SwaggerText.AUTH_TAG_NAME, description = SwaggerText.AUTH_TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService authService;
	private final AuthCookieFactory authCookieFactory;

	public AuthController(AuthService authService, AuthCookieFactory authCookieFactory) {
		this.authService = authService;
		this.authCookieFactory = authCookieFactory;
	}

	@Operation(summary = SwaggerText.LOGIN_SUMMARY, description = SwaggerText.LOGIN_DESCRIPTION)
	@PostMapping("/login")
	public Mono<ResponseEntity<Void>> login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request.username(), request.password(), request.clientId())
				.map(this::withAuthCookies);
	}

	@Operation(summary = SwaggerText.REFRESH_SUMMARY, description = SwaggerText.DEFAULT_CLIENT_ONLY_DESCRIPTION)
	@PostMapping("/refresh")
	public Mono<ResponseEntity<Void>> refresh(@CookieValue(CookieNames.REFRESH_TOKEN) String refreshToken) {
		return authService.refresh(refreshToken).map(this::withAuthCookies);
	}

	@Operation(summary = SwaggerText.LOGOUT_SUMMARY, description = SwaggerText.DEFAULT_CLIENT_ONLY_DESCRIPTION)
	@PostMapping("/logout")
	public Mono<ResponseEntity<Void>> logout(
			@CookieValue(value = CookieNames.REFRESH_TOKEN, required = false) String refreshToken) {
		// UC-EACRML-001 Alt Senaryo 5 ile ayni prensip: cookie'ler her zaman temizlenir,
		// Keycloak cagrisi (refresh token'i sunucu tarafinda da gecersiz kilmak icin)
		// best-effort'tur - basarisiz olsa da kullanicinin cikisini engellemez.
		Mono<Void> keycloakLogout = refreshToken != null ? authService.logout(refreshToken) : Mono.empty();
		return keycloakLogout.onErrorResume(ex -> Mono.empty())
				.then(Mono.just(withClearCookies(ResponseEntity.noContent().build())));
	}

	@Operation(summary = SwaggerText.ME_SUMMARY)
	@GetMapping("/me")
	public Mono<Map<String, Object>> me(@AuthenticationPrincipal Mono<Jwt> jwt) {
		return jwt.map(token -> Map.of(
				"name", buildDisplayName(token),
				"roles", extractRoles(token)));
	}

	private String buildDisplayName(Jwt token) {
		String given = token.getClaimAsString("given_name");
		String family = token.getClaimAsString("family_name");
		String full = String.join(" ",
				given != null ? given : "",
				family != null ? family : "").trim();
		if (!full.isEmpty()) {
			return full;
		}
		String name = token.getClaimAsString("name");
		if (name != null && !name.isBlank()) {
			return name;
		}
		return token.getClaimAsString("preferred_username");
	}

	@SuppressWarnings("unchecked")
	private List<String> extractRoles(Jwt token) {
		Map<String, Object> realmAccess = token.getClaim("realm_access");
		if (realmAccess == null) {
			return List.of();
		}
		Object roles = realmAccess.get("roles");
		return roles instanceof List<?> roleList ? (List<String>) roleList : List.of();
	}

	private ResponseEntity<Void> withAuthCookies(TokenResponse token) {
		ResponseEntity.HeadersBuilder<?> builder = ResponseEntity.noContent();
		authCookieFactory.buildAuthCookies(token).forEach(cookie -> builder.header(HttpHeaders.SET_COOKIE, cookie.toString()));
		return builder.build();
	}

	private ResponseEntity<Void> withClearCookies(ResponseEntity<Void> response) {
		ResponseEntity.HeadersBuilder<?> builder = ResponseEntity.status(response.getStatusCode());
		authCookieFactory.buildClearCookies().forEach(cookie -> builder.header(HttpHeaders.SET_COOKIE, cookie.toString()));
		return builder.build();
	}
}
