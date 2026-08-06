package com.etiya.crm.productservice.business.exceptions;

import com.etiya.crm.productservice.constants.MessageKeys;

public class ProductOfferingNotFoundException extends BusinessException {

    public ProductOfferingNotFoundException(Long productOfferingId) {
        super(MessageKeys.PRODUCT_OFFERING_NOT_FOUND, productOfferingId);
    }
}
