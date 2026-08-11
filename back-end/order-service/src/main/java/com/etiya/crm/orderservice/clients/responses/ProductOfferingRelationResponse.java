package com.etiya.crm.orderservice.clients.responses;

// FR-014 ACC-012/BR-04: sepetteki tekliflerin birbiriyle cakisip cakismadigini (EXCL) kontrol
// etmek icin - bkz. BasketValidationRules.ensureNoConflictingItems/ensureItemNotConflicting.
public record ProductOfferingRelationResponse(

    Long productOfferingId1,
    Long productOfferingId2,
    Boolean exclusive,
    Boolean active
) {

}
