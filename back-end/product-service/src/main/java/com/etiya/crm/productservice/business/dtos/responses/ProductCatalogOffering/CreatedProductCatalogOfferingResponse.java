package com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatedProductCatalogOfferingResponse {

    private Long productCatalogOfferingId;   // kendi ID'si
    private Long productCatalogId;            // ilişki 1 → ID olarak
    private Long productOfferingId;           // ilişki 2 → ID olarak
    private Long statusId;

}
