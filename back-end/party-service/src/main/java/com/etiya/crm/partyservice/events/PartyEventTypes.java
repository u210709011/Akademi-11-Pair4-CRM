package com.etiya.crm.partyservice.events;

/**
 * CUSTOMER_SERVICE_CONTRACTS.md SS5.1: "type" sadece IndividualPartyCreated
 * veya IndividualUpdated olabilir. Bu kapsamda sadece create akisi (POST
 * /api/individuals) var; update akisi (FR-004) bu promptun disinda, bu yuzden
 * IndividualUpdated burada tanimlanmadi.
 */
public final class PartyEventTypes {

    public static final String INDIVIDUAL_PARTY_CREATED = "IndividualPartyCreated";

    private PartyEventTypes() {
    }
}
