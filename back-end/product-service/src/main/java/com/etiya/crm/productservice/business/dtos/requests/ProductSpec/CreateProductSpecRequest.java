package com.etiya.crm.productservice.business.dtos.requests.ProductSpec;

import com.etiya.crm.productservice.constants.MessageKeys;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductSpecRequest {
    @NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    @Size(max = 100, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
    private String name;

    @NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    @Size(max = 100, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
    private String descr;

    @NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private String statusCode;

    @NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private Boolean dev; // büyük b ile yazılması not null için, yani boş gelmemeli bu değer gibi
}
