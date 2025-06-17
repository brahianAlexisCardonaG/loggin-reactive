package com.person.project.infraestructure.entrypoints.personbootcamp;

import com.person.project.infraestructure.entrypoints.personbootcamp.dto.PersonBootcampDto;
import com.person.project.infraestructure.entrypoints.personbootcamp.handler.PersonBootcampHandlerImpl;
import com.person.project.infraestructure.entrypoints.personbootcamp.response.PersonListBootcampResponse;
import com.person.project.infraestructure.entrypoints.util.ApiResponseBase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.person.project.infraestructure.entrypoints.util.Constants.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
@Tag(name = "Person", description = "API Person")
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class RouterRestPersonBootcamp {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = PATH_PERSON_BOOTCAMP_CREATE,
                    produces = {"application/json"},
                    method = org.springframework.web.bind.annotation.RequestMethod.POST,
                    beanClass = PersonBootcampHandlerImpl.class,
                    beanMethod = "createPersonRelateBootcamp",
                    operation = @Operation(
                            operationId = "createPersonRelateBootcamp",
                            summary = "Associate person with a bootcamps",
                            tags = {"Endpoints for Persons"},
                            security = @SecurityRequirement(name = "BearerAuth"),
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = PersonBootcampDto.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Relation created",
                                            content = @Content(schema = @Schema(implementation = PersonListBootcampResponse.class))
                                    ),
                                    @ApiResponse(responseCode = "401", description = "Unauthorized")
                            }
                    )
            ),
            @RouterOperation(
                    path = PATH_BOOTCAMP_PERSON_GET_MAX_NUMBER_PERSON,
                    produces = { "application/json" },
                    method = RequestMethod.GET,
                    beanClass = PersonBootcampHandlerImpl.class,
                    beanMethod = "getPersonsByBootcampsByIdMaxNumberPerson",
                    operation = @Operation(
                            operationId = "getPersonsByBootcampsByIdMaxNumberPerson",
                            summary = "Get bootcampId and persons by bootcamp with max numbers of persons registers",
                            tags = { "Endpoints webclients" },
                            security = @SecurityRequirement(name = "BearerAuth"),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Bootcamps found",
                                            content = @Content(schema = @Schema(implementation = ApiResponseBase.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid query parameters"
                                    ),
                                    @ApiResponse(responseCode = "401", description = "Unauthorized")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunctionPersonBootcamp(PersonBootcampHandlerImpl personBootcampHandler) {
        return RouterFunctions
                .route(POST(PATH_PERSON_BOOTCAMP_CREATE),
                        personBootcampHandler::createPersonRelateBootcamp)
                .andRoute(GET(PATH_BOOTCAMP_PERSON_GET_MAX_NUMBER_PERSON),
                        personBootcampHandler::getPersonsByBootcampsByIdMaxNumberPerson);
    }
}