package com.etiya.crm.productservice.business.dtos.responses.ProductOffering;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllProductOfferingResponse {

    private Long productOfferingId;
    private String productOfferingNo;
    private Long productSpecId;
    private String name;
    private String descr;
    private Long parentOfferingId;
    private Long statusId;
    private BigDecimal totalPrice;
}
