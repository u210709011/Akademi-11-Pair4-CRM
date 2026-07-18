package com.etiya.crm.contactinfoservice.messaging;

import com.etiya.crm.contactinfoservice.business.abstracts.AddressService;
import com.etiya.crm.contactinfoservice.business.abstracts.ContactMediumService;
import com.etiya.crm.contactinfoservice.inbox.Inbox;
import com.etiya.crm.contactinfoservice.inbox.InboxRepository;
import com.etiya.crm.shared.contracts.lookup.DataTypeIds;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerEventListenerTest {

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
        ConsumerRecord<String, String> record = recordWithType("CustomerDeleted");
        when(inboxRepository.existsById(any())).thenReturn(false);

        customerEventListener.onMessage(record);

        verify(addressService).deactivateAllForRow(10L, DataTypeIds.CUSTOMER);
        verify(contactMediumService).deactivateAllForRow(10L, DataTypeIds.CUSTOMER);
        verify(inboxRepository).save(any(Inbox.class));
    }

    @Test
    void onMessage_skips_whenEventAlreadyProcessed() {
        ConsumerRecord<String, String> record = recordWithType("CustomerDeleted");
        when(inboxRepository.existsById(any())).thenReturn(true);

        customerEventListener.onMessage(record);

        verify(addressService, never()).deactivateAllForRow(anyLong(), anyLong());
        verify(contactMediumService, never()).deactivateAllForRow(anyLong(), anyLong());
        verify(inboxRepository, never()).save(any());
    }

    @Test
    void onMessage_skips_whenPayloadIsNotValidJson() {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("customer-events", 0, 0L, "key", "not-json");

        customerEventListener.onMessage(record);

        verify(addressService, never()).deactivateAllForRow(anyLong(), anyLong());
        verify(inboxRepository, never()).existsById(any());
    }

    @Test
    void onMessage_skips_whenEventTypeIsNotCustomerDeleted() {
        ConsumerRecord<String, String> record = recordWithType("CustomerOnboarded");

        customerEventListener.onMessage(record);

        verify(addressService, never()).deactivateAllForRow(anyLong(), anyLong());
        verify(inboxRepository, never()).existsById(any());
    }

    private ConsumerRecord<String, String> recordWithType(String type) {
        String payload = "{\"eventId\":\"9c1e6e2a-1b2c-4d3e-8f4a-000000000001\",\"type\":\"" + type
                + "\",\"custId\":10,\"partyRoleId\":20}";
        return new ConsumerRecord<>("customer-events", 0, 0L, "key", payload);
    }

}
