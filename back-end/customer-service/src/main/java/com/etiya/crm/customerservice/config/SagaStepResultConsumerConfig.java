package com.etiya.crm.customerservice.config;

import java.util.Map;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.etiya.crm.shared.events.saga.SagaStepResultEvent;

import lombok.RequiredArgsConstructor;

/** "customer-deletion-saga-events" icin ConsumerFactory + ContainerFactory - PartyConsumerConfig ile ayni desen. */
@Configuration
@RequiredArgsConstructor
public class SagaStepResultConsumerConfig {

	private final KafkaProperties kafkaProperties;

	@Bean
	public ConsumerFactory<String, SagaStepResultEvent> sagaStepResultConsumerFactory() {
		Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
		props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, SagaStepResultEvent.class.getName());
		return new DefaultKafkaConsumerFactory<>(props);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, SagaStepResultEvent> sagaStepResultKafkaListenerContainerFactory(
			ConsumerFactory<String, SagaStepResultEvent> sagaStepResultConsumerFactory) {
		ConcurrentKafkaListenerContainerFactory<String, SagaStepResultEvent> factory =
				new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(sagaStepResultConsumerFactory);
		return factory;
	}
}
