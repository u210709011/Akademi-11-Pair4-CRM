package com.etiya.crm.lookupservice.business.exceptions;

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(String entityName, Object id) {
        super(entityName + " bulunamadi: id=" + id);
    }
}
