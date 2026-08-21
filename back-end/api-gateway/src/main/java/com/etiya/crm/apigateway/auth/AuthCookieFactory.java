package com.etiya.crm.apigateway.auth;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.etiya.crm.apigateway.auth.constants.CookieNames;
import com.etiya.crm.apigateway.auth.dtos.TokenResponse;

/**
 * access/refresh token'lari httpOnly cookie olarak set eder/temizler - JS taraf token'i
 * hic gormez (XSS ile localStorage'dan calinma riskini kapatir). SameSite=Lax yeterli:
 * front-end/gateway ayni "site" sayilir (port farki site sinirini degistirmez), bu yuzden
 * ayri bir CSRF-token mekanizmasi kurulmadi - Lax zaten cross-site XHR/fetch'e cookie
 * eklenmesini engelliyor.
 */
@Component
public class AuthCookieFactory {

	@Value("${cookie.secure:false}")
	private boolean secure;

	public List<ResponseCookie> buildAuthCookies(TokenResponse token) {
		return List.of(
				buildCookie(CookieNames.ACCESS_TOKEN, token.accessToken(), token.expiresIn()),
				buildCookie(CookieNames.REFRESH_TOKEN, token.refreshToken(), token.refreshExpiresIn()));
	}

	public List<ResponseCookie> buildClearCookies() {
		return List.of(clearCookie(CookieNames.ACCESS_TOKEN), clearCookie(CookieNames.REFRESH_TOKEN));
	}

	private ResponseCookie buildCookie(String name, String value, Long maxAgeSeconds) {
		return ResponseCookie.from(name, value)
				.httpOnly(true)
				.secure(secure)
				.sameSite("Lax")
				.path("/")
				.maxAge(maxAgeSeconds != null ? Duration.ofSeconds(maxAgeSeconds) : Duration.ofHours(1))
				.build();
	}

	private ResponseCookie clearCookie(String name) {
		return ResponseCookie.from(name, "")
				.httpOnly(true)
				.secure(secure)
				.sameSite("Lax")
				.path("/")
				.maxAge(Duration.ZERO)
				.build();
	}
}
