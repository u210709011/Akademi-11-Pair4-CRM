package com.etiya.crm.contactinfoservice.business.exceptions;

import com.etiya.crm.contactinfoservice.constants.MessageKeys;

public class AddressNotFoundException extends BusinessException {

    public AddressNotFoundException(Long id) {
        super(MessageKeys.ADDRESS_NOT_FOUND, id);
    }

}
