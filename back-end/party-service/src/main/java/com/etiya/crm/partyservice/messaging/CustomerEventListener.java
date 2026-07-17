package com.etiya.crm.partyservice.messaging;

import com.etiya.crm.partyservice.business.abstracts.PartyRoleService;
import com.etiya.crm.shared.events.customer.CustomerDeletedEvent;
import com.etiya.crm.shared.events.customer.CustomerEventTypes;
import com.etiya.crm.shared.events.inbox.InboxEvent;
import com.etiya.crm.shared.events.inbox.InboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * "customer-events" topic'ini (Debezium outbox, yayinci customer-service)
 * dinler; sadece CustomerDeleted{eventId, type, custId, partyRoleId} ile
 * ilgilenir. "type" ve "eventId" payload'un icine gomulu (self-describing,
 * bkz. shared-events CustomerDeletedEvent) - Debezium header'ina bagimli
 * degildir, bu yuzden ayrica bir "eventType" header kontrolu gerekmez.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerEventListener {

    private final PartyRoleService partyRoleService;
    private final InboxEventRepository inboxEventRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "customer-events", groupId = "party-service")
    @Transactional
    public void onMessage(ConsumerRecord<String, String> record) {
        CustomerDeletedEvent event;
        try {
            event = objectMapper.readValue(record.value(), CustomerDeletedEvent.class);
        } catch (JsonProcessingException e) {
            log.error("customer-events payload parse edilemedi: {}", record.value(), e);
            return;
        }

        if (!CustomerEventTypes.CUSTOMER_DELETED.equals(event.type())) {
            return;
        }

        if (inboxEventRepository.existsById(event.eventId())) {
            log.info("customer-events mesaji zaten islenmis, atlaniyor: partyRoleId={}", event.partyRoleId());
            return;
        }

        partyRoleService.deactivatePartyRole(event.partyRoleId());
        inboxEventRepository.save(InboxEvent.of(event.eventId(), event.type()));
    }
}
