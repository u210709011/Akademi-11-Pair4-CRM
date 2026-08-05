package com.etiya.crm.productservice.business.dtos.requests.ProductCharacteristicValue;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductCharacteristicValueRequest {

    @NotNull(message = "Product alanı boş bırakılamaz")
    private Long productId;

    @NotNull(message = "Karakteristik alanı boş bırakılamaz")
    private Long characteristicId; //lookup

    private Long characteristicValueId; // lookup

    private String value;

    private Long statusId; // lookup
}
