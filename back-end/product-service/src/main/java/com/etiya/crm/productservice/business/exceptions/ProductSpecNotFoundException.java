package com.etiya.crm.productservice.business.exceptions;

import com.etiya.crm.productservice.constants.MessageKeys;

public class ProductSpecNotFoundException extends BusinessException {

    public ProductSpecNotFoundException(Long productSpecId) {
        super(MessageKeys.PRODUCT_SPEC_NOT_FOUND, productSpecId);
    }
}