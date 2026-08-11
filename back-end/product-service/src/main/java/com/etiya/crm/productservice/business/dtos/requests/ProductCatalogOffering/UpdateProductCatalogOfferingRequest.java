package com.etiya.crm.productservice.business.dtos.requests.ProductCatalogOffering;

import com.etiya.crm.productservice.constants.MessageKeys;
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
public class UpdateProductCatalogOfferingRequest {
    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private Long productCatalogId;

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private Long productOfferingId;

    @NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private String statusCode; // aktf pasif ?
}
