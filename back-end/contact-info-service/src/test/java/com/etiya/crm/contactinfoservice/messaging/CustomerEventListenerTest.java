package com.etiya.crm.contactinfoservice.messaging;

import com.etiya.crm.shared.events.customer.CustomerDeletedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Bu test sadece adapter'in kendi sorumlulugunu (JSON parse + handler'a devir)
 * dogrular - is mantigi (idempotency, deactivate cagrilari) artik
 * CustomerDeletedEventHandlerTest'te.
 */
@ExtendWith(MockitoExtension.class)
class CustomerEventListenerTest {

	@Mock
	private CustomerDeletedEventHandler customerDeletedEventHandler;

	@Spy
	private ObjectMapper objectMapper = new ObjectMapper();

	@InjectMocks
	private CustomerEventListener customerEventListener;

	@Test
	void onMessage_parsesPayload_andDelegatesToHandler() {
		ConsumerRecord<String, String> record = recordWithType("CustomerDeleted");

		customerEventListener.onMessage(record);

		ArgumentCaptor<CustomerDeletedEvent> captor = ArgumentCaptor.forClass(CustomerDeletedEvent.class);
		verify(customerDeletedEventHandler).handle(captor.capture());
		assertThat(captor.getValue().custId()).isEqualTo(10L);
		assertThat(captor.getValue().partyRoleId()).isEqualTo(20L);
		assertThat(captor.getValue().dataTypeId()).isEqualTo(12L);
	}

	@Test
	void onMessage_skips_whenPayloadIsNotValidJson() {
		ConsumerRecord<String, String> record = new ConsumerRecord<>("customer-events", 0, 0L, "key", "not-json");

		customerEventListener.onMessage(record);

		verify(customerDeletedEventHandler, never()).handle(org.mockito.ArgumentMatchers.any());
	}

	private ConsumerRecord<String, String> recordWithType(String type) {
		String payload = "{\"eventId\":\"9c1e6e2a-1b2c-4d3e-8f4a-000000000001\",\"type\":\"" + type
				+ "\",\"custId\":10,\"partyRoleId\":20,\"dataTypeId\":12}";
		return new ConsumerRecord<>("customer-events", 0, 0L, "key", payload);
	}
}
