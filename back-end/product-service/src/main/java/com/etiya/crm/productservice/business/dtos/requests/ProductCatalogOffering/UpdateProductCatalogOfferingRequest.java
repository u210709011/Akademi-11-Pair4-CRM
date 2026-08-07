package com.etiya.crm.productservice.business.dtos.requests.ProductCatalogOffering;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductCatalogOfferingRequest {
    @NotNull(message = "Katalog id alanı zorunludur")
    private Long productCatalogId;

    @NotNull(message = "Teklif id alanı zorunludur")
    private Long productOfferingId;

    @NotBlank(message = "Durum kodu zorunludur")
    private String statusCode; // aktf pasif ?
}
