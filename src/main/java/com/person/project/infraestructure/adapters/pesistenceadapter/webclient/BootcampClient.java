package com.person.project.infraestructure.adapters.pesistenceadapter.webclient;

import com.person.project.domain.model.bootcamp.Bootcamp;
import com.person.project.domain.spi.bootcamp.BootcampWebClientPort;
import com.person.project.infraestructure.adapters.pesistenceadapter.webclient.mapper.BootcampResponseMapper;
import com.person.project.infraestructure.adapters.pesistenceadapter.webclient.response.api.ApiBootcampListResponse;
import com.person.project.infraestructure.adapters.pesistenceadapter.webclient.util.ErrorsWebClient;
import com.person.project.infraestructure.adapters.pesistenceadapter.webclient.util.SendTokenWebClient;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static com.person.project.infraestructure.adapters.pesistenceadapter.webclient.util.ConstantsWebClient.LOCALHOST_BOOTCAMP;
import static com.person.project.infraestructure.adapters.pesistenceadapter.webclient.util.ConstantsWebClient.PATH_GET_BOOTCAMPS_BY_IDS;

@Service
public class BootcampClient implements BootcampWebClientPort {

    private final WebClient webClient;
    private final BootcampResponseMapper bootcampResponseMapper;

    public BootcampClient(WebClient.Builder builder,
                          SendTokenWebClient sendTokenWebClient,
                          BootcampResponseMapper bootcampResponseMapper
    ) {
        this.webClient = builder.baseUrl(LOCALHOST_BOOTCAMP)
                .filter(sendTokenWebClient.authHeaderFilter())
                .build();
        this.bootcampResponseMapper = bootcampResponseMapper;
    }

    @Override
    public Mono<List<Bootcamp>> getBootcampsByIds(List<Long> bootcampIds) {
        String idsParam = bootcampIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(PATH_GET_BOOTCAMPS_BY_IDS)
                        .queryParam("bootcampIds", idsParam)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        ErrorsWebClient.handleError(response.bodyToMono(String.class)))
                .bodyToMono(ApiBootcampListResponse.class)
                .flatMapMany(response -> Flux.fromIterable(response.getData()))
                .map(bootcampResponseMapper::toDomain)
                .collectList();
    }
}
