package com.etiya.crm.productservice.business.dtos.responses.ProductSpec;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatedProductSpecResponse {

    private Long productSpecId;

    private String name;

    private String descr;

    private Long statusId;

    private boolean dev;
}
