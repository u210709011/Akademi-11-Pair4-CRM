package com.etiya.crm.productservice.business.dtos.requests.ProductRelation;

import com.etiya.crm.productservice.constants.MessageKeys;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRelationRequest {
    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private Long productId1;

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private Long productId2;

    @NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private String relationTypeCode;

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private Boolean active;
}
