package com.etiya.crm.productservice.business.dtos.requests.ProductOfferingRelation;

import com.etiya.crm.productservice.constants.MessageKeys;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductOfferingRelationRequest {

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private Long productOfferingId1;

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private Long productOfferingId2;

    @NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private String relationTypeCode;

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    @Positive(message = "{" + MessageKeys.FIELD_POSITIVE + "}")
    private Integer qty;

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private Boolean active;
}
