package com.etiya.crm.orderservice.clients.responses;

// same as lookup service
public record GnlTpResponse(
    Long gnlTpId,
    String name,
    String descr,
    String shrtCode,
    boolean active
) {
}
