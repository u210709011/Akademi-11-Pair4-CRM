package com.etiya.crm.productservice.business.dtos.requests.ProductSpecServiceSpec;

import com.etiya.crm.productservice.constants.MessageKeys;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductSpecServiceSpecRequest {

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private Long productSpecId;

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private Long serviceSpecId;

    @NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private String relationTypeCode;

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private String statusCode;
}
