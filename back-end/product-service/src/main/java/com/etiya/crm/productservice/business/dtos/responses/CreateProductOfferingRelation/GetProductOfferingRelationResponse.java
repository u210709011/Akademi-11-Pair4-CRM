package com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetProductOfferingRelationResponse {
    private Long productOfferingRelationId;
    private Long productOfferingId1;
    private Long productOfferingId2;
    private Long relationTypeId;
    private Integer qty;
    private Boolean active;
}