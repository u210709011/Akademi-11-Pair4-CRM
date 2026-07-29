package com.etiya.crm.partyservice.messaging;

import com.etiya.crm.partyservice.business.abstracts.PartyRoleService;
import com.etiya.crm.partyservice.constants.LogMessages;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.customer.CustomerDeletedEvent;
import com.etiya.crm.shared.events.customer.CustomerEventTypes;
import com.etiya.crm.shared.events.inbox.InboxEvent;
import com.etiya.crm.shared.events.inbox.InboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
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
 *
 * "customer-events"in contact-info-service ile ORTAK bir tuketicisi var - retry/dlt
 * suffix'leri her iki tuketicide de FARKLI olmali, aksi halde ikisi de ayni
 * "customer-events-dlt" topic'ini kullanmaya calisir ve mesajlar karisir.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerEventListener {

    private final PartyRoleService partyRoleService;
    private final InboxEventRepository inboxEventRepository;

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            retryTopicSuffix = "-retry-party",
            dltTopicSuffix = "-dlt-party",
            include = Exception.class)
    @KafkaListener(topics = KafkaTopics.CUSTOMER_EVENTS, groupId = "party-service")
    @Transactional
    public void onMessage(CustomerDeletedEvent event) {
        if (!CustomerEventTypes.CUSTOMER_DELETED.equals(event.type())) {
            return;
        }

        if (inboxEventRepository.existsById(event.eventId())) {
            log.info(LogMessages.CUSTOMER_EVENT_ALREADY_PROCESSED, event.partyRoleId());
            return;
        }

        partyRoleService.deactivatePartyRole(event.partyRoleId());
        inboxEventRepository.save(InboxEvent.of(event.eventId(), event.type()));
    }
}
