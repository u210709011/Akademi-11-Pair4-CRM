package com.etiya.crm.contactinfoservice.messaging;

import com.etiya.crm.contactinfoservice.business.abstracts.AddressService;
import com.etiya.crm.contactinfoservice.business.abstracts.ContactMediumService;
import com.etiya.crm.contactinfoservice.constants.DataTypeIds;
import com.etiya.crm.contactinfoservice.events.CustomerDeletedEvent;
import com.etiya.crm.contactinfoservice.inbox.Inbox;
import com.etiya.crm.contactinfoservice.inbox.InboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * "customer-events" topic'ini (Debezium outbox, yayinci customer-service)
 * dinler; sadece CustomerDeleted{custId, partyRoleId} ile ilgilenir ve
 * musteriye ait adres/iletisim kayitlarini pasife ceker (AS-002: silme =
 * statu guncellemesi). party-service'teki CustomerEventListener ile ayni
 * desen: eventType header'i, INBOX ile idempotency.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerEventListener {

    private static final String CUSTOMER_DELETED_TYPE = "CustomerDeleted";
    private static final String EVENT_TYPE_HEADER = "eventType";

    private final AddressService addressService;
    private final ContactMediumService contactMediumService;
    private final InboxRepository inboxRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "customer-events", groupId = "contact-info-service")
    @Transactional
    public void onMessage(ConsumerRecord<String, String> record) {
        String eventType = readEventTypeHeader(record);
        if (eventType == null) {
            log.warn("customer-events mesaji atlandi: '{}' header'i bulunamadi. payload={}",
                    EVENT_TYPE_HEADER, record.value());
            return;
        }
        if (!CUSTOMER_DELETED_TYPE.equals(eventType)) {
            return;
        }

        CustomerDeletedEvent event;
        try {
            event = objectMapper.readValue(record.value(), CustomerDeletedEvent.class);
        } catch (JsonProcessingException e) {
            log.error("customer-events payload parse edilemedi: {}", record.value(), e);
            return;
        }

        UUID inboxEventId = UUID.nameUUIDFromBytes(
                (CUSTOMER_DELETED_TYPE + "-" + event.getCustId()).getBytes(StandardCharsets.UTF_8));

        if (inboxRepository.existsById(inboxEventId)) {
            log.info("customer-events mesaji zaten islenmis, atlaniyor: custId={}", event.getCustId());
            return;
        }

        addressService.deactivateAllForRow(event.getCustId(), DataTypeIds.CUSTOMER);
        contactMediumService.deactivateAllForRow(event.getCustId(), DataTypeIds.CUSTOMER);
        inboxRepository.save(new Inbox(inboxEventId, CUSTOMER_DELETED_TYPE, null));
    }

    private String readEventTypeHeader(ConsumerRecord<String, String> record) {
        Header header = record.headers().lastHeader(EVENT_TYPE_HEADER);
        return header == null ? null : new String(header.value(), StandardCharsets.UTF_8);
    }

}
