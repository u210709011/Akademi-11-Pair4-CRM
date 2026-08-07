package com.etiya.crm.productservice.business.dtos.requests.ProductCatalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductCatalogRequest {

    @NotBlank(message = "İsim zorunludur.")
    @Size(max = 100, message = "Ad en fazla 100 karakter olabilir.")
    private String name;

    @NotBlank(message = "Aciklama zorunludur.")
    @Size(max = 100, message = "Aciklama en fazla 100 karakter olabilir.")
    private String descr;

    @NotBlank(message = "Durum kodu zorunludur.")
    private String statusCode;

    private String shortCode;

}
