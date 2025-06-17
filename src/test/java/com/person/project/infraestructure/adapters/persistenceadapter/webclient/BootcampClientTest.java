package com.person.project.infraestructure.adapters.persistenceadapter.webclient;

import com.person.project.domain.model.bootcamp.Bootcamp;
import com.person.project.infraestructure.adapters.pesistenceadapter.webclient.BootcampClient;
import com.person.project.infraestructure.adapters.pesistenceadapter.webclient.mapper.BootcampResponseMapper;
import com.person.project.infraestructure.adapters.pesistenceadapter.webclient.response.api.ApiBootcampListResponse;
import com.person.project.infraestructure.adapters.pesistenceadapter.webclient.response.bootcamp.BootcampResponse;
import com.person.project.infraestructure.adapters.pesistenceadapter.webclient.util.SendTokenWebClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BootcampClientTest {
    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private BootcampResponseMapper bootcampResponseMapper;

    private BootcampClient bootcampClient;

    @BeforeEach
    public void setUp() {
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        SendTokenWebClient sendTokenWebClient = new SendTokenWebClient();
        bootcampClient = new BootcampClient(webClientBuilder, sendTokenWebClient, bootcampResponseMapper);
    }

    @Test
    public void testGetBootcampsByIds() {
        List<Long> bootcampIds = List.of(1L, 2L, 3L);
        String expectedQueryParam = bootcampIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        // Creamos un dummy BootcampResponse y lo incluimos en ApiBootcampListResponse.
        BootcampResponse dummyBootcampResponse = new BootcampResponse(1L, "Bootcamp 1", LocalDate.now(), 30);
        List<BootcampResponse> bootcampResponseList = List.of(dummyBootcampResponse);
        ApiBootcampListResponse dummyApiResponse = new ApiBootcampListResponse();
        dummyApiResponse.setData(bootcampResponseList);

        // Creamos el Bootcamp esperado a partir del dummy.
        Bootcamp expectedBootcamp = Bootcamp.builder()
                .id(dummyBootcampResponse.getId())
                .name(dummyBootcampResponse.getName())
                .releaseDate(dummyBootcampResponse.getReleaseDate())
                .duration(dummyBootcampResponse.getDuration())
                .build();

        // Indicamos al mapper que al mapear el dummyBootcampResponse retorne expectedBootcamp.
        when(bootcampResponseMapper.toDomain(dummyBootcampResponse)).thenReturn(expectedBootcamp);

        // Configuramos las llamadas del WebClient.
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ApiBootcampListResponse.class))
                .thenReturn(Mono.just(dummyApiResponse));

        Mono<List<Bootcamp>> result = bootcampClient.getBootcampsByIds(bootcampIds);

        StepVerifier.create(result)
                .expectNext(List.of(expectedBootcamp))
                .verifyComplete();

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(any(Function.class));
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(ApiBootcampListResponse.class);
    }
}
