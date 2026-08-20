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

import com.etiya.crm.shared.events.contactmedium.ContactMediumEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Gecmiste customer-service'te tum Kafka tuketicileri tek/paylasilan bir
 * ConsumerFactory kullaniyordu ve JsonDeserializer'in hedef tipi hep AYNI
 * event sinifina sabitlenmisti - farkli topic'lerden gelen mesajlar yanlis
 * tipe deserialize edilmeye calisilip sonsuz retry dongusune giriyordu
 * (bkz. commit 4abc2b5). Bu test, ContactMediumEvent icin dogru
 * VALUE_DEFAULT_TYPE'in ayarlandigini - yani o regresyonun bir daha
 * olmayacagini - dogrular.
 */
@ExtendWith(MockitoExtension.class)
class ContactMediumConsumerConfigTest {

	@Mock
	private KafkaProperties kafkaProperties;

	@Test
	void consumerFactory_targetsContactMediumEvent_notAnotherEventType() {
		when(kafkaProperties.buildConsumerProperties(null)).thenReturn(new HashMap<>());
		ContactMediumConsumerConfig config = new ContactMediumConsumerConfig(kafkaProperties);

		ConsumerFactory<String, ContactMediumEvent> factory = config.contactMediumConsumerFactory();

		assertThat(factory.getConfigurationProperties())
				.containsEntry(JsonDeserializer.VALUE_DEFAULT_TYPE, ContactMediumEvent.class.getName());
	}

	@Test
	void containerFactory_usesGivenConsumerFactory() {
		when(kafkaProperties.buildConsumerProperties(null)).thenReturn(new HashMap<>());
		ContactMediumConsumerConfig config = new ContactMediumConsumerConfig(kafkaProperties);
		ConsumerFactory<String, ContactMediumEvent> consumerFactory = config.contactMediumConsumerFactory();

		ConcurrentKafkaListenerContainerFactory<String, ContactMediumEvent> containerFactory = config
				.contactMediumKafkaListenerContainerFactory(consumerFactory);

		assertThat(containerFactory.getConsumerFactory()).isSameAs(consumerFactory);
	}
}
