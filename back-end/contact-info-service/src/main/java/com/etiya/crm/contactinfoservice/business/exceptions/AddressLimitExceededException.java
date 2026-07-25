package com.etiya.crm.contactinfoservice.business.exceptions;

import com.etiya.crm.contactinfoservice.constants.MessageKeys;

public class AddressLimitExceededException extends BusinessException {

    public AddressLimitExceededException() {
        super(MessageKeys.ADDRESS_MAX_EXCEEDED);
    }

}
