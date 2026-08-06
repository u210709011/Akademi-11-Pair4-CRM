package com.etiya.crm.productservice.business.exceptions;
import com.etiya.crm.productservice.constants.MessageKeys;

public class CharacteristicNotFoundException extends BusinessException {
    public CharacteristicNotFoundException(Long characteristicId) {
        super(MessageKeys.CHARACTERISTIC_NOT_FOUND, characteristicId);
    }
}