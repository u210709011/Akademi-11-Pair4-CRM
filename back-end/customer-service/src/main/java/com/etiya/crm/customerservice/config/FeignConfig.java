package com.etiya.crm.customerservice.config;

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
 * to outgoing service calls. When customer-service receives a request with
 * JWT authentication, this interceptor ensures the token is forwarded to
 * downstream services like party-service, contact-address-service, etc.
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
			// Extract the JWT token from the current security context
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (authentication instanceof JwtAuthenticationToken jwtAuth) {
				Jwt jwt = jwtAuth.getToken();
				String tokenValue = jwt.getTokenValue();

				// Add the Authorization header with Bearer token
				template.header(AUTHORIZATION_HEADER, BEARER_PREFIX + tokenValue);
			}
		}
	}
}
