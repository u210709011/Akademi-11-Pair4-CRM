package com.etiya.crm.contactinfoservice.business.exceptions;

/** messageKey caller'dan gelir (bkz. MessageKeys.CONTACT_MEDIUM_INVALID_*_FORMAT) - format turune gore degisir. */
public class InvalidContactMediumFormatException extends BusinessException {

    public InvalidContactMediumFormatException(String messageKey) {
        super(messageKey);
    }

}
