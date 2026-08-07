package com.etiya.crm.productservice.business.exceptions;
import com.etiya.crm.productservice.constants.MessageKeys;

public class ServiceSpecNotFoundException extends BusinessException {
    public ServiceSpecNotFoundException(Long serviceSpecId) {
        super(MessageKeys.SERVICE_SPEC_NOT_FOUND, serviceSpecId);
    }
}