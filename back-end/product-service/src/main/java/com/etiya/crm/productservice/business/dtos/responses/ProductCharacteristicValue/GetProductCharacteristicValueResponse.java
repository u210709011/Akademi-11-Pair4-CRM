package com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetProductCharacteristicValueResponse {
    private Long productCharacteristicValueId;   // PK
    private String characteristicName;
    private Long productId;                       // ilişki → ID
    private Long characteristicId;                // lookup, düz Long
    private Long characteristicValueId;           // lookup, düz Long (opsiyonel, null olabilir)
    private String characteristicValueName;
    private String value;                         // düz String (opsiyonel)
    private Long statusId;                        // lookup
}
