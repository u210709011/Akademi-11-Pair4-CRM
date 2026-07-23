package com.etiya.crm.lookupservice.mapper;

import com.etiya.crm.shared.contracts.typevalue.CreateTypeValueRequest;
import com.etiya.crm.shared.contracts.typevalue.TypeValueResponse;
import com.etiya.crm.lookupservice.entities.concretes.TypeValue;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TypeValueMapper {

    TypeValueResponse toResponse(TypeValue typeValue);

    TypeValue toEntity(CreateTypeValueRequest request);
}
