package com.etiya.crm.contactinfoservice.business.exceptions;

import com.etiya.crm.contactinfoservice.constants.MessageKeys;

public class ContactMediumNotFoundException extends BusinessException {

    public ContactMediumNotFoundException(Long id) {
        super(MessageKeys.CONTACT_MEDIUM_NOT_FOUND, id);
    }

}
