package com.etiya.crm.orderservice.clients.responses;

import java.util.List;

// customer-service GET /customers/{custId}/accounts, org.springframework.data.domain.Page<> dondurur -
// Jackson bunu {"content":[...], "pageable":{...}, ...} olarak serialize eder (duz array degil).
// Feign'in bunu decode edebilmesi icin sadece "content" alanini okuyan bu wrapper kullanilir;
// tanimadigi diger alanlar (pageable, totalElements, ...) varsayilan Jackson ayariyla yok sayilir.
public record CustomerAccountPageResponse(

    List<CustomerAccountResponse> content
) {

}
