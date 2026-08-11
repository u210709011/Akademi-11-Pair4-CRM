package com.etiya.crm.productservice.business.dtos.requests.ProductOfferingCharUse;

import com.etiya.crm.productservice.constants.MessageKeys;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductOfferingCharUseRequest {

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private Long productOfferingId;

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private Long characteristicId;

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private Boolean mandatory;

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private Boolean active;
}
