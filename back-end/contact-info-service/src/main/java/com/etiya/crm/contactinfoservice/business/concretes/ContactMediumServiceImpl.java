package com.etiya.crm.contactinfoservice.business.concretes;

import com.etiya.crm.contactinfoservice.business.abstracts.ContactMediumService;
import com.etiya.crm.contactinfoservice.business.dtos.requests.CreateContactMediumRequest;
import com.etiya.crm.contactinfoservice.business.dtos.requests.UpdateContactMediumRequest;
import com.etiya.crm.contactinfoservice.business.dtos.responses.ContactMediumResponse;
import com.etiya.crm.contactinfoservice.business.rules.ContactMediumBusinessRules;
import com.etiya.crm.contactinfoservice.dataAccess.abstracts.ContactMediumRepository;
import com.etiya.crm.contactinfoservice.entities.concretes.ContactMedium;
import com.etiya.crm.contactinfoservice.mapper.ContactMediumMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactMediumServiceImpl implements ContactMediumService {

    private final ContactMediumRepository contactMediumRepository;
    private final ContactMediumBusinessRules contactMediumBusinessRules;

    public ContactMediumServiceImpl(ContactMediumRepository contactMediumRepository,
                                     ContactMediumBusinessRules contactMediumBusinessRules) {
        this.contactMediumRepository = contactMediumRepository;
        this.contactMediumBusinessRules = contactMediumBusinessRules;
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
    public ContactMediumResponse add(CreateContactMediumRequest request) {
        contactMediumBusinessRules.checkDataFormat(request.getCntcData(), request.getCntcMediumTypeId());

        ContactMedium contactMedium = ContactMediumMapper.toEntity(request);
        ContactMedium saved = contactMediumRepository.save(contactMedium);
        return ContactMediumMapper.toResponse(saved);
    }

    @Override
    public ContactMediumResponse update(Long id, UpdateContactMediumRequest request) {
        ContactMedium contactMedium = contactMediumBusinessRules.checkIfContactMediumExists(id);
        contactMediumBusinessRules.checkDataFormat(request.getCntcData(), request.getCntcMediumTypeId());

        ContactMediumMapper.updateEntity(contactMedium, request);
        ContactMedium saved = contactMediumRepository.save(contactMedium);
        return ContactMediumMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        ContactMedium contactMedium = contactMediumBusinessRules.checkIfContactMediumExists(id);
        contactMedium.setActive(false);
        contactMediumRepository.save(contactMedium);
    }

    @Override
    public void deactivateAllForRow(Long rowId, Long dataTypeId) {
        List<ContactMedium> contactMediums = contactMediumRepository.findAllByRowIdAndDataTypeIdAndActiveTrue(rowId, dataTypeId);
        contactMediums.forEach(contactMedium -> contactMedium.setActive(false));
        contactMediumRepository.saveAll(contactMediums);
    }

}
