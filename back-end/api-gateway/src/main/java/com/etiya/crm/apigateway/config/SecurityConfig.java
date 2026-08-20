package com.etiya.crm.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

import com.etiya.crm.apigateway.auth.CookieBearerTokenConverter;

@Configuration
@EnableWebFluxSecurity
/** Gateway kimlik doğrulama ve CORS kurallarını tanımlar. */
public class SecurityConfig {

    @Value("${cors.allowed-origins:http://localhost:4200}")
    private List<String> allowedOrigins;

    private final CookieBearerTokenConverter cookieBearerTokenConverter;

    public SecurityConfig(CookieBearerTokenConverter cookieBearerTokenConverter) {
        this.cookieBearerTokenConverter = cookieBearerTokenConverter;
    }

    // Birim testi yok: govde sadece ServerHttpSecurity DSL'ini zincirliyor, kendi kod dalimiz
    // (branch) yok - bu yuzden Mockito ile anlamli bir sey dogrulanamaz. Gercekten test etmenin
    // tek yolu bir Spring context (@WebFluxTest/@SpringBootTest) ayaga kaldirip path'lere gercek
    // istek atmak, bu da bu projede kullanilan Mockito-only birim test kapsaminin disinda kaliyor.
    // corsConfigurationSource() ise saf bir bean builder oldugu icin ayrica test edildi.
    @Bean
    /** Public uçları açık bırakıp kalan istekleri (access_token cookie veya Authorization header'daki) JWT ile korur. */
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                // CSRF-token mekanizmasi kurulmadi: SameSite=Lax cookie'ler zaten cross-site
                // XHR/fetch'e eklenmiyor, front-end/gateway de ayni "site" (port farki site
                // sinirini degistirmiyor) - bkz. AuthCookieFactory yorumu.
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/actuator/**", "/eureka/**").permitAll()
                        .pathMatchers("/api/v1/auth/login", "/api/v1/auth/refresh", "/api/v1/auth/logout").permitAll()
                        .pathMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/webjars/**").permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenConverter(cookieBearerTokenConverter)
                        .jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    /** Front-end kaynakları için izin verilen CORS politikasını oluşturur. */
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
