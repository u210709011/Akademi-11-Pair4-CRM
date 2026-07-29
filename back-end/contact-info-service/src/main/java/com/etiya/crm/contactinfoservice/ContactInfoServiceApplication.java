package com.etiya.crm.contactinfoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * shared-events'ten hem ".outbox" (OutboxEvent/OutboxEventRepository/
 * OutboxEventPublisher) hem ".inbox" (InboxEvent/InboxEventRepository, bkz.
 * CustomerEventListener) alt paketleri eklenir - bu servisin eskiden ayni
 * "inbox" tablosuna eslenen yerel bir Inbox entity'si vardi, artik onun
 * yerine dogrudan shared-events'teki InboxEvent kullanilir (ayni sema, bkz.
 * V1__baseline_existing_schema.sql). Bu servisin kendi paketleri acikca
 * eklenir cunku @EntityScan/@EnableJpaRepositories/@ComponentScan bir kez
 * belirtilince varsayilan (sinifin kendi paketi) scan'in yerini alir.
 */
@EnableFeignClients
@EntityScan(basePackages = { "com.etiya.crm.contactinfoservice", "com.etiya.crm.shared.events.outbox",
		"com.etiya.crm.shared.events.inbox" })
@EnableJpaRepositories(basePackages = { "com.etiya.crm.contactinfoservice", "com.etiya.crm.shared.events.outbox",
		"com.etiya.crm.shared.events.inbox" })
@ComponentScan(basePackages = { "com.etiya.crm.contactinfoservice", "com.etiya.crm.shared.events.outbox",
		"com.etiya.crm.shared.events.inbox" })
@SpringBootApplication
public class ContactInfoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContactInfoServiceApplication.class, args);
	}

}
