package com.etiya.crm.productservice.business.dtos.requests.ProductOffering;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductOfferingRequest {

    @NotNull(message = "Product Spec Id alanı boş bırakılamaz")
    private Long productSpecId;

    @NotBlank(message = "İsim alanı boş bırakılamaz")
    @Size(max = 100, message = "İsim alanı en fazla 100 karakter içerebilir")
    private String name;

    @NotBlank(message = "Açıklama alanı boş bırakılamaz")
    @Size(max = 100, message = "Açıklama alanı en fazla 100 karakter içerebilir")
    private String descr;

    private Long parentOfferingId;

    @NotNull(message = "Durum zorunludur")
    private Long statusId;

    @NotNull(message = "Fiyat alanı boş bırakılamaz")
    @Positive(message = "Fiyat negatif olamaz")
    private BigDecimal totalPrice;
}
