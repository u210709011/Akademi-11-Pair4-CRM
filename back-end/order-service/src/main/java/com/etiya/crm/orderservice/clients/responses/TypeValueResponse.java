package com.etiya.crm.orderservice.clients.responses;

// same as lookup service
public record TypeValueResponse(
    Long typeValueId,
    String tableName,
    Long fieldName,
    String description,
    String value
) {
}
