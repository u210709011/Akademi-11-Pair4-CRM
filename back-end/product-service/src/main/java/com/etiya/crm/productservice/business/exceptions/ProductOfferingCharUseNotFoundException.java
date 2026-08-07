package com.etiya.crm.productservice.business.exceptions;
import com.etiya.crm.productservice.constants.MessageKeys;

public class ProductOfferingCharUseNotFoundException extends BusinessException {
    public ProductOfferingCharUseNotFoundException(Long id) {
        super(MessageKeys.PRODUCT_OFFERING_CHAR_USE_NOT_FOUND, id);
    }
}