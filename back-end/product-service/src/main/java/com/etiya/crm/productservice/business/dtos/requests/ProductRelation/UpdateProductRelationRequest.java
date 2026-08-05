package com.etiya.crm.productservice.business.dtos.requests.ProductRelation;

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

    @NotNull(message = "İlişki tipi id alanı zorunludur")
    private Long relationTypeId;

    @NotNull(message = "Aktiflik alanı zorunludur")
    private Boolean active;
}
