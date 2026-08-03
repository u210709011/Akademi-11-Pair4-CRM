package com.etiya.crm.partyservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import lombok.RequiredArgsConstructor;

/**
 * Feign configuration to propagate JWT tokens from incoming requests
 * to outgoing service calls. When party-service receives a request with
 * JWT authentication, this interceptor ensures the token is forwarded to
 * downstream services like lookup-service.
 */
@Configuration
@RequiredArgsConstructor
public class FeignConfig {

	private final MachineTokenService machineTokenService;

	@Bean
	public RequestInterceptor jwtTokenPropagationInterceptor() {
		return new JwtTokenPropagationInterceptor(machineTokenService);
	}

	/**
	 * Intercepts all Feign requests and adds a JWT as the Authorization header.
	 * Gelen kullanici JWT'si varsa forward edilir; SecurityContext bos geldiginde
	 * (CustomerEventListener gibi Kafka consumer'lar ya da circuit breaker'in
	 * cagriyi farkli bir thread'de calistirdigi durumlar) party-service-m2m'in
	 * service-account token'ina dusulur - aksi halde lookup-service istegi
	 * Authorization header'siz gider ve 401 doner (bkz. MachineTokenService).
	 */
	@RequiredArgsConstructor
	public static class JwtTokenPropagationInterceptor implements RequestInterceptor {

		private static final String AUTHORIZATION_HEADER = "Authorization";
		private static final String BEARER_PREFIX = "Bearer ";

		private final MachineTokenService machineTokenService;

		@Override
		public void apply(RequestTemplate template) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (authentication instanceof JwtAuthenticationToken jwtAuth) {
				Jwt jwt = jwtAuth.getToken();
				template.header(AUTHORIZATION_HEADER, BEARER_PREFIX + jwt.getTokenValue());
			} else {
				template.header(AUTHORIZATION_HEADER, BEARER_PREFIX + machineTokenService.getAccessToken());
			}
		}
	}
}
