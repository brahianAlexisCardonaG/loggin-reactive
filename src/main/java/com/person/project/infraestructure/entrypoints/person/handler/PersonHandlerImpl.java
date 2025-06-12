package com.person.project.infraestructure.entrypoints.person.handler;

import com.person.project.domain.api.PersonServicePort;
import com.person.project.domain.enums.TechnicalMessage;
import com.person.project.domain.model.Person;
import com.person.project.infraestructure.entrypoints.person.dto.AuthenticatePersonDto;
import com.person.project.infraestructure.entrypoints.person.dto.RegisterPersonDto;
import com.person.project.infraestructure.entrypoints.person.mapper.PersonMapper;
import com.person.project.infraestructure.entrypoints.util.ApiResponseBase;
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

import static com.person.project.infraestructure.entrypoints.util.Constants.PERSON_ERROR;
import static com.person.project.infraestructure.entrypoints.util.Constants.X_MESSAGE_ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersonHandlerImpl {
    private final PersonServicePort personServicePort;
    private final PersonMapper personMapper;
    private final ApplyErrorHandler applyErrorHandler;

    public Mono<ServerResponse> register(ServerRequest request) {
        // Se lee el body y se transforma al modelo de dominio
        Mono<ServerResponse> response = request.bodyToMono(RegisterPersonDto.class)
                .flatMap(req -> {
                    Person person = personMapper.toPersonRegister(req);
                    // Llama al servicio reactivo para registrar el usuario y generar el token
                    return personServicePort.registerPerson(person);
                })
                .flatMap(authResponse ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ApiResponseBase.builder()
                                        .code(TechnicalMessage.PERSON_CREATED.getCode())
                                        .message(TechnicalMessage.PERSON_CREATED.getMessage())
                                        .date(Instant.now().toString())
                                        .data(authResponse)
                                        .build()))
                .contextWrite(Context.of(X_MESSAGE_ID, ""))
                .doOnError(ex -> log.error(PERSON_ERROR, ex));

        return applyErrorHandler.applyErrorHandling(response);
    }

    public Mono<ServerResponse> authenticate(ServerRequest request) {
        Mono<ServerResponse> response = request.bodyToMono(AuthenticatePersonDto.class)
                .flatMap(req -> {
                    Person person = personMapper.toPersonAuthenticate(req);
                    // Llama al servicio reactivo para autenticar, el cual genera el token JWT
                    return personServicePort.authenticatePerson(person);
                })
                .flatMap(authResponse ->
                        ServerResponse.status(HttpStatus.OK)
                                .bodyValue(ApiResponseBase.builder()
                                        .code(TechnicalMessage.PERSON_LOGGED.getCode())
                                        .message(TechnicalMessage.PERSON_LOGGED.getMessage())
                                        .date(Instant.now().toString())
                                        .data(authResponse)
                                        .build()))
                .contextWrite(Context.of(X_MESSAGE_ID, ""))
                .doOnError(ex -> log.error(PERSON_ERROR, ex));

        return applyErrorHandler.applyErrorHandling(response);
    }
}
