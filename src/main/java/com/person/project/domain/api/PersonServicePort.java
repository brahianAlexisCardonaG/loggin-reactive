package com.person.project.domain.api;

import com.person.project.infraestructure.entrypoints.person.response.AuthenticationResponse;
import com.person.project.domain.model.person.Person;
import reactor.core.publisher.Mono;

public interface PersonServicePort {
    Mono<AuthenticationResponse> registerPerson(Person person);
    Mono<AuthenticationResponse> authenticatePerson(Person person);
}
