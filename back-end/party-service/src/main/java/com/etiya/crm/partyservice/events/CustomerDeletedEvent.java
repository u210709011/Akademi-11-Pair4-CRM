package com.etiya.crm.partyservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * "customer-events" topic'inden (Debezium outbox, yayinci customer-service)
 * tuketilen CustomerDeleted payload'i. CUSTOMER_SERVICE_CONTRACTS.md SS5.2 ile
 * birebir: { custId, partyRoleId }.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDeletedEvent {

    private Long custId;

    private Long partyRoleId;
}
