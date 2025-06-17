package com.person.project.infraestructure.entrypoints.person.mapper;

import com.person.project.domain.model.person.Person;
import com.person.project.infraestructure.entrypoints.person.dto.AuthenticatePersonDto;
import com.person.project.infraestructure.entrypoints.person.dto.RegisterPersonDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PersonMapper {
    Person toPersonRegister(RegisterPersonDto RegisterPersonDto);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "age", ignore = true)
    @Mapping(target = "roleUserEnum", ignore = true)
    Person toPersonAuthenticate(AuthenticatePersonDto authenticatePersonDto);
}
