package com.etiya.crm.contactinfoservice.business.exceptions;

import com.etiya.crm.contactinfoservice.constants.MessageKeys;

public class PrimaryAddressDeletionException extends BusinessException {

    public PrimaryAddressDeletionException() {
        super(MessageKeys.PRIMARY_ADDRESS_CANNOT_BE_DELETED);
    }

}
