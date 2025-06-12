package com.person.project.infraestructure.entrypoints.person;

import com.person.project.infraestructure.entrypoints.person.dto.AuthenticatePersonDto;
import com.person.project.infraestructure.entrypoints.person.dto.RegisterPersonDto;
import com.person.project.infraestructure.entrypoints.person.handler.PersonHandlerImpl;
import com.person.project.infraestructure.entrypoints.util.ApiResponseBase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.person.project.infraestructure.entrypoints.util.Constants.PATH_AUTH_AUTHENTICATION_PERSON;
import static com.person.project.infraestructure.entrypoints.util.Constants.PATH_AUTH_REGISTER_PERSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
@Tag(name = "Person", description = "API Person")
public class RouterRestPerson {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = PATH_AUTH_REGISTER_PERSON,
                    produces = {"application/json"},
                    method = org.springframework.web.bind.annotation.RequestMethod.POST,
                    beanClass = PersonHandlerImpl.class,
                    beanMethod = "register",
                    operation = @Operation(
                            operationId = "register",
                            summary = "Register person with ROLE ADMIN or USER",
                            tags = { "Endpoints for Persons" },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = RegisterPersonDto.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Register success",
                                            content = @Content(schema = @Schema(implementation = ApiResponseBase.class))
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = PATH_AUTH_AUTHENTICATION_PERSON,
                    produces = {"application/json"},
                    method = org.springframework.web.bind.annotation.RequestMethod.POST,
                    beanClass = PersonHandlerImpl.class,
                    beanMethod = "authenticate",
                    operation = @Operation(
                            operationId = "authenticate",
                            summary = "authenticate person with ROLE ADMIN or USER",
                            tags = { "Endpoints for Persons" },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = AuthenticatePersonDto.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "authenticate success",
                                            content = @Content(schema = @Schema(implementation = ApiResponseBase.class))
                                    )
                            }
                    )
            ),
    })
    public RouterFunction<ServerResponse> routerFunctionPerson(PersonHandlerImpl personHandler) {
        return RouterFunctions
                .route(POST(PATH_AUTH_REGISTER_PERSON), personHandler::register)
                .andRoute(POST(PATH_AUTH_AUTHENTICATION_PERSON), personHandler::authenticate);
    }
}
