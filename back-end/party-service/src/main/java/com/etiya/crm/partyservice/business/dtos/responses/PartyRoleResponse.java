package com.etiya.crm.partyservice.business.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CUSTOMER_SERVICE_CONTRACTS.md SS2 ile birebir.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartyRoleResponse {

    private Long partyId;

    private Long partyRoleId;
}
