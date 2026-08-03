package com.etiya.crm.productservice.business.dtos.requests.ProductSpec;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductSpecRequest {

    @NotBlank(message = "Ad zorunludur.")
    @Size(max = 100, message = "Ad en fazla 100 karakter olabilir.")
    private String name;

    @NotBlank(message = "Aciklama zorunludur.")
    @Size(max = 100, message = "Aciklama en fazla 100 karakter olabilir.")
    private String descr;

    @NotNull(message = "Durum zorunludur.")
    private Long statusId;

    @NotNull(message = "Gelistirme bilgisi zorunludur.")
    private Boolean dev;

}
