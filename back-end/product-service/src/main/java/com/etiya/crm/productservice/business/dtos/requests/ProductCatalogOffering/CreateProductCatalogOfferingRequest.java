package com.etiya.crm.productservice.business.dtos.requests.ProductCatalogOffering;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductCatalogOfferingRequest {

    @NotNull(message = "Katalog id alanı zorunludur")
    private Long productCatalogId;

    @NotNull(message = "Teklif id alanı zorunludur")
    private Long productOfferingId;

    @NotNull(message = "Durum zorunludur")
    private Long statusId; // aktf pasif ?
}
