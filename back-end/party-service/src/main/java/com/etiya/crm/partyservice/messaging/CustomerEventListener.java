package com.etiya.crm.partyservice.messaging;

import com.etiya.crm.partyservice.business.abstracts.PartyRoleService;
import com.etiya.crm.shared.events.customer.CustomerDeletedEvent;
import com.etiya.crm.shared.events.customer.CustomerEventTypes;
import com.etiya.crm.shared.events.inbox.InboxEvent;
import com.etiya.crm.shared.events.inbox.InboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * "customer-events" topic'ini (Debezium outbox, yayinci customer-service)
 * dinler; sadece CustomerDeleted{eventId, type, custId, partyRoleId} ile
 * ilgilenir. "type" ve "eventId" payload'un icine gomulu (self-describing,
 * bkz. shared-events CustomerDeletedEvent) - Debezium header'ina bagimli
 * degildir, bu yuzden ayrica bir "eventType" header kontrolu gerekmez.
 *
 * application.yml'deki spring.kafka.consumer.properties.spring.json.value.default.type
 * zaten CustomerDeletedEvent'e ayarli - JsonDeserializer bunu OTOMATIK olarak
 * bu tipe cevirir, bu yuzden burada manuel ObjectMapper.readValue(...) GEREKMEZ
 * (eskiden ConsumerRecord&lt;String,String&gt; alip elle parse ediliyordu, ama
 * consumer factory zaten deserialize edilmis bir CustomerDeletedEvent
 * verdiginden bu, "record.value()" cagrisinda ClassCastException'a yol acardi).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerEventListener {

    private final PartyRoleService partyRoleService;
    private final InboxEventRepository inboxEventRepository;

    @KafkaListener(topics = "customer-events", groupId = "party-service")
    @Transactional
    public void onMessage(CustomerDeletedEvent event) {
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
