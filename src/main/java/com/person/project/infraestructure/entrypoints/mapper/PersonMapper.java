package com.person.project.infraestructure.entrypoints.mapper;

import com.person.project.domain.model.Person;
import com.person.project.infraestructure.entrypoints.dto.AuthenticatePersonDto;
import com.person.project.infraestructure.entrypoints.dto.RegisterPersonDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonMapper {
    Person toPersonRegister(RegisterPersonDto RegisterPersonDto);
    Person toPersonAuthenticate(AuthenticatePersonDto authenticatePersonDto);
    RegisterPersonDto toPersonDto(Person Person);
}
