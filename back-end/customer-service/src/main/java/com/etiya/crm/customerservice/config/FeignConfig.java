package com.etiya.crm.customerservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.RequiredArgsConstructor;

/**
 * Feign configuration to propagate JWT tokens from incoming requests
 * to outgoing service calls. When customer-service receives a request with
 * JWT authentication, this interceptor ensures the token is forwarded to
 * downstream services like party-service, contact-address-service, etc.
 */
@Configuration
@RequiredArgsConstructor
public class FeignConfig {

	private final MachineTokenService machineTokenService;

	@Bean
	public RequestInterceptor jwtTokenPropagationInterceptor() {
		return new JwtTokenPropagationInterceptor(machineTokenService);
	}

	@Bean
	public RequestInterceptor acceptLanguagePropagationInterceptor() {
		return new AcceptLanguagePropagationInterceptor();
	}

	/**
	 * Intercepts all Feign requests and adds a JWT as the Authorization header.
	 * HTTP istekle tetiklenen akislarda gelen kullanici JWT'si forward edilir.
	 * PartyEventListener/ContactMediumEventListener gibi Kafka listener'lar bir
	 * istek icinde calismadigindan SecurityContext bos gelir - bu durumda
	 * crm-client'in service-account (client_credentials) token'i kullanilir.
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

	/**
	 * Gelen istegin ham Accept-Language header'ini downstream Feign cagrilarina tasir - SADECE
	 * gercek bir HTTP istegi baglami varsa (RequestContextHolder). Kafka listener thread'lerinin
	 * (PartyEventListener/ContactMediumEventListener) istek baglami yoktur, bu yuzden hicbir
	 * header eklenmez ve lookup-service'in taban (Ingilizce) degeri donulur - async yazma yolunda
	 * davranis degismez.
	 */
	public static class AcceptLanguagePropagationInterceptor implements RequestInterceptor {

		private static final String ACCEPT_LANGUAGE_HEADER = "Accept-Language";

		@Override
		public void apply(RequestTemplate template) {
			if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
				return;
			}
			String acceptLanguage = attributes.getRequest().getHeader(ACCEPT_LANGUAGE_HEADER);
			if (acceptLanguage != null) {
				template.header(ACCEPT_LANGUAGE_HEADER, acceptLanguage);
			}
		}
	}
}
