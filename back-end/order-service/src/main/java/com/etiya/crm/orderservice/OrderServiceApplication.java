package com.etiya.crm.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * shared-events modulundeki OutboxEvent/InboxEvent entity+repository'leri ve
 * OutboxEventPublisher bean'i com.etiya.crm.shared.events paketinde yasar -
 * Spring Boot'un varsayilan component/entity scan'i sadece bu sinifin
 * paketinden asagisini tarar, bu yuzden shared paket acikca eklenir.
 */
@EnableFeignClients
@EnableJpaAuditing(auditorAwareRef = "jwtAuditorAware")
@EntityScan(basePackages = { "com.etiya.crm.orderservice.entities", "com.etiya.crm.shared.events" })
@EnableJpaRepositories(basePackages = { "com.etiya.crm.orderservice.dataAccess.abstracts",
		"com.etiya.crm.shared.events" })
@SpringBootApplication(scanBasePackages = { "com.etiya.crm.orderservice", "com.etiya.crm.shared.events" })
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}
