package com.person.project.infraestructure.adapters.pesistenceadapter.person;

import com.person.project.domain.model.person.Person;
import com.person.project.domain.model.person.PersonBasic;
import com.person.project.domain.spi.person.PersonPersistencePort;
import com.person.project.infraestructure.adapters.pesistenceadapter.person.mapper.PersonEntityMapper;
import com.person.project.infraestructure.adapters.pesistenceadapter.person.repository.PersonRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
public class PersonPersistenceAdapter implements PersonPersistencePort {
    private final PersonRepository repository;
    private final PersonEntityMapper personEntityMapper;

    @Override
    public Mono<Person> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(personEntityMapper::toPerson);
    }

    @Override
    public Mono<Person> save(Person user) {
        return repository.save(personEntityMapper.toPersonEntity(user))
                .map(personEntityMapper::toPerson);
    }

    @Override
    public Mono<Person> findById(Long personId) {
        return repository.findById(personId)
                .map(personEntityMapper::toPerson);
    }

    @Override
    public Flux<PersonBasic> findByIds(List<Long> personIds) {
        return repository.findAllById(personIds)
                .map(personEntityMapper::toPersonBasic);
    }

}
