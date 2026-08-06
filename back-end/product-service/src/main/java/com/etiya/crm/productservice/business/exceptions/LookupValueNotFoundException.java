package com.etiya.crm.productservice.business.exceptions;

import com.etiya.crm.productservice.constants.MessageKeys;

public class LookupValueNotFoundException extends BusinessException {

    public LookupValueNotFoundException(String entCodeName, String shrtCode) {
        super(MessageKeys.LOOKUP_VALUE_NOT_FOUND, entCodeName, shrtCode);
    }
}