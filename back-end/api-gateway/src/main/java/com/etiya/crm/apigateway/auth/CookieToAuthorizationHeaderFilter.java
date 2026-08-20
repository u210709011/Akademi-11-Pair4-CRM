package com.etiya.crm.apigateway.auth;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.etiya.crm.apigateway.auth.constants.CookieNames;

import reactor.core.publisher.Mono;

/**
 * customer-service/order-service/... (business servisler) kendi resource-server
 * dogrulamalarini standart "Authorization: Bearer ..." header'indan yapiyor, cookie'yi
 * anlamiyorlar. Login artik token'i sadece httpOnly cookie'ye yazdigi icin, gateway'den
 * downstream'e giden her istekte cookie'deki access_token'i - header zaten yoksa -
 * Authorization header'ina kopyalar. Boylece business servis tarafinda hicbir degisiklik
 * gerekmez.
 */
@Component
public class CookieToAuthorizationHeaderFilter implements GlobalFilter, Ordered {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		if (request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION) != null) {
			return chain.filter(exchange);
		}

		HttpCookie cookie = request.getCookies().getFirst(CookieNames.ACCESS_TOKEN);
		if (cookie == null || cookie.getValue().isBlank()) {
			return chain.filter(exchange);
		}

		ServerHttpRequest mutatedRequest = request.mutate()
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + cookie.getValue())
				.build();
		return chain.filter(exchange.mutate().request(mutatedRequest).build());
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
}
