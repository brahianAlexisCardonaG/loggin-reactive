package com.person.project.domain.spi;

import com.person.project.domain.model.bootcamp.Bootcamp;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BootcampWebClientPort {
    Mono<List<Bootcamp>> getBootcampsByIds(List<Long> bootcampIds);
}
