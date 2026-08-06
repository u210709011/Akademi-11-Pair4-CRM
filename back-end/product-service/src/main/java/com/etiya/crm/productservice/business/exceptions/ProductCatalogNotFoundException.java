package com.etiya.crm.productservice.business.exceptions;

import com.etiya.crm.productservice.constants.MessageKeys;

public class ProductCatalogNotFoundException extends BusinessException {

    public ProductCatalogNotFoundException(Long productCatalogId) {
        super(MessageKeys.PRODUCT_CATALOG_NOT_FOUND, productCatalogId);
    }
}
