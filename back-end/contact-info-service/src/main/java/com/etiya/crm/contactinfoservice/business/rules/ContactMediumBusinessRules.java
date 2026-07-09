package com.etiya.crm.contactinfoservice.business.rules;

import com.etiya.crm.contactinfoservice.business.exceptions.ContactMediumNotFoundException;
import com.etiya.crm.contactinfoservice.business.exceptions.InvalidContactMediumFormatException;
import com.etiya.crm.contactinfoservice.clients.LookupClient;
import com.etiya.crm.contactinfoservice.constants.LookupCodes;
import com.etiya.crm.contactinfoservice.constants.LookupGroups;
import com.etiya.crm.contactinfoservice.dataAccess.abstracts.ContactMediumRepository;
import com.etiya.crm.contactinfoservice.entities.concretes.ContactMedium;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ContactMediumBusinessRules {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{7,15}$");

    private final ContactMediumRepository contactMediumRepository;
    private final LookupClient lookupClient;

    public ContactMediumBusinessRules(ContactMediumRepository contactMediumRepository, LookupClient lookupClient) {
        this.contactMediumRepository = contactMediumRepository;
        this.lookupClient = lookupClient;
    }

    public ContactMedium checkIfContactMediumExists(Long id) {
        return contactMediumRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ContactMediumNotFoundException("ContactMedium not found with id: " + id));
    }

    public void checkDataFormat(String cntcData, Long cntcMediumTypeId) {
        String typeCode = lookupClient.getById(LookupGroups.CONTACT_MEDIUM_TYPE, cntcMediumTypeId).code();

        if (LookupCodes.CONTACT_MEDIUM_EMAIL.equals(typeCode)) {
            if (!EMAIL_PATTERN.matcher(cntcData).matches()) {
                throw new InvalidContactMediumFormatException("Invalid email format");
            }
        } else if (LookupCodes.CONTACT_MEDIUM_MOBILE_PHONE.equals(typeCode)
                || LookupCodes.CONTACT_MEDIUM_HOME_PHONE.equals(typeCode)
                || LookupCodes.CONTACT_MEDIUM_FAX.equals(typeCode)) {
            if (!PHONE_PATTERN.matcher(cntcData).matches()) {
                throw new InvalidContactMediumFormatException("Invalid phone number format");
            }
        }
    }

}
