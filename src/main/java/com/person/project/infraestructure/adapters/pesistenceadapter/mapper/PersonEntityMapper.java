package com.person.project.infraestructure.adapters.pesistenceadapter.mapper;

import com.person.project.domain.model.Person;
import com.person.project.infraestructure.adapters.pesistenceadapter.entity.PersonEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonEntityMapper {
    Person toPerson(PersonEntity personEntity);
    PersonEntity toPersonEntity(Person person);
}
