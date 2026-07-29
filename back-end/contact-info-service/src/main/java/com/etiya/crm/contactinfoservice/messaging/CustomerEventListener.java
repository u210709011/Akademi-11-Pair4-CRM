package com.etiya.crm.contactinfoservice.messaging;

import com.etiya.crm.contactinfoservice.business.abstracts.AddressService;
import com.etiya.crm.contactinfoservice.business.abstracts.ContactMediumService;
import com.etiya.crm.contactinfoservice.constants.LogMessages;
import com.etiya.crm.shared.events.KafkaTopics;
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
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * "customer-events" topic'ini (Debezium outbox, yayinci customer-service)
 * dinler; sadece CustomerDeleted{eventId, type, custId, partyRoleId, dataTypeId}
 * ile ilgilenir ve musteriye ait adres/iletisim kayitlarini pasife ceker (AS-002:
 * silme = statu guncellemesi). dataTypeId customer-service tarafinda dinamik
 * cozulmus gelir, burada hardcode edilmez. "type" ve "eventId" payload'un icine gomulu
 * (self-describing, bkz. shared-events CustomerDeletedEvent, party-service'teki
 * ayni desen) - eskiden burada bir "eventType" Kafka header'ina bakiliyordu,
 * ama Debezium EventRouter bu header'i hic yaymiyordu (connector config'inde
 * table.fields.additional.placement tanimli degildi), bu yuzden HER mesaj
 * sessizce atlaniyordu; artik payload'daki type alanina bakiliyor. Bu servisin
 * Kafka consumer'i varsayilan StringDeserializer ile calisiyor (spring.json.value.default.type
 * yok), bu yuzden ConsumerRecord&lt;String,String&gt; + manuel ObjectMapper.readValue(...)
 * korunur (party-service'in aksine, orada JsonDeserializer default type ile calisiyor).
 *
 * "customer-events"in party-service ile ORTAK bir tuketicisi var - retry/dlt
 * suffix'leri her iki tuketicide de FARKLI olmali, aksi halde ikisi de ayni
 * "customer-events-dlt" topic'ini kullanmaya calisir ve mesajlar karisir.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerEventListener {

    private final AddressService addressService;
    private final ContactMediumService contactMediumService;
    private final InboxEventRepository inboxEventRepository;
    private final ObjectMapper objectMapper;

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            retryTopicSuffix = "-retry-contact-info",
            dltTopicSuffix = "-dlt-contact-info",
            include = Exception.class)
    @KafkaListener(topics = KafkaTopics.CUSTOMER_EVENTS, groupId = "contact-info-service")
    @Transactional
    public void onMessage(ConsumerRecord<String, String> record) {
        CustomerDeletedEvent event;
        try {
            event = objectMapper.readValue(record.value(), CustomerDeletedEvent.class);
        } catch (JsonProcessingException e) {
            log.error(LogMessages.CUSTOMER_EVENT_PAYLOAD_PARSE_FAILED, record.value(), e);
            return;
        }

        if (!CustomerEventTypes.CUSTOMER_DELETED.equals(event.type())) {
            return;
        }

        if (inboxEventRepository.existsById(event.eventId())) {
            log.info(LogMessages.CUSTOMER_EVENT_ALREADY_PROCESSED, event.custId());
            return;
        }

        addressService.deactivateAllForRow(event.custId(), event.dataTypeId());
        contactMediumService.deactivateAllForRow(event.custId(), event.dataTypeId());
        inboxEventRepository.save(InboxEvent.of(event.eventId(), CustomerEventTypes.CUSTOMER_DELETED));
    }

}
