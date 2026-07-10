package com.etiya.crm.partyservice.security;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/** CUSER/UUSER alanlarini dolduran AuditorAware; JWT'den preferred_username okur. */
@Component("jwtAuditorAware")
public class JwtAuditorAware implements AuditorAware<String> {

    private static final String SYSTEM_USER = "system";
    private static final String PREFERRED_USERNAME_CLAIM = "preferred_username";

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return Optional.of(SYSTEM_USER);
        }
        return Optional.ofNullable(jwt.getClaimAsString(PREFERRED_USERNAME_CLAIM))
                .or(() -> Optional.of(SYSTEM_USER));
    }
}