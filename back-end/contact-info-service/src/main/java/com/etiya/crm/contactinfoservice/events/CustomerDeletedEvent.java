package com.etiya.crm.contactinfoservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * "customer-events" topic'inden (Debezium outbox, yayinci customer-service)
 * tuketilen CustomerDeleted payload'i: { custId, partyRoleId }.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDeletedEvent {

    private Long custId;

    private Long partyRoleId;

}
