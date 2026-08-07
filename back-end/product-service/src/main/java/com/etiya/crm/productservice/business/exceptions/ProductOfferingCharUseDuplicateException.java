package com.etiya.crm.productservice.business.exceptions;

import com.etiya.crm.productservice.constants.MessageKeys;

public class ProductOfferingCharUseDuplicateException extends BusinessException {
    public ProductOfferingCharUseDuplicateException(Long productOfferingId, Long characteristicId) {
        super(MessageKeys.PRODUCT_OFFERING_CHAR_USE_DUPLICATE, productOfferingId, characteristicId);
    }
}
