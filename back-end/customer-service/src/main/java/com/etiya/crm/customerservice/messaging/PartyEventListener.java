package com.etiya.crm.customerservice.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.party.PartyEvent;

import lombok.RequiredArgsConstructor;

/**
 * party-service'in party-events topic'ine yayinladigi event'leri dinleyen
 * Kafka'ya OZGU ince adapter - deserialize edilmis event'i oldugu gibi
 * PartyEventHandler'a devreder, is mantigi/idempotency orada. Bu sinif
 * broker-spesifiktir (KafkaListener/RetryableTopic); baska bir mesajlasma
 * aracina gecilirse sadece bu adapter degisir, handler ve testleri etkilenmez.
 * Kalici hatalarda 4 deneme sonrasi "party-events-dlt" topic'ine dusurulur (DLQ).
 */
@Component
@RequiredArgsConstructor
public class PartyEventListener {

	private final PartyEventHandler partyEventHandler;

	@RetryableTopic(
			attempts = "4",
			backoff = @Backoff(delay = 1000, multiplier = 2.0),
			dltTopicSuffix = "-dlt",
			include = Exception.class)
	@KafkaListener(topics = KafkaTopics.PARTY_EVENTS, groupId = "customer-service")
	public void onPartyEvent(PartyEvent event) {
		partyEventHandler.handle(event);
	}
}
