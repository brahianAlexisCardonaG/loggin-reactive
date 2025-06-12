package com.person.project.infraestructure.adapters.pesistenceadapter.webclient.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.person.project.domain.enums.TechnicalMessage;
import com.person.project.domain.exception.ProcessorException;
import com.person.project.infraestructure.entrypoints.util.ErrorDto;
import reactor.core.publisher.Mono;

public class ErrorsWebClient {
    private static final String BASE_URL = "http://localhost:8083";

    public static Mono<Throwable> handleError(Mono<String> errorBodyMono) {
        return errorBodyMono.flatMap(errorBody -> {
            try {
                ErrorDto errorDto = new ObjectMapper().readValue(errorBody, ErrorDto.class);
                TechnicalMessage technicalMessage = TechnicalMessage.INVALID_REQUEST;
                return Mono.error(new ProcessorException(errorDto.getMessage(), technicalMessage));
            } catch (Exception e) {
                return Mono.error(new ProcessorException("Error parsing error response: " +
                        BASE_URL  + " " + errorBody,
                        TechnicalMessage.INTERNAL_ERROR));
            }
        });
    }
}
