package com.etiya.crm.productservice.business.exceptions;

import com.etiya.crm.productservice.constants.MessageKeys;

public class ProductRelationNotFoundException extends BusinessException {

    public ProductRelationNotFoundException(Long productRelationId) {
        super(MessageKeys.PRODUCT_RELATION_NOT_FOUND, productRelationId);
    }
}