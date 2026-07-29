package com.etiya.crm.productservice.business.dtos.responses.ProductCatalog;

import com.etiya.crm.productservice.entities.concretes.ProductCatalog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatedProductCatalogResponse {
    private Long productCatalogId;
    private String name;
    private String descr;
    private Long statusId;
    private String shortCode;
}