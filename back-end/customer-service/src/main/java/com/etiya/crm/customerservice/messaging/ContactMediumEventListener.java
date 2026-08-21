package com.etiya.crm.customerservice.messaging;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import com.etiya.crm.customerservice.constants.AlertTags;
import com.etiya.crm.customerservice.constants.KafkaConsumerGroups;
import com.etiya.crm.customerservice.constants.LogMessages;
import com.etiya.crm.shared.contracts.messaging.DltAlertNotifier;
import com.etiya.crm.shared.contracts.messaging.DltMetrics;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.contactmedium.ContactMediumEvent;
import com.etiya.crm.shared.events.messaging.NonRetryableEventException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * contact-info-service'in yayinladigi "contact-medium-events" topic'ini
 * dinleyen Kafka'ya OZGU ince adapter - deserialize edilmis event'i oldugu
 * gibi ContactMediumEventHandler'a devreder, is mantigi/idempotency/filtreleme
 * orada. Bu sinif broker-spesifiktir (KafkaListener/RetryableTopic); baska bir
 * mesajlasma aracina gecilirse sadece bu adapter degisir, handler ve testleri
 * etkilenmez. Kalici hatalarda 4 deneme sonrasi "contact-medium-events-dlt"
 * topic'ine dusurulur (DLQ).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContactMediumEventListener {

	private final ContactMediumEventHandler contactMediumEventHandler;
	private final MeterRegistry meterRegistry;
	private final DltAlertNotifier dltAlertNotifier;

	@RetryableTopic(
			attempts = "4",
			backoff = @Backoff(delay = 1000, multiplier = 2.0),
			dltTopicSuffix = "-dlt",
			exclude = NonRetryableEventException.class,
			traversingCauses = "true")
	@KafkaListener(topics = KafkaTopics.CONTACT_MEDIUM_EVENTS, groupId = KafkaConsumerGroups.CUSTOMER_SERVICE,
			containerFactory = "contactMediumKafkaListenerContainerFactory")
	public void onContactMediumEvent(ContactMediumEvent event) {
		contactMediumEventHandler.handle(event);
	}

	@DltHandler
	public void onContactMediumEventDlt(ContactMediumEvent event,
			@Header(value = KafkaHeaders.EXCEPTION_FQCN, required = false) String exceptionType,
			@Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage) {
		log.error(LogMessages.CONTACT_MEDIUM_EVENT_DLT, event.eventId(), event.type(), exceptionType, exceptionMessage);
		meterRegistry.counter(DltMetrics.DLT_EVENTS_COUNTER, DltMetrics.TAG_EVENT_TYPE, event.type(),
				DltMetrics.TAG_LISTENER, AlertTags.CONTACT_MEDIUM_EVENT_LISTENER).increment();
		dltAlertNotifier.alert(KafkaConsumerGroups.CUSTOMER_SERVICE, AlertTags.CONTACT_MEDIUM_EVENT_LISTENER, event.type(),
				exceptionType, exceptionMessage);
	}
}
