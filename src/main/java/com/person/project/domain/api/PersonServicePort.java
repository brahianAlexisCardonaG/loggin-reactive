package com.person.project.domain.api;

import com.person.project.domain.model.AuthenticationResponse;
import com.person.project.domain.model.Person;
import reactor.core.publisher.Mono;

public interface PersonServicePort {
    Mono<AuthenticationResponse> registerPerson(Person person);
    Mono<AuthenticationResponse> authenticatePerson(Person person);
}
