package com.etiya.crm.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Mono;

/**
 * Redis-backed RequestRateLimiter (bkz. config-repo'daki
 * spring.cloud.gateway.server.webflux.default-filters) bu bean'i isim/SpEL ile
 * referans aliyor. Kullanici-tier kavrami yok, bu yuzden istemci IP'si
 * bazinda sinirlandiriliyor.
 */
@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getRemoteAddress())
                .map(address -> address.getAddress().getHostAddress())
                .defaultIfEmpty("unknown");
    }
}
