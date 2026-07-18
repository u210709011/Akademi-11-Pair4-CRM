package com.etiya.crm.contactinfoservice.business.concretes;

import com.etiya.crm.contactinfoservice.business.abstracts.ContactMediumService;
import com.etiya.crm.contactinfoservice.business.rules.ContactMediumBusinessRules;
import com.etiya.crm.contactinfoservice.dataAccess.abstracts.ContactMediumRepository;
import com.etiya.crm.contactinfoservice.entities.concretes.ContactMedium;
import com.etiya.crm.contactinfoservice.mapper.ContactMediumMapper;
import com.etiya.crm.shared.contracts.contactmedium.ContactMediumResponse;
import com.etiya.crm.shared.contracts.contactmedium.CreateContactMediumRequest;
import com.etiya.crm.shared.contracts.contactmedium.UpdateContactMediumRequest;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.contactmedium.ContactMediumEvent;
import com.etiya.crm.shared.events.contactmedium.ContactMediumEventTypes;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ContactMediumServiceImpl implements ContactMediumService {

    private final ContactMediumRepository contactMediumRepository;
    private final ContactMediumBusinessRules contactMediumBusinessRules;
    private final OutboxEventPublisher outboxEventPublisher;

    public ContactMediumServiceImpl(ContactMediumRepository contactMediumRepository,
                                     ContactMediumBusinessRules contactMediumBusinessRules,
                                     OutboxEventPublisher outboxEventPublisher) {
        this.contactMediumRepository = contactMediumRepository;
        this.contactMediumBusinessRules = contactMediumBusinessRules;
        this.outboxEventPublisher = outboxEventPublisher;
    }

    @Override
    public List<ContactMediumResponse> getAll() {
        return contactMediumRepository.findAllByActiveTrue().stream()
                .map(ContactMediumMapper::toResponse)
                .toList();
    }

    @Override
    public ContactMediumResponse getById(Long id) {
        ContactMedium contactMedium = contactMediumBusinessRules.checkIfContactMediumExists(id);
        return ContactMediumMapper.toResponse(contactMedium);
    }

    @Override
    public List<ContactMediumResponse> getByRowIdAndDataTypeId(Long rowId, Long dataTypeId) {
        return contactMediumRepository.findAllByRowIdAndDataTypeIdAndActiveTrue(rowId, dataTypeId).stream()
                .map(ContactMediumMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ContactMediumResponse add(CreateContactMediumRequest request) {
        contactMediumBusinessRules.checkDataFormat(request.cntcData(), request.cntcMediumTypeId());

        ContactMedium contactMedium = ContactMediumMapper.toEntity(request);
        ContactMedium saved = contactMediumRepository.save(contactMedium);
        publishEvent(ContactMediumEventTypes.CONTACT_MEDIUM_CREATED, saved);
        return ContactMediumMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ContactMediumResponse update(Long id, UpdateContactMediumRequest request) {
        ContactMedium contactMedium = contactMediumBusinessRules.checkIfContactMediumExists(id);
        contactMediumBusinessRules.checkDataFormat(request.cntcData(), request.cntcMediumTypeId());

        ContactMediumMapper.updateEntity(contactMedium, request);
        ContactMedium saved = contactMediumRepository.save(contactMedium);
        publishEvent(ContactMediumEventTypes.CONTACT_MEDIUM_UPDATED, saved);
        return ContactMediumMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ContactMedium contactMedium = contactMediumBusinessRules.checkIfContactMediumExists(id);
        contactMedium.setActive(false);
        ContactMedium saved = contactMediumRepository.save(contactMedium);
        publishEvent(ContactMediumEventTypes.CONTACT_MEDIUM_DEACTIVATED, saved);
    }

    @Override
    public void deactivateAllForRow(Long rowId, Long dataTypeId) {
        List<ContactMedium> contactMediums = contactMediumRepository.findAllByRowIdAndDataTypeIdAndActiveTrue(rowId, dataTypeId);
        contactMediums.forEach(contactMedium -> contactMedium.setActive(false));
        contactMediumRepository.saveAll(contactMediums);
    }

    /**
     * Ayni transaction icinde outbox tablosuna insert eder; Debezium bu satiri
     * WAL'den okuyup "contact-medium-events" topic'ine yayinlar. rowId'nin
     * kime ait oldugu (customer/party/vb.) bu serviste bilinmez/bilinmemelidir;
     * filtreleme tuketici tarafinda yapilir (bkz. ContactMediumEvent).
     */
    private void publishEvent(String eventType, ContactMedium contactMedium) {
        ContactMediumEvent payload = new ContactMediumEvent(
                UUID.randomUUID(),
                eventType,
                contactMedium.getRowId(),
                contactMedium.getDataTypeId(),
                contactMedium.getCntcMediumTypeId(),
                contactMedium.getCntcData());

        outboxEventPublisher.publish(KafkaTopics.CONTACT_MEDIUM_AGGREGATE_TYPE,
                String.valueOf(contactMedium.getId()), eventType, payload);
    }

}
