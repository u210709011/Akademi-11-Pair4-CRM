package com.etiya.crm.productservice.business.dtos.requests.ProductSpecResourceSpec;

import jakarta.validation.constraints.NotBlank;
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
public class UpdateProductSpecResourceSpecRequest {

    @NotNull(message = "Ürün tanımı id alanı zorunludur")
    private Long productSpecId;

    @NotNull(message = "Kaynak tanımı id alanı zorunludur")
    private Long resourceSpecId;

    @NotBlank(message = "İlişki tipi kodu zorunludur")
    private String relationTypeCode;

    @NotNull(message = "Başlangıç tarihi zorunludur")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotBlank(message = "Durum kodu zorunludur")
    private String statusCode;
}
