package com.etiya.crm.contactinfoservice.messaging;

import com.etiya.crm.contactinfoservice.business.abstracts.AddressService;
import com.etiya.crm.contactinfoservice.business.abstracts.ContactMediumService;
import com.etiya.crm.shared.contracts.lookup.DataTypeIds;
import com.etiya.crm.contactinfoservice.inbox.Inbox;
import com.etiya.crm.contactinfoservice.inbox.InboxRepository;
import com.etiya.crm.shared.events.customer.CustomerDeletedEvent;
import com.etiya.crm.shared.events.customer.CustomerEventTypes;
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
 * ilgilenir ve musteriye ait adres/iletisim kayitlarini pasife ceker (AS-002:
 * silme = statu guncellemesi). "type" ve "eventId" payload'un icine gomulu
 * (self-describing, bkz. shared-events CustomerDeletedEvent, party-service'teki
 * ayni desen) - eskiden burada bir "eventType" Kafka header'ina bakiliyordu,
 * ama Debezium EventRouter bu header'i hic yaymiyordu (connector config'inde
 * table.fields.additional.placement tanimli degildi), bu yuzden HER mesaj
 * sessizce atlaniyordu; artik payload'daki type alanina bakiliyor. Bu servisin
 * Kafka consumer'i varsayilan StringDeserializer ile calisiyor (spring.json.value.default.type
 * yok), bu yuzden ConsumerRecord&lt;String,String&gt; + manuel ObjectMapper.readValue(...)
 * korunur (party-service'in aksine, orada JsonDeserializer default type ile calisiyor).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerEventListener {

    private final AddressService addressService;
    private final ContactMediumService contactMediumService;
    private final InboxRepository inboxRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "customer-events", groupId = "contact-info-service")
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

        if (inboxRepository.existsById(event.eventId())) {
            log.info("customer-events mesaji zaten islenmis, atlaniyor: custId={}", event.custId());
            return;
        }

        addressService.deactivateAllForRow(event.custId(), DataTypeIds.CUSTOMER);
        contactMediumService.deactivateAllForRow(event.custId(), DataTypeIds.CUSTOMER);
        inboxRepository.save(new Inbox(event.eventId(), CustomerEventTypes.CUSTOMER_DELETED, null));
    }

}
