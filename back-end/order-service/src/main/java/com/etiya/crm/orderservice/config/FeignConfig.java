package com.etiya.crm.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import feign.RequestInterceptor;
// we use this to eliminate the question of who made the request, the JWT is passed from the order-service to other services
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor jwtTokenPropagationInterceptor()  {
        return template -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof JwtAuthenticationToken jwtAuth) {
                Jwt jwt = jwtAuth.getToken();
                template.header("Authorization", "Bearer " + jwt.getTokenValue());
            }
        };
    }

}
