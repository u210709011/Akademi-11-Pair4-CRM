package com.etiya.crm.contactinfoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * shared-events'ten sadece ".outbox" alt paketi eklenir (OutboxEvent/
 * OutboxEventRepository/OutboxEventPublisher) - ".inbox" alt paketindeki
 * InboxEvent, bu serviste zaten var olan yerel Inbox entity'siyle (ayni
 * "inbox" tablosu) cakisir, bu yuzden kasitli olarak disaridedir. Bu servisin
 * kendi paketleri acikca eklenir cunku @EntityScan/@EnableJpaRepositories/
 * @ComponentScan bir kez belirtilince varsayilan (sinifin kendi paketi)
 * scan'in yerini alir.
 */
@EnableFeignClients
@EntityScan(basePackages = { "com.etiya.crm.contactinfoservice", "com.etiya.crm.shared.events.outbox" })
@EnableJpaRepositories(basePackages = { "com.etiya.crm.contactinfoservice", "com.etiya.crm.shared.events.outbox" })
@ComponentScan(basePackages = { "com.etiya.crm.contactinfoservice", "com.etiya.crm.shared.events.outbox" })
@SpringBootApplication
public class ContactInfoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContactInfoServiceApplication.class, args);
	}

}
