package com.etiya.crm.productservice.business.dtos.responses.ProductCatalog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedProductCatalogResponse {
    private Long productCatalogId;
    private String name;
    private String descr;
    private Long statusId;
    private String shortCode;
}