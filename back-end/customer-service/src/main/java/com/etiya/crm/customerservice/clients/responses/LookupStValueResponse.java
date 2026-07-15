package com.etiya.crm.customerservice.clients.responses;

/** lookup-service'teki gnl_st tablosu verilerini temsil eden response. */
public record LookupStValueResponse(Long st_id, String shrt_code, String ent_code_name, String ent_name, String name, String descr) {
}
