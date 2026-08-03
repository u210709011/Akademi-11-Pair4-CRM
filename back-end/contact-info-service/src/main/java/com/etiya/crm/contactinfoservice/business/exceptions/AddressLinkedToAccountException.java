package com.etiya.crm.contactinfoservice.business.exceptions;

import com.etiya.crm.contactinfoservice.constants.MessageKeys;

public class AddressLinkedToAccountException extends BusinessException {

    public AddressLinkedToAccountException() {
        super(MessageKeys.ADDRESS_LINKED_TO_BILLING_ACCOUNT);
    }

}
