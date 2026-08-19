package com.etiya.crm.customerservice.messaging;

import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import com.etiya.crm.customerservice.constants.KafkaConsumerGroups;
import com.etiya.crm.customerservice.constants.LogMessages;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.messaging.NonRetryableEventException;
import com.etiya.crm.shared.events.party.PartyEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@RequiredArgsConstructor
public class PartyEventListener {

	private final PartyEventHandler partyEventHandler;

	@RetryableTopic(
			attempts = "4",
			backoff = @Backoff(delay = 1000, multiplier = 2.0),
			dltTopicSuffix = "-dlt",
			include = Exception.class,
			exclude = NonRetryableEventException.class,
			traversingCauses = "true")
	@KafkaListener(topics = KafkaTopics.PARTY_EVENTS, groupId = KafkaConsumerGroups.CUSTOMER_SERVICE)
	public void onPartyEvent(PartyEvent event) {
		partyEventHandler.handle(event);
	}

	@DltHandler
	public void onPartyEventDlt(PartyEvent event,
			@Header(value = KafkaHeaders.EXCEPTION_FQCN, required = false) String exceptionType,
			@Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage) {
		log.error(LogMessages.PARTY_EVENT_DLT, event.eventId(), event.type(), exceptionType, exceptionMessage);
	}
}
