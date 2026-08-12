package com.etiya.crm.productservice.config;

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

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor jwtTokenPropagationInterceptor() {
        return new JwtTokenPropagationInterceptor();
    }

    @Bean
    public RequestInterceptor acceptLanguagePropagationInterceptor() {
        return new AcceptLanguagePropagationInterceptor();
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

    /**
     * Gelen istegin ham Accept-Language header'ini downstream Feign cagrilarina (lookup-service'e
     * karakteristik adi cozumleme) tasir - SADECE gercek bir HTTP istegi baglami varsa. customer-
     * service'teki FeignConfig'deki ayni isimli interceptor'la birebir ayni desen.
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