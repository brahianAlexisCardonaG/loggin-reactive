package com.person.project.domain.spi;

import com.person.project.domain.model.Person;
import reactor.core.publisher.Mono;

public interface PersonPersistencePort {
    Mono<Person> findByEmail(String email);
    Mono<Person> save(Person user);
    Mono<Person> findById(Long personId);

}
