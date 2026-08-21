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
import com.etiya.crm.shared.events.messaging.NonRetryableEventException;
import com.etiya.crm.shared.events.saga.SagaStepResultEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** "customer-deletion-saga-events" topic'ini dinleyen Kafka'ya ozgu ince adapter. */
@Slf4j
@Component
@RequiredArgsConstructor
public class SagaStepResultEventListener {

	private final SagaStepResultEventHandler sagaStepResultEventHandler;
	private final MeterRegistry meterRegistry;
	private final DltAlertNotifier dltAlertNotifier;

	@RetryableTopic(
			attempts = "4",
			backoff = @Backoff(delay = 1000, multiplier = 2.0),
			dltTopicSuffix = "-dlt",
			exclude = NonRetryableEventException.class,
			traversingCauses = "true")
	@KafkaListener(topics = KafkaTopics.CUSTOMER_DELETION_SAGA_EVENTS, groupId = KafkaConsumerGroups.CUSTOMER_SERVICE,
			containerFactory = "sagaStepResultKafkaListenerContainerFactory")
	public void onSagaStepResult(SagaStepResultEvent event) {
		sagaStepResultEventHandler.handle(event);
	}

	@DltHandler
	public void onSagaStepResultDlt(SagaStepResultEvent event,
			@Header(value = KafkaHeaders.EXCEPTION_FQCN, required = false) String exceptionType,
			@Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage) {
		log.error(LogMessages.SAGA_STEP_RESULT_EVENT_DLT, event.eventId(), event.custId(), event.stepName(),
				exceptionType, exceptionMessage);
		meterRegistry.counter(DltMetrics.DLT_EVENTS_COUNTER, DltMetrics.TAG_EVENT_TYPE, event.type(),
				DltMetrics.TAG_LISTENER, AlertTags.SAGA_STEP_RESULT_EVENT_LISTENER).increment();
		dltAlertNotifier.alert(KafkaConsumerGroups.CUSTOMER_SERVICE, AlertTags.SAGA_STEP_RESULT_EVENT_LISTENER, event.type(),
				exceptionType, exceptionMessage);
	}
}
