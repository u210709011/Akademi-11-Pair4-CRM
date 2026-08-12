package com.etiya.crm.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.etiya.crm.orderservice.constants.LogMessages;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * Feign cagrilarina gelen kullanici JWT'sini forward eder - customer/party/contact-info-service'teki
 * ayni isimli JwtTokenPropagationInterceptor deseniyle tutarli (bkz. o servislerdeki FeignConfig).
 * order-service, o servislerin aksine hicbir Kafka listener'a sahip degil (sadece outbox ile yayinlar,
 * tuketmez) - yani Feign cagrilari her zaman bir HTTP istegi icinde, dolu bir SecurityContext ile
 * calisir. Bu yuzden simdilik machine-token (M2M) fallback'i BURAYA eklenmedi: o, ayri bir
 * "order-service-m2m" Keycloak client'inin config-server'da (bu repodaki depoda degil, ayri config
 * git repo'sunda) kayitli olmasini gerektirir. SecurityContext bos gelirse (beklenmeyen durum) sessizce
 * Authorization header'siz istek atmak yerine acikca reddediyoruz - downstream zaten 401 dondurecekti,
 * ama burada erken ve anlasilir sekilde patlamak debug'i kolaylastirir.
 */
@Configuration
public class FeignConfig {

	@Bean
	public RequestInterceptor jwtTokenPropagationInterceptor() {
		return new JwtTokenPropagationInterceptor();
	}

	@Slf4j
	public static class JwtTokenPropagationInterceptor implements RequestInterceptor {

		private static final String AUTHORIZATION_HEADER = "Authorization";
		private static final String BEARER_PREFIX = "Bearer ";

		@Override
		public void apply(RequestTemplate template) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
				log.warn(LogMessages.NO_JWT_FOR_DOWNSTREAM_CALL);
				throw new IllegalStateException(LogMessages.NO_JWT_FOR_DOWNSTREAM_CALL);
			}

			Jwt jwt = jwtAuth.getToken();
			template.header(AUTHORIZATION_HEADER, BEARER_PREFIX + jwt.getTokenValue());
		}
	}
}
