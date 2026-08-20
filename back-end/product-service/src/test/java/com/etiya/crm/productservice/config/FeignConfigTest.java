package com.etiya.crm.productservice.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class FeignConfigTest {

	@AfterEach
	void clearContexts() {
		SecurityContextHolder.clearContext();
		RequestContextHolder.resetRequestAttributes();
	}

	@Test
	void jwtInterceptor_propagatesUserJwt_whenAuthenticatedWithJwt() {
		Jwt jwt = Jwt.withTokenValue("user-token").header("alg", "none").claim("sub", "user").build();
		SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
		RequestTemplate template = new RequestTemplate();

		new FeignConfig.JwtTokenPropagationInterceptor().apply(template);

		assertThat(template.headers().get("Authorization")).containsExactly("Bearer user-token");
	}

	@Test
	void jwtInterceptor_addsNoHeader_whenNoUserAuthentication() {
		SecurityContextHolder.clearContext();
		RequestTemplate template = new RequestTemplate();

		new FeignConfig.JwtTokenPropagationInterceptor().apply(template);

		assertThat(template.headers()).doesNotContainKey("Authorization");
	}

	@Test
	void acceptLanguageInterceptor_copiesHeader_whenRequestContextHasIt() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Accept-Language", "tr-TR");
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		RequestTemplate template = new RequestTemplate();

		new FeignConfig.AcceptLanguagePropagationInterceptor().apply(template);

		assertThat(template.headers().get("Accept-Language")).containsExactly("tr-TR");
	}

	@Test
	void acceptLanguageInterceptor_addsNoHeader_whenNoRequestContext() {
		RequestContextHolder.resetRequestAttributes();
		RequestTemplate template = new RequestTemplate();

		new FeignConfig.AcceptLanguagePropagationInterceptor().apply(template);

		assertThat(template.headers()).doesNotContainKey("Accept-Language");
	}
}
