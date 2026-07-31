package com.etiya.crm.contactinfoservice.messaging;

import com.etiya.crm.contactinfoservice.constants.LogMessages;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.customer.CustomerDeletedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

/**
 * "customer-events" topic'ini (Debezium outbox, yayinci customer-service)
 * dinleyen Kafka'ya OZGU ince adapter. Bu servisin consumer'i varsayilan
 * StringDeserializer ile calisiyor (spring.json.value.default.type yok), bu
 * yuzden ConsumerRecord&lt;String,String&gt; + manuel ObjectMapper.readValue(...)
 * BURADA (adapter'da) korunur - deserialize edilmis event, is mantigi/idempotency
 * icin CustomerDeletedEventHandler'a devredilir. Eskiden burada bir "eventType"
 * Kafka header'ina bakiliyordu, ama Debezium EventRouter bu header'i hic
 * yaymiyordu, bu yuzden payload'daki type alanina bakilir (handler icinde).
 *
 * Bu sinif broker-spesifiktir (ConsumerRecord/KafkaListener/RetryableTopic);
 * baska bir mesajlasma aracina gecilirse sadece bu adapter degisir, handler ve
 * is mantigi testleri etkilenmez.
 *
 * "customer-events"in party-service ile ORTAK bir tuketicisi var - retry/dlt
 * suffix'leri her iki tuketicide de FARKLI olmali, aksi halde ikisi de ayni
 * "customer-events-dlt" topic'ini kullanmaya calisir ve mesajlar karisir.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerEventListener {

	private final CustomerDeletedEventHandler customerDeletedEventHandler;
	private final ObjectMapper objectMapper;

	@RetryableTopic(
			attempts = "4",
			backoff = @Backoff(delay = 1000, multiplier = 2.0),
			retryTopicSuffix = "-retry-contact-info",
			dltTopicSuffix = "-dlt-contact-info",
			include = Exception.class)
	@KafkaListener(topics = KafkaTopics.CUSTOMER_EVENTS, groupId = "contact-info-service")
	public void onMessage(ConsumerRecord<String, String> record) {
		CustomerDeletedEvent event;
		try {
			event = objectMapper.readValue(record.value(), CustomerDeletedEvent.class);
		} catch (JsonProcessingException e) {
			log.error(LogMessages.CUSTOMER_EVENT_PAYLOAD_PARSE_FAILED, record.value(), e);
			return;
		}

		customerDeletedEventHandler.handle(event);
	}
}
