package com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAllProductCatalogOfferingResponse {
    private Long productCatalogOfferingId;   // kendi ID'si
    private Long productCatalogId;            // ilişki 1 → ID olarak
    private Long productOfferingId;           // ilişki 2 → ID olarak
    private String productOfferingName;
    private BigDecimal totalPrice;
    private Long statusId;
}
