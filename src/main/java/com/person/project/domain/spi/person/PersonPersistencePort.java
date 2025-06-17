package com.person.project.domain.spi.person;

import com.person.project.domain.model.person.Person;
import com.person.project.domain.model.person.PersonBasic;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PersonPersistencePort {
    Mono<Person> findByEmail(String email);
    Mono<Person> save(Person user);
    Mono<Person> findById(Long personId);
    Flux<PersonBasic> findByIds(List<Long> personIds);
}
