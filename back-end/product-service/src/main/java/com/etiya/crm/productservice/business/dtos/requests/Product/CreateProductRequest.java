package com.etiya.crm.productservice.business.dtos.requests.Product;

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
public class CreateProductRequest {

    private Long parentProductId;

    @NotNull(message = "{" + MessageKeys.PRODUCT_OFFERING_ID_REQUIRED + "}")
    private Long productOfferingId;

    @NotNull(message = "{" + MessageKeys.PRODUCT_SPEC_ID_REQUIRED + "}")
    private Long productSpecId;

    private String name;

    private String descr;

    private Long campaignId;

    @NotBlank(message = "{" + MessageKeys.STATUS_CODE_REQUIRED + "}")
    private String statusCode;
}
