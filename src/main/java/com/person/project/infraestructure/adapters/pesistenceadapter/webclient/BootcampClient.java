package com.person.project.infraestructure.adapters.pesistenceadapter.webclient;

import com.person.project.domain.spi.BootcampWebClientPort;
import com.person.project.infraestructure.adapters.pesistenceadapter.webclient.response.api.ApiBootcampListResponse;
import com.person.project.infraestructure.adapters.pesistenceadapter.webclient.util.ErrorsWebClient;
import com.person.project.infraestructure.adapters.pesistenceadapter.webclient.util.SendTokenWebClient;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BootcampClient implements BootcampWebClientPort {

    private final WebClient webClient;

    public BootcampClient(WebClient.Builder builder, SendTokenWebClient sendTokenWebClient) {
        this.webClient = builder.baseUrl("http://localhost:8083")
                .filter(sendTokenWebClient.authHeaderFilter())
                .build();
    }

    @Override
    public Mono<ApiBootcampListResponse> getBootcampsByIds(List<Long> bootcampIds) {
        String idsParam = bootcampIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("api/v1/bootcamp/by-ids")
                        .queryParam("bootcampIds", idsParam)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        ErrorsWebClient.handleError(response.bodyToMono(String.class)))
                .bodyToMono(ApiBootcampListResponse.class);
    }
}
