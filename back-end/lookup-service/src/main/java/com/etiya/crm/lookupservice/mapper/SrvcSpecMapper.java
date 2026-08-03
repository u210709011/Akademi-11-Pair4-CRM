package com.etiya.crm.lookupservice.mapper;

import com.etiya.crm.shared.contracts.srvcspec.CreateSrvcSpecRequest;
import com.etiya.crm.shared.contracts.srvcspec.SrvcSpecResponse;
import com.etiya.crm.lookupservice.entities.concretes.SrvcSpec;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SrvcSpecMapper {

    SrvcSpecResponse toResponse(SrvcSpec srvcSpec);

    SrvcSpec toEntity(CreateSrvcSpecRequest request);
}
