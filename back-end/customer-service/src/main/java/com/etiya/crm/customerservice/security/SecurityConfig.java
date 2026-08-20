package com.etiya.crm.customerservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


@Configuration
@EnableMethodSecurity
/** Customer servisinin HTTP ve method güvenlik kurallarını tanımlar. */
public class SecurityConfig {

	private static final String ACTUATOR_PATH = "/actuator/**";

	private static final String[] SWAGGER_PATHS = {
			"/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/webjars/**"
	};

	@Bean
	/** JWT doğrulamasını ve korunan endpoint kuralını etkinleştirir. */
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(ACTUATOR_PATH).permitAll()
						.requestMatchers(SWAGGER_PATHS).permitAll()
						.anyRequest().authenticated())
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
		return http.build();
	}

	@Bean
	/** Keycloak realm rollerini Spring Security yetkilerine dönüştürür. */
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
		authoritiesConverter.setAuthorityPrefix("ROLE_");
		authoritiesConverter.setAuthoritiesClaimName("realm_access.roles");

		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(jwt -> {
			Collection<GrantedAuthority> authorities = new ArrayList<>();
			Map<String, Object> realmAccess = jwt.getClaim("realm_access");
			if (realmAccess != null) {
				Object roles = realmAccess.get("roles");
				if (roles instanceof Collection<?> roleList) {
					roleList.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
				}
			}
			return authorities;
		});
		return converter;
	}

}
