package com.etiya.crm.productservice.business.dtos.responses.ProductRelation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedProductRelationResponse {
    private Long productRelationId;   // PK
    private Long productId1;          // ilişki 1 → ID
    private Long productId2;          // ilişki 2 → ID
    private Long relationTypeId;      // lookup, düz Long
    private boolean active;           // response'ta küçük boolean
}
