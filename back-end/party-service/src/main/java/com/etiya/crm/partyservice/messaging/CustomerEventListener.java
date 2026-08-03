package com.etiya.crm.partyservice.messaging;

import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.customer.CustomerDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

/**
 * "customer-events" topic'ini (Debezium outbox, yayinci customer-service)
 * dinleyen Kafka'ya OZGU ince adapter - deserialize edilmis event'i olduğu
 * gibi CustomerDeletedEventHandler'a devreder, is mantigi/idempotency orada.
 * Bu sinif broker-spesifiktir (KafkaListener/RetryableTopic); baska bir mesajlasma
 * aracina gecilirse sadece bu adapter degisir, handler ve testleri etkilenmez.
 *
 * application.yml'deki spring.kafka.consumer.properties.spring.json.value.default.type
 * zaten CustomerDeletedEvent'e ayarli - JsonDeserializer bunu OTOMATIK olarak
 * bu tipe cevirir, bu yuzden burada manuel ObjectMapper.readValue(...) GEREKMEZ.
 *
 * "customer-events"in contact-info-service ile ORTAK bir tuketicisi var - retry/dlt
 * suffix'leri her iki tuketicide de FARKLI olmali, aksi halde ikisi de ayni
 * "customer-events-dlt" topic'ini kullanmaya calisir ve mesajlar karisir.
 */
@Component
@RequiredArgsConstructor
public class CustomerEventListener {

	private final CustomerDeletedEventHandler customerDeletedEventHandler;

	@RetryableTopic(
			attempts = "4",
			backoff = @Backoff(delay = 1000, multiplier = 2.0),
			retryTopicSuffix = "-retry-party",
			dltTopicSuffix = "-dlt-party",
			include = Exception.class)
	@KafkaListener(topics = KafkaTopics.CUSTOMER_EVENTS, groupId = "party-service")
	public void onMessage(CustomerDeletedEvent event) {
		customerDeletedEventHandler.handle(event);
	}
}
