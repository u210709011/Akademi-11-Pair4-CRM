package com.etiya.crm.contactinfoservice.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<Long> {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final Long DEFAULT_USER_ID = 0L;

    @Override
    public Optional<Long> getCurrentAuditor() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return Optional.of(DEFAULT_USER_ID);
        }

        String header = attributes.getRequest().getHeader(USER_ID_HEADER);
        try {
            return Optional.of(header != null ? Long.parseLong(header) : DEFAULT_USER_ID);
        } catch (NumberFormatException ex) {
            return Optional.of(DEFAULT_USER_ID);
        }
    }

}
