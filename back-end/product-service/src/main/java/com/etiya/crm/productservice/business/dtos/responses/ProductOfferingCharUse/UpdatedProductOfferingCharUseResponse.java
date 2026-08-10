package com.etiya.crm.productservice.business.dtos.responses.ProductOfferingCharUse;

import com.etiya.crm.productservice.entities.concretes.ProductOfferingCharUse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedProductOfferingCharUseResponse{
    private Long productOfferingCharUseId;
    private Long productOfferingId;
    private Long characteristicId;
    private String characteristicName;
    private Boolean mandatory;
    private Boolean active;
}