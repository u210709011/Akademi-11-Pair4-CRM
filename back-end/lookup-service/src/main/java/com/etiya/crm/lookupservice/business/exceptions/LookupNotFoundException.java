package com.etiya.crm.lookupservice.business.exceptions;

public class LookupNotFoundException extends BusinessException {

    public LookupNotFoundException(String groupCode, Object identifier) {
        super("Lookup bulunamadi: groupCode=" + groupCode + ", identifier=" + identifier);
    }
}
