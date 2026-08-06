package com.etiya.crm.productservice.business.exceptions;

import com.etiya.crm.productservice.constants.MessageKeys;

public class ProductSpecServiceSpecNotFoundException extends BusinessException {

    public ProductSpecServiceSpecNotFoundException(Long productSpecServiceSpecId) {
        super(MessageKeys.PRODUCT_SPEC_SERVICE_SPEC_NOT_FOUND, productSpecServiceSpecId);
    }
}