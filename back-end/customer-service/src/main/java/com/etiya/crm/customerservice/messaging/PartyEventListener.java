package com.etiya.crm.customerservice.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import com.etiya.crm.customerservice.constants.KafkaConsumerGroups;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.party.PartyEvent;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class PartyEventListener {

	private final PartyEventHandler partyEventHandler;

	@RetryableTopic(
			attempts = "4",
			backoff = @Backoff(delay = 1000, multiplier = 2.0),
			dltTopicSuffix = "-dlt",
			include = Exception.class)
	@KafkaListener(topics = KafkaTopics.PARTY_EVENTS, groupId = KafkaConsumerGroups.CUSTOMER_SERVICE)
	public void onPartyEvent(PartyEvent event) {
		partyEventHandler.handle(event);
	}
}
