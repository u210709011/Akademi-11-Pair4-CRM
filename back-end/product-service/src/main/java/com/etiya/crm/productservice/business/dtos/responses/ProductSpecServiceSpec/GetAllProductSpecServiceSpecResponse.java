package com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllProductSpecServiceSpecResponse {

    private Long productSpecServiceSpecId;   // PK
    private Long productSpecId;              // ilişki → ID
    private Long serviceSpecId;              // lookup, düz Long
    private Long relationTypeId;             // lookup, düz Long
    private LocalDate startDate;
    private LocalDate endDate;
    private Long statusId;                   // lookup, düz Long
}
