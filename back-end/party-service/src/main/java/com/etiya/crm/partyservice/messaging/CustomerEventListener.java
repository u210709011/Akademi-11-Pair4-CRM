package com.etiya.crm.partyservice.messaging;

import com.etiya.crm.partyservice.constants.LogMessages;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.customer.CustomerDeletedEvent;
import com.etiya.crm.shared.events.messaging.NonRetryableEventException;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
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
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventListener {

	private final CustomerDeletedEventHandler customerDeletedEventHandler;
	private final MeterRegistry meterRegistry;

	@RetryableTopic(
			attempts = "4",
			backoff = @Backoff(delay = 1000, multiplier = 2.0),
			retryTopicSuffix = "-retry-party",
			dltTopicSuffix = "-dlt-party",
			exclude = NonRetryableEventException.class,
			traversingCauses = "true")
	@KafkaListener(topics = KafkaTopics.CUSTOMER_EVENTS, groupId = "party-service")
	public void onMessage(CustomerDeletedEvent event) {
		customerDeletedEventHandler.handle(event);
	}

	@DltHandler
	public void onMessageDlt(CustomerDeletedEvent event,
			@Header(value = KafkaHeaders.EXCEPTION_FQCN, required = false) String exceptionType,
			@Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage) {
		log.error(LogMessages.CUSTOMER_EVENT_DLT, event.eventId(), event.type(), exceptionType, exceptionMessage);
		meterRegistry.counter("kafka.dlt.events", "eventType", event.type(), "listener", "PartyServiceCustomerEventListener").increment();
	}
}
