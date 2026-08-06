package com.etiya.crm.productservice.business.exceptions;
import com.etiya.crm.productservice.constants.MessageKeys;

// CharacteristicValueNotFoundException.java
public class CharacteristicValueNotFoundException extends BusinessException {
    public CharacteristicValueNotFoundException(Long characteristicValueId) {
        super(MessageKeys.CHARACTERISTIC_VALUE_NOT_FOUND, characteristicValueId);
    }
}