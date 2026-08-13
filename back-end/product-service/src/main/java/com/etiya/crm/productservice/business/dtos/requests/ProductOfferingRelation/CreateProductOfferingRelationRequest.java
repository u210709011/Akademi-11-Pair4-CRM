package com.etiya.crm.productservice.business.dtos.requests.ProductOfferingRelation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductOfferingRelationRequest {

    @NotNull(message = "Kaynak ürün teklifi id alanı zorunludur")
    private Long productOfferingId1;

    @NotNull(message = "Hedef ürün teklifi id alanı zorunludur")
    private Long productOfferingId2;

    @NotBlank(message = "İlişki tipi kodu zorunludur")
    private String relationTypeCode;

    @NotNull(message = "Miktar alanı zorunludur")
    @Positive(message = "Miktar pozitif olmalıdır")
    private Integer qty;

    @NotNull(message = "Aktiflik alanı zorunludur")
    private Boolean active;
}