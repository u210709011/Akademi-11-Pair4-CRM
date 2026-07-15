package com.etiya.crm.customerservice.clients.responses;

/** lookup-service'teki gnl_tp tablosu verilerini temsil eden response. */
public record LookupTpValueResponse(Long tp_id, String shrt_code, String ent_code_name, String ent_name, String name, String descr) {
}
