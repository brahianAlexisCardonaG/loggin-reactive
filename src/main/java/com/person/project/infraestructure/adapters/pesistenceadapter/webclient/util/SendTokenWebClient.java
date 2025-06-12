package com.person.project.infraestructure.adapters.pesistenceadapter.webclient.util;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@Component
public class SendTokenWebClient {

    public ExchangeFilterFunction authHeaderFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest ->
                ReactiveSecurityContextHolder.getContext()
                        .map(securityContext -> securityContext.getAuthentication())
                        .flatMap(authentication -> {
                            Object credentials = authentication.getCredentials();
                            String token;
                            if (credentials instanceof String) {
                                token = (String) credentials;
                            } else if (credentials instanceof org.springframework.security.oauth2.jwt.Jwt) {
                                token = ((org.springframework.security.oauth2.jwt.Jwt) credentials).getTokenValue();
                            } else {
                                token = "";
                            }
                            System.out.println("Token obtenido: " + token);
                            if (!token.isEmpty()) {
                                return Mono.just(ClientRequest.from(clientRequest)
                                        .headers(headers -> headers.setBearerAuth(token))
                                        .build());
                            }
                            return Mono.just(clientRequest);
                        })
                        .defaultIfEmpty(clientRequest)
        );
    }
}
