package com.etiya.crm.productservice.business.dtos.requests.ProductCharacteristicValue;

import com.etiya.crm.productservice.constants.MessageKeys;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductCharacteristicValueRequest {

    @NotNull(message = "{" + MessageKeys.PRODUCT_ID_REQUIRED + "}")
    private Long productId;

    @NotNull(message = "{" + MessageKeys.CHARACTERISTIC_ID_REQUIRED + "}")
    private Long characteristicId; //lookup

    private Long characteristicValueId; // lookup

    private String value;

    private String statusCode; // lookup, opsiyonel — null kalabilir
}
