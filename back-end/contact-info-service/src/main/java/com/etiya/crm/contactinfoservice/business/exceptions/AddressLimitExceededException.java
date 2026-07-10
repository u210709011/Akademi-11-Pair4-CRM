package com.etiya.crm.contactinfoservice.business.exceptions;

public class AddressLimitExceededException extends RuntimeException {

    public AddressLimitExceededException(String message) {
        super(message);
    }

}
