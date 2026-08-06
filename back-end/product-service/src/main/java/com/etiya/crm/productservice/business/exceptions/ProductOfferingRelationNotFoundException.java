// ProductOfferingRelationNotFoundException.java
package com.etiya.crm.productservice.business.exceptions;
import com.etiya.crm.productservice.constants.MessageKeys;

public class ProductOfferingRelationNotFoundException extends BusinessException {
    public ProductOfferingRelationNotFoundException(Long productOfferingRelationId) {
        super(MessageKeys.PRODUCT_OFFERING_RELATION_NOT_FOUND, productOfferingRelationId);
    }
}