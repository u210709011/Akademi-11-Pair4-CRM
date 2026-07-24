package com.etiya.crm.customerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * shared-events modulundeki OutboxEvent/InboxEvent entity+repository'leri ve
 * OutboxEventPublisher bean'i com.etiya.crm.shared.events paketinde yasar -
 * Spring Boot'un varsayilan component/entity scan'i sadece bu sinifin
 * paketinden asagisini tarar, bu yuzden shared paket acikca eklenir.
 */
@EnableDiscoveryClient
@EnableFeignClients
@EnableJpaAuditing(auditorAwareRef = "jwtAuditorAware")
@ConfigurationPropertiesScan
@EntityScan(basePackages = { "com.etiya.crm.customerservice.entities", "com.etiya.crm.shared.events" })
@EnableJpaRepositories(basePackages = { "com.etiya.crm.customerservice.dataAccess.abstracts",
		"com.etiya.crm.shared.events" })
@ComponentScan(basePackages = { "com.etiya.crm.customerservice", "com.etiya.crm.shared.events" })
@SpringBootApplication
public class CustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}

}
