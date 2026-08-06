package com.etiya.crm.productservice.business.exceptions;

import com.etiya.crm.productservice.constants.MessageKeys;

public class ProductCatalogOfferingNotFoundException extends BusinessException {

    public ProductCatalogOfferingNotFoundException(Long productCatalogOfferingId) {
        super(MessageKeys.PRODUCT_CATALOG_OFFERING_NOT_FOUND, productCatalogOfferingId);
    }
}
