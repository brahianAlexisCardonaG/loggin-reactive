package com.person.project.infraestructure.adapters.persistenceadapter.webclient;

import com.person.project.infraestructure.adapters.pesistenceadapter.webclient.BootcampClient;
import com.person.project.infraestructure.adapters.pesistenceadapter.webclient.response.api.ApiBootcampListResponse;
import com.person.project.infraestructure.adapters.pesistenceadapter.webclient.util.SendTokenWebClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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

    private BootcampClient bootcampClient;

    @BeforeEach
    public void setUp() {
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        SendTokenWebClient sendTokenWebClient = new SendTokenWebClient();
        bootcampClient = new BootcampClient(webClientBuilder, sendTokenWebClient);
    }

    @Test
    public void testGetBootcampsByIds() {
        List<Long> bootcampIds = List.of(1L, 2L, 3L);
        String expectedQueryParam = bootcampIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        ApiBootcampListResponse dummyResponse = new ApiBootcampListResponse(); // Mock de respuesta

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ApiBootcampListResponse.class)).thenReturn(Mono.just(dummyResponse));

        Mono<ApiBootcampListResponse> result = bootcampClient.getBootcampsByIds(bootcampIds);

        StepVerifier.create(result)
                .expectNext(dummyResponse)
                .verifyComplete();

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(any(Function.class));
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(ApiBootcampListResponse.class);
    }
}
