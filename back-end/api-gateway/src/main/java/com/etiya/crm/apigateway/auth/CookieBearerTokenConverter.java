package com.etiya.crm.apigateway.auth;

import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.etiya.crm.apigateway.auth.constants.CookieNames;

import reactor.core.publisher.Mono;

/**
 * Gateway'in kendi resource-server dogrulamasi icin: once klasik Authorization header'a
 * bakar (Swagger "Authorize" ile veya servis-ici cagrilarla test edilebilsin diye), yoksa
 * access_token cookie'sine duser. Boylece login artik token'i govdede degil cookie'de
 * dondugu halde gateway kendi route'larini (anyExchange().authenticated()) hala JWT ile
 * koruyabiliyor.
 */
@Component
public class CookieBearerTokenConverter implements ServerAuthenticationConverter {

	@Override
	public Mono<org.springframework.security.core.Authentication> convert(ServerWebExchange exchange) {
		String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		if (header != null && header.regionMatches(true, 0, "Bearer ", 0, 7)) {
			return Mono.just(new BearerTokenAuthenticationToken(header.substring(7)));
		}

		HttpCookie cookie = exchange.getRequest().getCookies().getFirst(CookieNames.ACCESS_TOKEN);
		if (cookie != null && !cookie.getValue().isBlank()) {
			return Mono.just(new BearerTokenAuthenticationToken(cookie.getValue()));
		}

		return Mono.empty();
	}
}
