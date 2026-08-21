package com.etiya.crm.contactinfoservice.messaging;

import com.etiya.crm.contactinfoservice.constants.AlertTags;
import com.etiya.crm.contactinfoservice.constants.LogMessages;
import com.etiya.crm.shared.contracts.messaging.DltAlertNotifier;
import com.etiya.crm.shared.contracts.messaging.DltMetrics;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.customer.CustomerDeletedEvent;
import com.etiya.crm.shared.events.customer.CustomerEventTypes;
import com.etiya.crm.shared.events.messaging.NonRetryableEventException;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;
import com.etiya.crm.shared.events.saga.SagaEventTypes;
import com.etiya.crm.shared.events.saga.SagaStepNames;
import com.etiya.crm.shared.events.saga.SagaStepResultEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
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
	private final MeterRegistry meterRegistry;
	private final OutboxEventPublisher outboxEventPublisher;
	private final DltAlertNotifier dltAlertNotifier;

	@RetryableTopic(
			attempts = "4",
			backoff = @Backoff(delay = 1000, multiplier = 2.0),
			retryTopicSuffix = "-retry-contact-info",
			dltTopicSuffix = "-dlt-contact-info",
			exclude = NonRetryableEventException.class,
			traversingCauses = "true")
	@KafkaListener(topics = KafkaTopics.CUSTOMER_EVENTS, groupId = AlertTags.SERVICE_NAME)
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

	@DltHandler
	public void onMessageDlt(ConsumerRecord<String, String> record,
			@Header(value = KafkaHeaders.EXCEPTION_FQCN, required = false) String exceptionType,
			@Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage) {
		log.error(LogMessages.CUSTOMER_EVENT_DLT, record.key(), exceptionType, exceptionMessage);
		meterRegistry.counter(DltMetrics.DLT_EVENTS_COUNTER, DltMetrics.TAG_EVENT_TYPE, AlertTags.CUSTOMER_DELETED_EVENT_TYPE,
				DltMetrics.TAG_LISTENER, AlertTags.CUSTOMER_EVENT_LISTENER).increment();
		dltAlertNotifier.alert(AlertTags.SERVICE_NAME, AlertTags.CUSTOMER_EVENT_LISTENER, AlertTags.CUSTOMER_DELETED_EVENT_TYPE,
				exceptionType, exceptionMessage);

		try {
			CustomerDeletedEvent event = objectMapper.readValue(record.value(), CustomerDeletedEvent.class);
			if (CustomerEventTypes.CUSTOMER_DELETED.equals(event.type())) {
				outboxEventPublisher.publish(KafkaTopics.CUSTOMER_DELETION_SAGA_AGGREGATE_TYPE, event.custId().toString(),
						SagaEventTypes.STEP_FAILED,
						new SagaStepResultEvent(UUID.randomUUID(), SagaEventTypes.STEP_FAILED, event.custId(),
								SagaStepNames.CONTACT_INFO_DEACTIVATION, false, exceptionType + ": " + exceptionMessage));
			}
		} catch (JsonProcessingException parseEx) {
			log.error(LogMessages.CUSTOMER_EVENT_DLT_PAYLOAD_UNPARSEABLE, record.key());
		}
	}
}
