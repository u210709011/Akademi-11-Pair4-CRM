package com.etiya.crm.lookupservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "jwtAuditorAware")
public class JpaAuditingConfig {
}
