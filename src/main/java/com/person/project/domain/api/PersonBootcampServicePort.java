package com.person.project.domain.api;

import com.person.project.domain.model.bootcamp.PersonListBootcamp;
import com.person.project.domain.model.bootcamp.BootcampPersonList;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PersonBootcampServicePort {
    Mono<List<PersonListBootcamp>> saveBootcampCapability(Long personId, List<Long> bootcampIds);
    Mono<BootcampPersonList> getPersonsByBootcampsByIdMaxNumberPerson();
}
