package com.etiya.crm.productservice.constants;

public final class MessageKeys {

    public static final String PRODUCT_SPEC_NOT_FOUND = "error.product-spec.not-found";
    public static final String PRODUCT_NOT_FOUND = "error.product.not-found";
    public static final String PRODUCT_OFFERING_NOT_FOUND = "error.product-offering.not-found";
    public static final String PRODUCT_CATALOG_NOT_FOUND = "error.product-catalog.not-found";
    public static final String PRODUCT_CATALOG_OFFERING_NOT_FOUND = "error.product-catalog-offering.not-found";
    public static final String CAMPAIGN_NOT_FOUND = "error.campaign.not-found";
    public static final String LOOKUP_VALUE_NOT_FOUND = "error.lookup.value-not-found";
    public static final String VALIDATION_FAILED = "error.validation.failed";
    public static final String DOWNSTREAM_CALL_FAILED = "error.downstream.call-failed";
    public static final String DOWNSTREAM_UNAVAILABLE = "error.downstream.unavailable";
    public static final String UNEXPECTED_ERROR = "error.unexpected";
    public static final String PRODUCT_RELATION_NOT_FOUND = "error.product-relation.not-found";
    public static final String PRODUCT_SPEC_RESOURCE_SPEC_NOT_FOUND = "error.product-spec-resource-spec.not-found";
    public static final String PRODUCT_SPEC_SERVICE_SPEC_NOT_FOUND = "error.product-spec-service-spec.not-found";
    public static final String PRODUCT_CHARACTERISTIC_VALUE_NOT_FOUND = "error.product-characteristic-value.not-found";
    public static final String RESOURCE_SPEC_NOT_FOUND = "error.resource-spec.not-found";
    public static final String SERVICE_SPEC_NOT_FOUND = "error.service-spec.not-found";
    public static final String CHARACTERISTIC_NOT_FOUND = "error.characteristic.not-found";
    public static final String CHARACTERISTIC_VALUE_NOT_FOUND = "error.characteristic-value.not-found";
    public static final String PRODUCT_OFFERING_RELATION_NOT_FOUND = "error.product-offering-relation.not-found";
    public static final String CAMPAIGN_OFFERING_NOT_FOUND = "error.campaign-offering.not-found";
    public static final String PRODUCT_OFFERING_CHAR_USE_NOT_FOUND = "error.product-offering-char-use.not-found";
    public static final String PRODUCT_OFFERING_CHAR_USE_DUPLICATE = "error.product-offering-char-use.duplicate";
    public static final String PARAMETER_TYPE_MISMATCH = "validation.parameter.type-mismatch";
    public static final String INVALID_REQUEST_PARAMETER = "validation.parameter.invalid";
    public static final String MISSING_REQUEST_PARAMETER = "validation.parameter.missing";
    public static final String METHOD_NOT_SUPPORTED = "error.http.method-not-allowed";
    public static final String ROUTE_NOT_FOUND = "error.http.route-not-found";

    // Bean Validation - generic field concepts shared across Create/Update DTO pairs
    // (and across entities where the underlying field concept is identical)
    public static final String STATUS_CODE_REQUIRED = "validation.status-code.required";
    public static final String NAME_REQUIRED = "validation.name.required";
    public static final String NAME_MAX_LENGTH = "validation.name.max-length";
    public static final String DESCRIPTION_REQUIRED = "validation.description.required";
    public static final String DESCRIPTION_MAX_LENGTH = "validation.description.max-length";
    public static final String ACTIVE_REQUIRED = "validation.active.required";
    public static final String START_DATE_REQUIRED = "validation.start-date.required";
    public static final String RELATION_TYPE_CODE_REQUIRED = "validation.relation-type-code.required";

    public static final String CAMPAIGN_CODE_REQUIRED = "validation.campaign.code-required";
    public static final String CAMPAIGN_PENALTY_REQUIRED = "validation.campaign.penalty-required";
    public static final String CAMPAIGN_ID_REQUIRED = "validation.campaign.id-required";
    public static final String DISCOUNT_PCT_REQUIRED = "validation.campaign-offering.discount-pct-required";
    public static final String DISCOUNT_PCT_MIN = "validation.campaign-offering.discount-pct-min";
    public static final String DISCOUNT_PCT_MAX = "validation.campaign-offering.discount-pct-max";

    public static final String PRODUCT_ID_REQUIRED = "validation.product.id-required";
    public static final String PRODUCT_ID_SOURCE_REQUIRED = "validation.product.source-required";
    public static final String PRODUCT_ID_TARGET_REQUIRED = "validation.product.target-required";

    public static final String PRODUCT_OFFERING_ID_REQUIRED = "validation.product-offering.id-required";
    public static final String PRODUCT_OFFERING_SOURCE_REQUIRED = "validation.product-offering.source-required";
    public static final String PRODUCT_OFFERING_TARGET_REQUIRED = "validation.product-offering.target-required";

    public static final String PRODUCT_SPEC_ID_REQUIRED = "validation.product-spec.id-required";
    public static final String PRODUCT_SPEC_DEV_FLAG_REQUIRED = "validation.product-spec.dev-required";

    public static final String PRODUCT_CATALOG_ID_REQUIRED = "validation.product-catalog.id-required";

    public static final String CHARACTERISTIC_ID_REQUIRED = "validation.characteristic.id-required";
    public static final String MANDATORY_FLAG_REQUIRED = "validation.mandatory-flag.required";

    public static final String RESOURCE_SPEC_ID_REQUIRED = "validation.resource-spec.id-required";
    public static final String SERVICE_SPEC_ID_REQUIRED = "validation.service-spec.id-required";

    public static final String QTY_REQUIRED = "validation.qty.required";
    public static final String QTY_POSITIVE = "validation.qty.positive";

    public static final String PRICE_REQUIRED = "validation.price.required";
    public static final String PRICE_POSITIVE = "validation.price.positive";

    private MessageKeys() {
    }
}