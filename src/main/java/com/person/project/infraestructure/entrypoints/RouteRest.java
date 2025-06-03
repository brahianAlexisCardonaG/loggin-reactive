package com.person.project.infraestructure.entrypoints;

import com.person.project.infraestructure.entrypoints.handler.PersonHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class RouteRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(PersonHandlerImpl personHandler) {
        return RouterFunctions
                .route(POST("api/v1/auth/register"), personHandler::register)
                .andRoute(POST("api/v1/auth/authenticate"), personHandler::authenticate);
    }
}
