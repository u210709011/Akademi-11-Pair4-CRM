package com.etiya.crm.productservice.business.exceptions;

import com.etiya.crm.productservice.constants.MessageKeys;

public class ProductNotFoundException extends BusinessException {

    public ProductNotFoundException(Long productId) {
        super(MessageKeys.PRODUCT_NOT_FOUND, productId);
    }
}
