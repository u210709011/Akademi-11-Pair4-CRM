
package com.etiya.crm.productservice.business.exceptions;
import com.etiya.crm.productservice.constants.MessageKeys;

public class ResourceSpecNotFoundException extends BusinessException {
    public ResourceSpecNotFoundException(Long resourceSpecId) {
        super(MessageKeys.RESOURCE_SPEC_NOT_FOUND, resourceSpecId);
    }
}