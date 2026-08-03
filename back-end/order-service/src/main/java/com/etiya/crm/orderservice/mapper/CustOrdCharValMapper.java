package com.etiya.crm.orderservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.etiya.crm.orderservice.business.dtos.requests.ProdCharValRequest;
import com.etiya.crm.orderservice.business.dtos.responses.ProdCharValResponse;
import com.etiya.crm.orderservice.entities.concretes.CustOrdCharVal;

@Mapper(componentModel = "spring")
public interface CustOrdCharValMapper {

    @Mapping(target = "custOrdCharValId", ignore = true)
    @Mapping(target = "custOrd", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    CustOrdCharVal toEntity(ProdCharValRequest request);
    /*charId, charValId,val ortak otomatik eşleşir */

    ProdCharValResponse toResponse(CustOrdCharVal entity);

}
