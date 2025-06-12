package com.person.project.infraestructure.adapters.pesistenceadapter.person.mapper;

import com.person.project.domain.model.Person;
import com.person.project.infraestructure.adapters.pesistenceadapter.person.entity.PersonEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonEntityMapper {
    Person toPerson(PersonEntity personEntity);
    PersonEntity toPersonEntity(Person person);
}
