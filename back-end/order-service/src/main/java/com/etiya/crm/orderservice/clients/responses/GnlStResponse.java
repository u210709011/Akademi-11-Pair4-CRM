package com.etiya.crm.orderservice.clients.responses;

// same as lookup service
public record GnlStResponse(
    Long gnlStId,
    String name,
    String descr,
    String shrtCode,
    boolean active,
    String entCodeName,
    String entName
) {
}
