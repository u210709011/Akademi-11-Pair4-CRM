package com.etiya.crm.productservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor jwtTokenPropagationInterceptor() {
        return new JwtTokenPropagationInterceptor();
    }

    public static class JwtTokenPropagationInterceptor implements RequestInterceptor {

        private static final String AUTHORIZATION_HEADER = "Authorization";
        private static final String BEARER_PREFIX = "Bearer ";

        @Override
        public void apply(RequestTemplate template) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof JwtAuthenticationToken jwtAuth) {
                Jwt jwt = jwtAuth.getToken();
                template.header(AUTHORIZATION_HEADER, BEARER_PREFIX + jwt.getTokenValue());
            }
            // NOT: party-service'te SecurityContext boş kaldığında (Kafka consumer vb.)
            // MachineTokenService'e düşülüyordu. product-service'te şimdilik senkron
            // istek dışında bir çağrı yolu yok, o yüzden fallback eklenmedi.
        }
    }
}