package com.person.project.application.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import static com.person.project.domain.enums.RoleUserEnum.ADMIN;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private static final String[] WHITE_LIST_URL = {
            "/api/v1/auth/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/webjars/**"
    };

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final ServerLogoutHandler logoutHandler;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(WHITE_LIST_URL).permitAll()
                        .pathMatchers("/api/v1/person/bootcamp").hasAnyRole("ADMIN","USER")
                        .pathMatchers("/api/v1/person/bootcamp/**").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                .authenticationManager(reactiveAuthenticationManager)
                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .logoutHandler(logoutHandler)
                        .logoutSuccessHandler((exchange, authentication) -> {
                            return Mono.empty();
                        })
                );
        return http.build();
    }
}
