package com.etiya.crm.productservice.business.dtos.requests.ProductOffering;

import com.etiya.crm.productservice.constants.MessageKeys;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductOfferingRequest {

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private Long productSpecId;

    @NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    @Size(max = 100, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
    private String name;

    @NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    @Size(max = 100, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
    private String descr;

    private Long parentOfferingId;

    @NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private String statusCode;

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    @Positive(message = "{" + MessageKeys.FIELD_NON_NEGATIVE + "}")
    private BigDecimal totalPrice;
}
