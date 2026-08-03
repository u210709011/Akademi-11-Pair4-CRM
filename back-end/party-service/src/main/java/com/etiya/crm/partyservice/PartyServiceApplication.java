package com.etiya.crm.partyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.etiya.crm.shared.contracts.feign.DefaultFeignRetryConfig;

/**
 * shared-events modulundeki OutboxEvent/InboxEvent entity+repository'leri ve
 * OutboxEventPublisher bean'i com.etiya.crm.shared.events paketinde yasar -
 * Spring Boot'un varsayilan component/entity scan'i sadece bu sinifin
 * paketinden asagisini tarar, bu yuzden shared paket acikca eklenir.
 */
@EnableFeignClients(defaultConfiguration = DefaultFeignRetryConfig.class)
@ConfigurationPropertiesScan
@EnableJpaAuditing(auditorAwareRef = "jwtAuditorAware")
@EntityScan(basePackages = { "com.etiya.crm.partyservice.entities", "com.etiya.crm.shared.events" })
@EnableJpaRepositories(basePackages = { "com.etiya.crm.partyservice.dataAccess.abstracts",
		"com.etiya.crm.shared.events" })
@SpringBootApplication(scanBasePackages = { "com.etiya.crm.partyservice", "com.etiya.crm.shared.events" })
public class PartyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PartyServiceApplication.class, args);
	}

}
