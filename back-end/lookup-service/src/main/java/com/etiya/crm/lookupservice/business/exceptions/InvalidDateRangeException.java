package com.etiya.crm.lookupservice.business.exceptions;

import com.etiya.crm.lookupservice.constants.MessageKeys;

/** GNL_CHAR_VAL.edate, sdate'den once olamaz. */
public class InvalidDateRangeException extends BusinessException {

    public InvalidDateRangeException(Object sdate, Object edate) {
        super(MessageKeys.GNL_CHAR_VAL_INVALID_DATE_RANGE, sdate, edate);
    }
}
