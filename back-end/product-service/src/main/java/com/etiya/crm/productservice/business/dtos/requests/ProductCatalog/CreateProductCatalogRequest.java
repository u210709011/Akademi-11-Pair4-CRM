package com.etiya.crm.productservice.business.dtos.requests.ProductCatalog;

import com.etiya.crm.productservice.constants.MessageKeys;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductCatalogRequest {

    @NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    @Size(max = 100, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
    private String name;

    @NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    @Size(max = 100, message = "{" + MessageKeys.FIELD_MAX_LENGTH + "}")
    private String descr;

    @NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
    private String statusCode;

    private String shortCode;

}
