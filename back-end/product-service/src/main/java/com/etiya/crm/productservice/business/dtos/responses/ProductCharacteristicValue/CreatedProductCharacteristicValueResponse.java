package com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatedProductCharacteristicValueResponse {
    private Long productCharacteristicValueId;
    private Long productId;
    private Long characteristicId;
    private Long characteristicValueId;
    private String value;
    private Long statusId;

}
