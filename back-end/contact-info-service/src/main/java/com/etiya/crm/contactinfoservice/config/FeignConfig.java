package com.etiya.crm.contactinfoservice.config;

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
 * to outgoing service calls. When contact-info-service receives a request with
 * JWT authentication, this interceptor ensures the token is forwarded to
 * downstream services like lookup-service, customer-service, etc.
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
	 * Gelen kullanici JWT'si varsa forward edilir; SecurityContext bos geldiginde
	 * (CustomerEventListener gibi Kafka consumer'lar ya da circuit breaker'in
	 * cagriyi farkli bir thread'de calistirdigi durumlar) contact-info-service-m2m'in
	 * service-account token'ina dusulur - aksi halde downstream istek Authorization
	 * header'siz gider ve 401 doner (bkz. MachineTokenService).
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
	 * gercek bir HTTP istegi baglami varsa (RequestContextHolder). Kafka listener'larin istek
	 * baglami olmadigindan buraya hicbir header eklenmez. customer/product-service'teki ayni
	 * isimli interceptor'la birebir ayni desen (bkz. o servislerdeki FeignConfig).
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
