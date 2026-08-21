package com.etiya.crm.orderservice.clients.responses;

// FR-014 ACC-012/BR-04: sepetteki tekliflerin birbiriyle cakisip cakismadigini (EXCL) kontrol
// etmek icin - bkz. BasketValidationRules.ensureNoConflictingItems/ensureItemNotConflicting.
// FR-014 ACC-003/004: mandatory - product-service bunu zaten donuyordu ama bu record'ta alan
// olmadigi icin Jackson sessizce atiyordu; CustOrdManager.expandWithMandatoryOfferings bunu
// kullanir.
public record ProductOfferingRelationResponse(

    Long productOfferingId1,
    Long productOfferingId2,
    Boolean mandatory,
    Boolean exclusive,
    Boolean active
) {

}
