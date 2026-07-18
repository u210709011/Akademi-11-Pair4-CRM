package com.etiya.crm.partyservice.mapper;

import com.etiya.crm.partyservice.entities.concretes.Individual;
import com.etiya.crm.shared.contracts.individual.CreateIndividualCommand;
import com.etiya.crm.shared.contracts.individual.IndividualResponse;
import com.etiya.crm.shared.contracts.individual.UpdateIndividualCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface IndividualMapper {

    @Mapping(target = "individualId", ignore = true)
    @Mapping(target = "party", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    @Mapping(target = "active", ignore = true)
    Individual toEntity(CreateIndividualCommand command);

    IndividualResponse toResponse(Individual individual);

    /** nationalId/birthDate/statusId/party KASITLI OLARAK guncellenmez - bkz. UpdateIndividualCommand. */
    @Mapping(target = "individualId", ignore = true)
    @Mapping(target = "nationalId", ignore = true)
    @Mapping(target = "birthDate", ignore = true)
    @Mapping(target = "party", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntity(UpdateIndividualCommand command, @MappingTarget Individual individual);
}
