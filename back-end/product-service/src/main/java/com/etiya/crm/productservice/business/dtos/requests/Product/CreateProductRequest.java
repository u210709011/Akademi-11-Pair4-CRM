package com.etiya.crm.productservice.business.dtos.requests.Product;

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
public class CreateProductRequest {

    private Long parentProductId;

    @NotNull(message = "Ürün Teklif alanı boş bırakılamaz")
    private Long productOfferingId;

    @NotNull(message = "Ürün Tanım alanı boş bırakılamaz")
    private Long productSpecId;

    private String name;

    private String descr;

    private Long campaignId;

    @NotBlank(message = "Durum kodu zorunludur.")
    private String statusCode;
}
