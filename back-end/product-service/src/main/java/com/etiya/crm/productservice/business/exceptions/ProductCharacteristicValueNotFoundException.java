package com.etiya.crm.productservice.business.exceptions;

import com.etiya.crm.productservice.constants.MessageKeys;

public class ProductCharacteristicValueNotFoundException extends BusinessException {

    public ProductCharacteristicValueNotFoundException(Long productCharacteristicValueId) {
        super(MessageKeys.PRODUCT_CHARACTERISTIC_VALUE_NOT_FOUND, productCharacteristicValueId);
    }
}