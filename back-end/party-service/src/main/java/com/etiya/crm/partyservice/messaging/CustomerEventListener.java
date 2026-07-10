package com.etiya.crm.partyservice.messaging;

import com.etiya.crm.partyservice.business.abstracts.PartyRoleService;
import com.etiya.crm.partyservice.events.CustomerDeletedEvent;
import com.etiya.crm.partyservice.inbox.Inbox;
import com.etiya.crm.partyservice.inbox.InboxRepository;
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
 * dinler; sadece CustomerDeleted{custId, partyRoleId} ile ilgilenir.
 *
 * TODO: netlestirilecek - CUSTOMER_SERVICE_CONTRACTS.md SS5.2'de gosterilen
 * customer-outbox-connector.json'da "table.fields.additional.placement"
 * ayarlanmamis; yani outbox.type kolonu (CustomerOnboarded / CustomerDeleted
 * ayrimi) su anki haliyle ne payload'a ne de header'a yansiyor gibi
 * gorunuyor. Bu kod, event tipinin "eventType" header'i olarak geldigini
 * varsayiyor - customer-service tarafinda dogrulanmali/netlestirilmeli.
 * Header yoksa mesaj YANLIŞLIKLA CustomerOnboarded'i CustomerDeleted sanip
 * PartyRole pasiflestirmemek icin ISLENMEDEN atlanir (sessizce varsayim
 * yapip veri bozmaktansa, log uyarisiyla atlamak tercih edildi).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerEventListener {

    private static final String CUSTOMER_DELETED_TYPE = "CustomerDeleted";
    private static final String EVENT_TYPE_HEADER = "eventType";

    private final PartyRoleService partyRoleService;
    private final InboxRepository inboxRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "customer-events", groupId = "party-service")
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

        // TODO: netlestirilecek - CUSTOMER_SERVICE_CONTRACTS.md SS5.2'deki
        // CustomerDeleted payload ornegi {custId, partyRoleId} - eventId alani
        // dokumante edilmemis. Inbox tablosu event_id (UUID) bekledigi icin,
        // partyRoleId'den deterministik bir UUID turetiliyor.
        UUID inboxEventId = UUID.nameUUIDFromBytes(
                (CUSTOMER_DELETED_TYPE + "-" + event.getPartyRoleId()).getBytes(StandardCharsets.UTF_8));

        if (inboxRepository.existsById(inboxEventId)) {
            log.info("customer-events mesaji zaten islenmis, atlaniyor: partyRoleId={}", event.getPartyRoleId());
            return;
        }

        partyRoleService.deactivatePartyRole(event.getPartyRoleId());
        inboxRepository.save(new Inbox(inboxEventId, CUSTOMER_DELETED_TYPE, null));
    }

    private String readEventTypeHeader(ConsumerRecord<String, String> record) {
        Header header = record.headers().lastHeader(EVENT_TYPE_HEADER);
        return header == null ? null : new String(header.value(), StandardCharsets.UTF_8);
    }
}
