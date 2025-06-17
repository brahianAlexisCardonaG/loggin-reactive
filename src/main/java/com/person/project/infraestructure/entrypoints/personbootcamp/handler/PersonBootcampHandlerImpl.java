package com.person.project.infraestructure.entrypoints.personbootcamp.handler;

import com.person.project.domain.api.PersonBootcampServicePort;
import com.person.project.domain.enums.TechnicalMessage;
import com.person.project.infraestructure.entrypoints.personbootcamp.dto.PersonBootcampDto;
import com.person.project.infraestructure.entrypoints.personbootcamp.mapper.PersonBootcampMapper;
import com.person.project.infraestructure.entrypoints.personbootcamp.response.ApiBootcampPersonListResponse;
import com.person.project.infraestructure.entrypoints.personbootcamp.response.ApiPersonBootcampListResponse;
import com.person.project.infraestructure.entrypoints.personbootcamp.response.PersonListBootcampResponse;
import com.person.project.infraestructure.entrypoints.personbootcamp.validation.ValidationDtoPersonBootcamp;
import com.person.project.infraestructure.entrypoints.util.error.ApplyErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.Instant;
import java.util.List;

import static com.person.project.infraestructure.entrypoints.util.Constants.PERSON_ERROR;
import static com.person.project.infraestructure.entrypoints.util.Constants.X_MESSAGE_ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersonBootcampHandlerImpl {

    private final PersonBootcampServicePort personBootcampServicePort;
    private final ValidationDtoPersonBootcamp validationDtoPersonBootcamp;
    private final PersonBootcampMapper personBootcampMapper;
    private final ApplyErrorHandler applyErrorHandler;

    public Mono<ServerResponse> createPersonRelateBootcamp(ServerRequest request) {

        Mono<ServerResponse> response = request.bodyToMono(PersonBootcampDto.class)
                .flatMap(validationDtoPersonBootcamp::validateDuplicateIds)
                .flatMap(validationDtoPersonBootcamp::validateFieldNotNullOrBlank)
                .flatMap(dto ->
                        personBootcampServicePort.saveBootcampCapability(dto.getPersonId(), dto.getBootcampIds())
                )
                .flatMap(personBootcampList -> {
                    List<PersonListBootcampResponse> mappedList = personBootcampList.stream()
                            .map(personBootcampMapper::toPersonListBootcampResponse)
                            .toList();

                    return ServerResponse.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ApiPersonBootcampListResponse.builder()
                                    .code(TechnicalMessage.PERSON_BOOTCAMP_CREATED.getMessage())
                                    .message(TechnicalMessage.PERSON_BOOTCAMP_CREATED.getMessage())
                                    .date(Instant.now().toString())
                                    .data(mappedList)
                                    .build());
                }).contextWrite(Context.of(X_MESSAGE_ID, ""))
                .doOnError(ex -> log.error(PERSON_ERROR, ex));

        return applyErrorHandler.applyErrorHandling(response);
    }

    public Mono<ServerResponse> getPersonsByBootcampsByIdMaxNumberPerson(ServerRequest request) {
        Mono<ServerResponse> response = personBootcampServicePort.getPersonsByBootcampsByIdMaxNumberPerson()
                .map(personBootcampMapper::toBootcampPersonListResponse)
                .flatMap(bootcamp -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiBootcampPersonListResponse.builder()
                                .code(TechnicalMessage.BOOTCAMP_PERSON_MAX_NUMBER_PERSONS.getCode())
                                .message(TechnicalMessage.BOOTCAMP_PERSON_MAX_NUMBER_PERSONS.getMessage())
                                .date(Instant.now().toString())
                                .data(bootcamp)
                                .build()
                        )
                )
                .contextWrite(Context.of(X_MESSAGE_ID, ""))
                .doOnError(ex -> log.error( PERSON_ERROR, ex));

        return applyErrorHandler.applyErrorHandling(response);
    }
}
