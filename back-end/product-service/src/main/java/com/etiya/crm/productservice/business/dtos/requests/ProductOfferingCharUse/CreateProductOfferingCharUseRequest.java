package com.etiya.crm.productservice.business.dtos.requests.ProductOfferingCharUse;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductOfferingCharUseRequest {

    @NotNull(message = "Ürün teklifi id alanı zorunludur")
    private Long productOfferingId;

    @NotNull(message = "Karakteristik id alanı zorunludur")
    private Long characteristicId;

    @NotNull(message = "Zorunluluk bilgisi zorunludur")
    private Boolean mandatory;

    @NotNull(message = "Aktiflik alanı zorunludur")
    private Boolean active;
}