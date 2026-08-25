package com.etiya.crm.customerservice.config;

import java.util.Map;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.etiya.crm.shared.events.party.PartyEvent;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class PartyConsumerConfig {

	private final KafkaProperties kafkaProperties;

	@Bean
	public ConsumerFactory<String, PartyEvent> partyConsumerFactory() {
		Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
		props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, PartyEvent.class.getName());
		return new DefaultKafkaConsumerFactory<>(props);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, PartyEvent> partyKafkaListenerContainerFactory(
			ConsumerFactory<String, PartyEvent> partyConsumerFactory) {
		ConcurrentKafkaListenerContainerFactory<String, PartyEvent> factory =
				new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(partyConsumerFactory);
		return factory;
	}
}
