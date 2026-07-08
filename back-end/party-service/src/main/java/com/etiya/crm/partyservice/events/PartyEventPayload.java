package com.etiya.crm.partyservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * "party-events" topic'ine (Debezium outbox) yayinlanan payload.
 * Alan adlari CUSTOMER_SERVICE_CONTRACTS.md SS5.1 ile birebir; customer-service
 * bunu ayni alan adlariyla JSON olarak deserialize ediyor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartyEventPayload {

    private UUID eventId;

    private String type;

    private Long partyRoleId;

    private String firstName;

    private String lastName;

    private String nationalId;
}
