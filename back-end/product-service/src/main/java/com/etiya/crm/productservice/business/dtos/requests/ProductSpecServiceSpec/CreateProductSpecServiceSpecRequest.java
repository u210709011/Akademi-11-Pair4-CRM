package com.etiya.crm.productservice.business.dtos.requests.ProductSpecServiceSpec;

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
public class CreateProductSpecServiceSpecRequest {

    @NotNull(message = "Ürün tanımı id alanı zorunludur")
    private Long productSpecId;

    @NotNull(message = "Servis tanımı id alanı zorunludur")
    private Long serviceSpecId;

    @NotNull(message = "İlişki tipi id alanı zorunludur")
    private Long relationTypeId;

    @NotNull(message = "Başlangıç tarihi zorunludur")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "Durum zorunludur")
    private Long statusId;
}
