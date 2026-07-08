package com.etiya.crm.contactinfoservice.business.rules;

import com.etiya.crm.contactinfoservice.business.exceptions.ContactMediumNotFoundException;
import com.etiya.crm.contactinfoservice.dataAccess.abstracts.ContactMediumRepository;
import com.etiya.crm.contactinfoservice.entities.concretes.ContactMedium;
import org.springframework.stereotype.Component;

@Component
public class ContactMediumBusinessRules {

    private final ContactMediumRepository contactMediumRepository;

    public ContactMediumBusinessRules(ContactMediumRepository contactMediumRepository) {
        this.contactMediumRepository = contactMediumRepository;
    }

    public ContactMedium checkIfContactMediumExists(Long id) {
        return contactMediumRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ContactMediumNotFoundException("ContactMedium not found with id: " + id));
    }

}
