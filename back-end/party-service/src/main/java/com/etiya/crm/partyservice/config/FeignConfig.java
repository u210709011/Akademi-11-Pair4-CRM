package com.etiya.crm.partyservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Feign configuration to propagate JWT tokens from incoming requests
 * to outgoing service calls. When party-service receives a request with
 * JWT authentication, this interceptor ensures the token is forwarded to
 * downstream services like lookup-service.
 */
@Configuration
public class FeignConfig {

	@Bean
	public RequestInterceptor jwtTokenPropagationInterceptor() {
		return new JwtTokenPropagationInterceptor();
	}

	/**
	 * Intercepts all Feign requests and adds the JWT token from the current
	 * SecurityContext as an Authorization header.
	 */
	public static class JwtTokenPropagationInterceptor implements RequestInterceptor {

		private static final String AUTHORIZATION_HEADER = "Authorization";
		private static final String BEARER_PREFIX = "Bearer ";

		@Override
		public void apply(RequestTemplate template) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (authentication instanceof JwtAuthenticationToken jwtAuth) {
				Jwt jwt = jwtAuth.getToken();
				String tokenValue = jwt.getTokenValue();

				template.header(AUTHORIZATION_HEADER, BEARER_PREFIX + tokenValue);
			}
		}
	}
}
