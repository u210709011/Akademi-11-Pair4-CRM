package com.etiya.crm.orderservice.clients.responses;

import java.util.List;

// Spring Data Page<T>'nin Feign tarafinda deserialize edilebilmesi icin wrapper 
public record PageResponse<T>(List<T> content) {
}
