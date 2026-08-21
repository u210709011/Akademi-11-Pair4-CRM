package com.etiya.crm.customerservice.config;

import java.util.Map;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.etiya.crm.shared.events.contactmedium.ContactMediumEvent;

import lombok.RequiredArgsConstructor;

/**
 * ContactMediumEventListener'in dinledigi "contact-medium-events" icin ConsumerFactory +
 * ContainerFactory - PartyConsumerConfig ile ayni desen: her event tipi kendi tuketici
 * yapilandirmasini tasir, ortak/varsayilan bir hedef tipe guvenilmez. Diger tum tuketici
 * ayarlari (bootstrap-servers, group-id, error-handling deserializer, trusted packages, vb.)
 * KafkaProperties uzerinden aynen miras alinir, sadece VALUE_DEFAULT_TYPE degistirilir.
 */
@Configuration
@RequiredArgsConstructor
public class ContactMediumConsumerConfig {

	private final KafkaProperties kafkaProperties;

	@Bean
	public ConsumerFactory<String, ContactMediumEvent> contactMediumConsumerFactory() {
		Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
		props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ContactMediumEvent.class.getName());
		return new DefaultKafkaConsumerFactory<>(props);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, ContactMediumEvent> contactMediumKafkaListenerContainerFactory(
			ConsumerFactory<String, ContactMediumEvent> contactMediumConsumerFactory) {
		ConcurrentKafkaListenerContainerFactory<String, ContactMediumEvent> factory =
				new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(contactMediumConsumerFactory);
		return factory;
	}
}
