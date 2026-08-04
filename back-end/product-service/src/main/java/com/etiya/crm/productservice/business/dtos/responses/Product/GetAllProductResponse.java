package com.etiya.crm.productservice.business.dtos.responses.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllProductResponse {
    private Long productId;              // PK
    private Long parentProductId;        // ilişki → ID (opsiyonel, null olabilir)
    private Long productOfferingId;      // ilişki → ID
    private Long productSpecId;          // ilişki → ID
    private String name;
    private String descr;
    private Long campaignId;             // ilişki → ID (opsiyonel, null olabilir)
    private Long statusId;
}
