package com.etiya.crm.contactinfoservice.messaging;

import com.etiya.crm.contactinfoservice.business.abstracts.AddressService;
import com.etiya.crm.contactinfoservice.business.abstracts.ContactMediumService;
import com.etiya.crm.contactinfoservice.constants.DataTypeIds;
import com.etiya.crm.contactinfoservice.inbox.Inbox;
import com.etiya.crm.contactinfoservice.inbox.InboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerEventListenerTest {

    private static final String PAYLOAD = "{\"custId\":10,\"partyRoleId\":20}";

    @Mock
    private AddressService addressService;

    @Mock
    private ContactMediumService contactMediumService;

    @Mock
    private InboxRepository inboxRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private CustomerEventListener customerEventListener;

    @Test
    void onMessage_deactivatesAddressAndContactMedium_whenCustomerDeletedEventReceived() {
        ConsumerRecord<String, String> record = recordWithEventType("CustomerDeleted");
        when(inboxRepository.existsById(any())).thenReturn(false);

        customerEventListener.onMessage(record);

        verify(addressService).deactivateAllForRow(10L, DataTypeIds.CUSTOMER);
        verify(contactMediumService).deactivateAllForRow(10L, DataTypeIds.CUSTOMER);
        verify(inboxRepository).save(any(Inbox.class));
    }

    @Test
    void onMessage_skips_whenEventAlreadyProcessed() {
        ConsumerRecord<String, String> record = recordWithEventType("CustomerDeleted");
        when(inboxRepository.existsById(any())).thenReturn(true);

        customerEventListener.onMessage(record);

        verify(addressService, never()).deactivateAllForRow(anyLong(), anyLong());
        verify(contactMediumService, never()).deactivateAllForRow(anyLong(), anyLong());
        verify(inboxRepository, never()).save(any());
    }

    @Test
    void onMessage_skips_whenEventTypeHeaderMissing() {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("customer-events", 0, 0L, "key", PAYLOAD);

        customerEventListener.onMessage(record);

        verify(addressService, never()).deactivateAllForRow(anyLong(), anyLong());
        verify(inboxRepository, never()).existsById(any());
    }

    @Test
    void onMessage_skips_whenEventTypeIsNotCustomerDeleted() {
        ConsumerRecord<String, String> record = recordWithEventType("CustomerOnboarded");

        customerEventListener.onMessage(record);

        verify(addressService, never()).deactivateAllForRow(anyLong(), anyLong());
        verify(inboxRepository, never()).existsById(any());
    }

    private ConsumerRecord<String, String> recordWithEventType(String eventType) {
        RecordHeaders headers = new RecordHeaders();
        headers.add("eventType", eventType.getBytes(StandardCharsets.UTF_8));
        return new ConsumerRecord<>("customer-events", 0, 0L, System.currentTimeMillis(),
                org.apache.kafka.common.record.TimestampType.CREATE_TIME, -1, -1,
                "key", PAYLOAD, headers, java.util.Optional.empty());
    }

}
