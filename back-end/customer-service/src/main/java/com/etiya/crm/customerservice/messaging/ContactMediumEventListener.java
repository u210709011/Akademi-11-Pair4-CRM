package com.etiya.crm.customerservice.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import com.etiya.crm.customerservice.constants.KafkaConsumerGroups;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.contactmedium.ContactMediumEvent;

import lombok.RequiredArgsConstructor;

/**
 * contact-info-service'in yayinladigi "contact-medium-events" topic'ini
 * dinleyen Kafka'ya OZGU ince adapter - deserialize edilmis event'i oldugu
 * gibi ContactMediumEventHandler'a devreder, is mantigi/idempotency/filtreleme
 * orada. Bu sinif broker-spesifiktir (KafkaListener/RetryableTopic); baska bir
 * mesajlasma aracina gecilirse sadece bu adapter degisir, handler ve testleri
 * etkilenmez. Kalici hatalarda 4 deneme sonrasi "contact-medium-events-dlt"
 * topic'ine dusurulur (DLQ).
 */
@Component
@RequiredArgsConstructor
public class ContactMediumEventListener {

	private final ContactMediumEventHandler contactMediumEventHandler;

	@RetryableTopic(
			attempts = "4",
			backoff = @Backoff(delay = 1000, multiplier = 2.0),
			dltTopicSuffix = "-dlt",
			include = Exception.class)
	@KafkaListener(topics = KafkaTopics.CONTACT_MEDIUM_EVENTS, groupId = KafkaConsumerGroups.CUSTOMER_SERVICE,
			containerFactory = "contactMediumKafkaListenerContainerFactory")
	public void onContactMediumEvent(ContactMediumEvent event) {
		contactMediumEventHandler.handle(event);
	}
}
