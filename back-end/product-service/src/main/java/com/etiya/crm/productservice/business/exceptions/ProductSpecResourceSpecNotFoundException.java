package com.etiya.crm.productservice.business.exceptions;

import com.etiya.crm.productservice.constants.MessageKeys;

public class ProductSpecResourceSpecNotFoundException extends BusinessException {

    public ProductSpecResourceSpecNotFoundException(Long productSpecResourceSpecId) {
        super(MessageKeys.PRODUCT_SPEC_RESOURCE_SPEC_NOT_FOUND, productSpecResourceSpecId);
    }
}