package com.person.project.infraestructure.entrypoints.handler;

import com.person.project.domain.api.PersonServicePort;
import com.person.project.domain.enums.TechnicalMessage;
import com.person.project.domain.exception.BusinessException;
import com.person.project.domain.model.Person;
import com.person.project.infraestructure.entrypoints.dto.AuthenticatePersonDto;
import com.person.project.infraestructure.entrypoints.dto.RegisterPersonDto;
import com.person.project.infraestructure.entrypoints.mapper.PersonMapper;
import com.person.project.infraestructure.entrypoints.util.ApiResponse;
import com.person.project.infraestructure.entrypoints.util.BuildErrorResponse;
import com.person.project.infraestructure.entrypoints.util.ErrorDto;
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
public class PersonHandlerImpl {
    private final PersonServicePort personServicePort;
    private final PersonMapper personMapper;
    private final BuildErrorResponse buildErrorRes;


    public Mono<ServerResponse> register(ServerRequest request) {
        String messageId = "4345345";
        // Se lee el body y se transforma al modelo de dominio
        return request.bodyToMono(RegisterPersonDto.class)
                .flatMap(req -> {
                    Person person = personMapper.toPersonRegister(req);
                    // Llama al servicio reactivo para registrar el usuario y generar el token
                    return personServicePort.registerPerson(person);
                })
                .flatMap(authResponse ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ApiResponse.builder()
                                        .code(TechnicalMessage.USER_CREATED.getCode())
                                        .message(TechnicalMessage.USER_CREATED.getMessage())
                                        .identifier(messageId)
                                        .date(Instant.now().toString())
                                        .data(authResponse)
                                        .build()))
                .contextWrite(Context.of(X_MESSAGE_ID, messageId))
                .doOnError(ex -> log.error(PERSON_ERROR, ex))
                .onErrorResume(BusinessException.class, ex -> buildErrorRes.buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        messageId,
                        TechnicalMessage.INVALID_PARAMETERS,
                        List.of(ErrorDto.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())
                ))
                .onErrorResume(ex-> {
                    log.error("Unexpected error occurred for messageId: {}", messageId, ex);
                    return buildErrorRes.buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            messageId,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDto.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build()
                            )
                    );
                });
    }

    public Mono<ServerResponse> authenticate(ServerRequest request) {
        String messageId = "234235235";
        return request.bodyToMono(AuthenticatePersonDto.class)
                .flatMap(req -> {
                    Person person = personMapper.toPersonAuthenticate(req);
                    // Llama al servicio reactivo para autenticar, el cual genera el token JWT
                    return personServicePort.authenticatePerson(person);
                })
                .flatMap(authResponse ->
                        ServerResponse.status(HttpStatus.OK)
                                .bodyValue(ApiResponse.builder()
                                        .code(TechnicalMessage.USER_LOGGED.getCode())
                                        .message(TechnicalMessage.USER_LOGGED.getMessage())
                                        .identifier(messageId)
                                        .date(Instant.now().toString())
                                        .data(authResponse)
                                        .build()))
                .contextWrite(Context.of(X_MESSAGE_ID, messageId))
                .doOnError(ex -> log.error(PERSON_ERROR, ex))
                .onErrorResume(BusinessException.class, ex -> buildErrorRes.buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        messageId,
                        TechnicalMessage.INVALID_PARAMETERS,
                        List.of(ErrorDto.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())
                ))
                .onErrorResume(ex-> {
                    log.error("Unexpected error occurred for messageId: {}", messageId, ex);
                    return buildErrorRes.buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            messageId,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDto.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build()
                            )
                    );
                });
    }
}
