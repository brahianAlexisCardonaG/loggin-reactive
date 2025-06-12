package com.person.project.domain.api;

import com.person.project.domain.model.bootcamp.PersonListBootcamp;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PersonBootcampServicePort {
    Mono<List<PersonListBootcamp>> saveBootcampCapability(Long personId, List<Long> bootcampIds);
}
