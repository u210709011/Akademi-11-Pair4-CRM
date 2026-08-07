package com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatedProductSpecResourceSpecResponse {

    private Long productSpecResourceSpecId;   // PK
    private Long productSpecId;               // ilişki → ID olarak
    private Long resourceSpecId;              // lookup, düz Long
    private Long relationTypeId;              // lookup, düz Long
    private LocalDate startDate;
    private LocalDate endDate;
    private Long statusId;
}
