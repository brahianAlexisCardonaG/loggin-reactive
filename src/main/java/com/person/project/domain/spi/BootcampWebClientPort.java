package com.person.project.domain.spi;

import com.person.project.infraestructure.adapters.pesistenceadapter.webclient.response.api.ApiBootcampListResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BootcampWebClientPort {
    Mono<ApiBootcampListResponse> getBootcampsByIds(List<Long> bootcampIds);
}
