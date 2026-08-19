package com.etiya.crm.customerservice.config;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.etiya.crm.shared.events.party.PartyEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/** ContactMediumConsumerConfigTest ile ayni gerekce - bkz. o sinifin javadoc'u. */
@ExtendWith(MockitoExtension.class)
class PartyConsumerConfigTest {

	@Mock
	private KafkaProperties kafkaProperties;

	@Test
	void consumerFactory_targetsPartyEvent_notAnotherEventType() {
		when(kafkaProperties.buildConsumerProperties(null)).thenReturn(new HashMap<>());
		PartyConsumerConfig config = new PartyConsumerConfig(kafkaProperties);

		ConsumerFactory<String, PartyEvent> factory = config.partyConsumerFactory();

		assertThat(factory.getConfigurationProperties())
				.containsEntry(JsonDeserializer.VALUE_DEFAULT_TYPE, PartyEvent.class.getName());
	}

	@Test
	void containerFactory_usesGivenConsumerFactory() {
		when(kafkaProperties.buildConsumerProperties(null)).thenReturn(new HashMap<>());
		PartyConsumerConfig config = new PartyConsumerConfig(kafkaProperties);
		ConsumerFactory<String, PartyEvent> consumerFactory = config.partyConsumerFactory();

		ConcurrentKafkaListenerContainerFactory<String, PartyEvent> containerFactory = config
				.partyKafkaListenerContainerFactory(consumerFactory);

		assertThat(containerFactory.getConsumerFactory()).isSameAs(consumerFactory);
	}
}
