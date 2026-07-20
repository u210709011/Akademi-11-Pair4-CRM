package com.etiya.crm.lookupservice.mapper;

import com.etiya.crm.lookupservice.business.dtos.requests.CreateTypeValueRequest;
import com.etiya.crm.lookupservice.business.dtos.responses.TypeValueResponse;
import com.etiya.crm.lookupservice.entities.concretes.TypeValue;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TypeValueMapper {

    TypeValueResponse toResponse(TypeValue typeValue);

    TypeValue toEntity(CreateTypeValueRequest request);
}
