package com.etiya.crm.productservice.business.dtos.requests.ProductRelation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRelationRequest {
    @NotNull(message = "Kaynak ürün id alanı zorunludur")
    private Long productId1;

    @NotNull(message = "Hedef ürün id alanı zorunludur")
    private Long productId2;

    @NotBlank(message = "İlişki tipi kodu zorunludur")
    private String relationTypeCode;

    @NotNull(message = "Aktiflik alanı zorunludur")
    private Boolean active;
}
