package com.etiya.crm.lookupservice.mapper;

import com.etiya.crm.shared.contracts.lookup.LookupValueResponse;
import com.etiya.crm.lookupservice.entities.concretes.Lookup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LookupMapper {

    @Mapping(source = "valueId", target = "id")
    @Mapping(source = "lookUpValue", target = "value")
    LookupValueResponse toResponse(Lookup lookup);
}
