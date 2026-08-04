package com.etiya.crm.productservice.business.dtos.requests.ProductSpecResourceSpec;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductSpecResourceSpecRequest {

    @NotNull(message = "Ürün tanımı id alanı zorunludur")
    private Long productSpecId;

    @NotNull(message = "Kaynak tanımı id alanı zorunludur")
    private Long resourceSpecId;

    @NotNull(message = "İlişki tipi id alanı zorunludur")
    private Long relationTypeId;

    @NotNull(message = "Başlangıç tarihi zorunludur")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "Durum zorunludur")
    private Long statusId;
}
