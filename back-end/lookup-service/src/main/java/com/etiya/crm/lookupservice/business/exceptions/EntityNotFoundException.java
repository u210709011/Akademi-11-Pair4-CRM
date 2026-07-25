package com.etiya.crm.lookupservice.business.exceptions;

import com.etiya.crm.lookupservice.constants.MessageKeys;

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(String entityName, Object id) {
        super(MessageKeys.ENTITY_NOT_FOUND, entityName, id);
    }
}
